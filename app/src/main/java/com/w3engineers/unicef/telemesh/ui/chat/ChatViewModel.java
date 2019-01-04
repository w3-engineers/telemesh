package com.w3engineers.unicef.telemesh.ui.chat;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.List;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/10/2018 at 10:54 AM.
 *  *
 *  * Purpose: Perform message related operations
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/10/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

public class ChatViewModel extends BaseRxViewModel {
    /**
     * <h1>Instance variable scope</h1>
     */
//    private MessageDataSource messageDataSource;
    private MessageSourceData messageSourceData;
    private UserDataSource userDataSource;
    private DataSource dataSource;


    /**
     * <h1>View model constructor</h1>
     *
     * @param messageSourceData : MessageDataSource obj
     */
    public ChatViewModel(MessageSourceData messageSourceData) {
        this.messageSourceData = messageSourceData;
        userDataSource  = UserDataSource.getInstance();
        dataSource = Source.getDbSource();
    }

    /**
     * <h1>Create a stream pipeline to database</h1>
     *
     * @param meshId : friends user id
     * @return : list of message
     */
    LiveData<List<ChatEntity>> getAllMessage(String meshId) {
        return LiveDataReactiveStreams.fromPublisher(messageSourceData.getAllMessages(meshId));
    }

    /**
     * <h1>Init chatting user </h1>
     * <p>To recognize which message need to pass UI</p>
     * <p>Track read or unread message</p>
     *
     * @param userId : UserEntity obj
     */
    void setCurrentUser(String userId) {
        dataSource.setCurrentUser(userId);
    }

    /**
     * <h1>Send message on IO thread</h1>
     *
     * @param meshId        : Friends user id
     * @param message       : Message need to send
     * @param isTextMessage : is text or file
     */
    void sendMessage(String meshId, String message, boolean isTextMessage) {


        if (isTextMessage) {

            MessageEntity messageEntity = new MessageEntity()
                    .setMessage(message);

            setChatEntity(meshId, messageEntity);

            messageInsertionProcess(messageEntity);
        }
    }

    /**
     * At first this API will check the date entity is already exist or not
     * If this is the new message for today then at first insert a date message
     * then insert the actual message
     * we didn't call to ui for update a message because
     * we use live data for continous integrating chat messages
     * @param chatEntity : prepared chat data
     */

    @SuppressLint("CheckResult")
    private void messageInsertionProcess(ChatEntity chatEntity) {

        String dateFormat = TimeUtil.getDayMonthYear(chatEntity.getTime());

        Single<Boolean> dateEntitySingle = Single.fromCallable(() ->
                messageSourceData.hasChatEntityExist(chatEntity.getFriendsId(), dateFormat));

        dateEntitySingle.subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Boolean dateEntity) {
                if (!dateEntity) {

                    ChatEntity separatorMessage = new MessageEntity().setMessage(dateFormat)
                            .setTime(chatEntity.getTime())
                            .setMessageType(Constants.MessageType.DATE_MESSAGE)
                            .setFriendsId(chatEntity.getFriendsId())
                            .setMessageId(dateFormat);

                    messageSourceData.insertOrUpdateData(separatorMessage);
                }

                getCompositeDisposable().add(insertMessageData((MessageEntity) chatEntity)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    private Single<Long> insertMessageData(MessageEntity messageEntity) {
        return Single.fromCallable(() ->
                messageSourceData.insertOrUpdateData(messageEntity));
    }

    /**
     * Prepare a common chat entity for all (Text and File)
     * @param meshId
     * @param chatEntity
     */
    private void setChatEntity(String meshId, ChatEntity chatEntity) {

        chatEntity.setMessageId(UUID.randomUUID().toString())
                .setFriendsId(meshId)
                .setIncoming(false)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING);

        if (MessageEntity.class.isInstance(chatEntity)) {
            chatEntity.setMessageType(Constants.MessageType.TEXT_MESSAGE);
        }
    }

    /**
     * Mark all un read message as read
     *
     * @param friendsId : mesh id
     */
    void updateAllMessageStatus(String friendsId) {

        getCompositeDisposable().add(updateMessageSatus(friendsId)
                .subscribeOn(Schedulers.io()).subscribe());
    }

    private Single<Long> updateMessageSatus(String friendsId) {
        return Single.fromCallable(() -> messageSourceData.updateUnreadToRead(friendsId));
    }

    /**
     * This API concerned for tracking offline and online the chat current chat user
     * @param meshId
     * @return
     */
    LiveData<UserEntity> getUserById(String meshId){
        return LiveDataReactiveStreams.fromPublisher(userDataSource.getUserById(meshId));
    }

}
