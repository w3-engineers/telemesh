package com.w3engineers.unicef.telemesh.ui.chat;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.pager.ChatEntityListDataSource;
import com.w3engineers.unicef.telemesh.pager.DataSourceFactory;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    private static final int PAGE_SIZE = 10;
    private static final int PREFETCH_DISTANCE = 5;
    private LiveData<PagedList<ChatEntity>> pagedChatEntityList;


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


            ChatEntity chatEntity = prepareChatEntityForText(meshId, messageEntity);

            messageInsertionProcess(chatEntity);
        }
    }

    /**
     * This method will take steps to insert chat entity to local db.
     * we didn't call to ui for update a message because
     * we use live data for continuous integrating chat messages
     * @param chatEntity : prepared chat data
     */

    @SuppressLint("CheckResult")
    private void messageInsertionProcess(ChatEntity chatEntity) {

        /*String dateFormat = TimeUtil.getDayMonthYear(chatEntity.getTime());

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
        });*/

        getCompositeDisposable().add(insertMessageData((MessageEntity) chatEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

    }

    private Single<Long> insertMessageData(MessageEntity messageEntity) {
        return Single.fromCallable(() ->
                messageSourceData.insertOrUpdateData(messageEntity));
    }

    /**
     * Prepare a chat entity from text message.
     * @param meshId
     * @param messageEntity
     */
    private ChatEntity prepareChatEntityForText(String meshId, MessageEntity messageEntity) {


        ChatEntity chatEntity;
        chatEntity = messageEntity;

        chatEntity.setMessageId(UUID.randomUUID().toString())
                .setFriendsId(meshId)
                .setIncoming(false)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE);


        return chatEntity;
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

    public LiveData<PagedList<ChatEntity>> prepareDateSpecificChat(List<ChatEntity> chatEntityList) {

        List<ChatEntity> chatList = groupDataIntoHashMap(chatEntityList);

        ChatEntityListDataSource chatEntityListDataSource = new ChatEntityListDataSource(chatList);

        DataSourceFactory mDataSourceFactory = new DataSourceFactory(chatEntityListDataSource);


        PagedList.Config myConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setPageSize(PAGE_SIZE)
                .build();



        pagedChatEntityList = new LivePagedListBuilder<>(mDataSourceFactory, myConfig).build();

        return pagedChatEntityList;


    }


    private List<ChatEntity> groupDataIntoHashMap(List<ChatEntity> chatEntities) {

        LinkedHashMap<Long, Set<ChatEntity>> groupedHashMap = new LinkedHashMap<>();
        Set<ChatEntity> chatEntitySet = null;

        for (ChatEntity chatEntity : chatEntities) {

            if(chatEntity!= null){

                long hashMapKey = chatEntity.getTime();

                if (groupedHashMap.containsKey(hashMapKey)) {
                    // The key is already in the HashMap; add the pojo object
                    // against the existing key.
                    groupedHashMap.get(hashMapKey).add(chatEntity);
                } else {
                    // The key is not there in the HashMap; create a new key-value pair
                    chatEntitySet = new LinkedHashSet<>();
                    chatEntitySet.add(chatEntity);
                    groupedHashMap.put(hashMapKey, chatEntitySet);
                }
            }


        }

        //Generate list from map
        return generateListFromMap(groupedHashMap);

    }


    private List<ChatEntity> generateListFromMap(LinkedHashMap<Long, Set<ChatEntity>> groupedHashMap) {
        // We linearly add every item into the consolidatedList.
        Date date1 = null;
        Date date2 = null;


        date1 = TimeUtil.getInstance().getDateFromMillisecond(TimeUtil.DEFAULT_MILLISEC);



        List<ChatEntity> consolidatedList = new ArrayList<>();

        for (long millisec : groupedHashMap.keySet()) {


            date2 = TimeUtil.getInstance().getDateFromMillisecond(millisec);

            if(!TimeUtil.getInstance().isSameDay(date1, date2)){

                date1 = date2;

                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setMessageType(Constants.MessageType.DATE_MESSAGE);
                messageEntity.setTime(millisec);
                consolidatedList.add(messageEntity);

            }

            for (ChatEntity chatEntity : groupedHashMap.get(millisec)) {

                consolidatedList.add(chatEntity);
            }

        }

        return consolidatedList;
    }




}
