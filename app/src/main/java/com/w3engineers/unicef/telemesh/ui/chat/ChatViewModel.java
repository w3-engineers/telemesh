package com.w3engineers.unicef.telemesh.ui.chat;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.ChatEntityListDataSource;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;
import com.w3engineers.unicef.util.helper.model.ContentInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class ChatViewModel extends BaseRxAndroidViewModel {
    /**
     * <h1>Instance variable scope</h1>
     */

    private MessageSourceData messageSourceData;
    private UserDataSource userDataSource;
    private GroupDataSource groupDataSource;
    private DataSource dataSource;

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 70;
    private static final int PREFETCH_DISTANCE = 30;

    private boolean isGroup;

    private CompositeDisposable compositeDisposable;
    private MutableLiveData<PagedList<ChatEntity>> mutableChatList = new MutableLiveData<>();
    private MutableLiveData<Boolean> finishForGroupLeave = new MutableLiveData<>();
    private MutableLiveData<List<UserEntity>> groupAllMembers = new MutableLiveData<>();
    private MutableLiveData<List<UserEntity>> groupLiveMembers = new MutableLiveData<>();
    private MutableLiveData<List<UserEntity>> groupLiveMembersWithOutMe = new MutableLiveData<>();

    /**
     * <h1>View model constructor</h1>
     *
     *
     */
    public ChatViewModel(@NonNull Application application) {
        super(application);
        this.messageSourceData =  MessageSourceData.getInstance();

        compositeDisposable = new CompositeDisposable();
        userDataSource  = UserDataSource.getInstance();
        groupDataSource = GroupDataSource.getInstance();
        dataSource = Source.getDbSource();
    }

    void setChatPageInfo(boolean isGroup) {
        this.isGroup = isGroup;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    /**
     * <h1>Create a stream pipeline to database</h1>
     *
     * @param meshId : friends user id
     * @return : list of message
     */
    @NonNull
    public LiveData<List<ChatEntity>> getAllMessage(@NonNull String meshId) {
        return LiveDataReactiveStreams.fromPublisher(messageSourceData.getAllMessages(meshId));
    }

    @NonNull
    public LiveData<List<ChatEntity>> getAllGroupMessage(@NonNull String threadId) {
        return LiveDataReactiveStreams.fromPublisher(messageSourceData.getAllGroupMessages(threadId));
    }

    /**
     * <h1>Init chatting user </h1>
     * <p>To recognize which message need to pass UI</p>
     * <p>Track read or unread message</p>
     *
     * @param userId : UserEntity obj
     */
    public void setCurrentUser(@Nullable String userId) {
        dataSource.setCurrentUser(userId);
    }

    /**
     * This API concerned for tracking offline and online the chat current chat user
     * @param threadId -
     * @return -
     */
    @NonNull
    public LiveData<UserEntity> getUserById(@NonNull String threadId){
        return LiveDataReactiveStreams.fromPublisher(userDataSource.getUserById(threadId));
    }

    @NonNull
    public LiveData<GroupEntity> getLiveGroupById(@NonNull String threadId){
        return groupDataSource.getLiveGroupById(threadId);
    }

    public void processGroupUser(String membersInfo) {
        List<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(membersInfo);

        List<UserEntity> allMembers = new ArrayList<>();
        List<UserEntity> liveMembers = new ArrayList<>();
        List<UserEntity> liveMembersWithOutMe = new ArrayList<>();

        String myMeshId = getMyUserId();

        for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
            UserEntity userEntity = new UserEntity()
                    .setMeshId(groupMembersInfo.getMemberId())
                    .setAvatarIndex(groupMembersInfo.getAvatarPicture())
                    .setUserName(groupMembersInfo.getUserName());

            allMembers.add(userEntity);
            if (groupMembersInfo.getMemberStatus() == Constants.GroupEvent.GROUP_JOINED) {
                liveMembers.add(userEntity);
                if (!groupMembersInfo.getMemberId().equals(myMeshId)) {
                    liveMembersWithOutMe.add(userEntity);
                }
            }
        }
        groupAllMembers.postValue(allMembers);
        groupLiveMembers.postValue(liveMembers);
        groupLiveMembersWithOutMe.postValue(liveMembersWithOutMe);
    }

    public String getMyUserId() {
        return SharedPref.getSharedPref(TeleMeshApplication.getContext())
                .read(Constants.preferenceKey.MY_USER_ID);
    }

    public UserEntity getMyUserEntity() {
        int myAvatarIndex = SharedPref.getSharedPref(TeleMeshApplication.getContext())
                .readInt(Constants.preferenceKey.IMAGE_INDEX);
        return new UserEntity().setUserName("You").setMeshId(getMyUserId())
                .setAvatarIndex(myAvatarIndex);
    }

    void groupLeaveAction(GroupEntity groupEntity) {
        getCompositeDisposable().add(deleteGroupOperation(groupEntity.groupId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    groupEntity.setOwnStatus(Constants.GroupEvent.GROUP_LEAVE);
                    groupEntity.setGroupInfoId(UUID.randomUUID().toString());
                    dataSource.setGroupUserLeaveEvent(groupEntity);
                    finishForGroupLeave.postValue(true);
                }, Throwable::printStackTrace));
    }

    private Single<Integer> deleteGroupOperation(String groupId) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    emitter.onSuccess(groupDataSource.deleteGroupById(groupId));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    void clearMessage(String threadId, boolean isGroup) {
        getCompositeDisposable().add(clearMessages(threadId, isGroup)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {

                }, Throwable::printStackTrace));
    }

    private Single<Integer> clearMessages(String threadId, boolean isGroup) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    emitter.onSuccess(messageSourceData.clearMessage(threadId, isGroup));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    /**
     * <h1>Send message on IO thread</h1>
     *
     * @param threadId        : Friends user id
     * @param message       : Message need to send
     * @param isTextMessage : is text or file
     */
    public void sendMessage(@NonNull String threadId, @NonNull String message, boolean isTextMessage) {

        if (isTextMessage) {

            MessageEntity messageEntity = new MessageEntity()
                    .setMessage(message).setMessagePlace(isGroup);

            if (isGroup) {
                messageEntity.setGroupId(threadId);
                threadId = getMyUserId();
            }

            ChatEntity chatEntity = prepareChatEntityForText(threadId, messageEntity,
                    Constants.MessageType.TEXT_MESSAGE);

            messageInsertionProcess(chatEntity);
        }
    }

    public void sendContentMessage(String userId, Uri uri) {
        getCompositeDisposable().add(Single.just(ContentUtil.getInstance()
                .getFilePathFromUri(uri))
                .subscribeOn(Schedulers.newThread())
                .subscribe(contentPath ->{
                    startContentMessageProcess(userId, contentPath);
                }, throwable -> {
                    Timber.tag("FileMessage").e(throwable);
                }));
    }

    private void startContentMessageProcess(String userId, String contentPath) {
        if (ContentUtil.getInstance().isTypeImage(contentPath)) {
            // Without compress
//            sendContentMessage(userId, contentPath);
            // With compress
            compressImageContentMessage(userId, contentPath);
        } else {
            sendContentMessage(userId, contentPath);
        }
    }

    private void compressImageContentMessage(String userId, String contentPath) {
        getCompositeDisposable().add(Single.just(ContentUtil.getInstance()
                .compressImage(contentPath))
                .subscribeOn(Schedulers.newThread())
                .subscribe(compressedImagePath ->{
                    sendContentMessage(userId, compressedImagePath);
                }, throwable -> {
                    Timber.tag("FileMessage").e(throwable);
                }));
    }

    private void sendContentMessage(String userId, String path) {
        Log.v("CONTENT_BROADCAST:","IMAGE_PATH: " + path);
        getCompositeDisposable().add(getThumbnailPath(path)
                .subscribeOn(Schedulers.newThread())
                .subscribe(thumbPath -> {
                    prepareContentMessage(userId, path, thumbPath);
                }, throwable -> {
                    Timber.tag("FileMessage").e(throwable);
                }));
    }

    private Single<String> getThumbnailPath(String originalPath) {
        if (ContentUtil.getInstance().isTypeImage(originalPath)) {
            return Single.just(ContentUtil.getInstance().getThumbnailFromImagePath(originalPath));
        } else if (ContentUtil.getInstance().isTypeVideo(originalPath)) {
            return Single.just(ContentUtil.getInstance().getThumbnailFromVideoPath(originalPath));
        } else {
            return Single.just(ContentUtil.getInstance().getThumbnailFromImagePath(originalPath));
        }
    }

    public void resendContentMessage(MessageEntity messageEntity) {
        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED) {
            messageEntity.setStatus(Constants.MessageStatus.STATUS_RESEND_START);
            messageInsertionProcess(messageEntity);
            dataSource.reSendMessage(messageEntity);
        }
    }

    private void prepareContentMessage(String userId, String path, String thumbPath) {
        MessageEntity messageEntity = new MessageEntity()
                .setMessage(ContentUtil.getInstance().getContentMessageBody(path))
                .setContentPath(path)
                .setContentThumbPath(thumbPath);

        if (ContentUtil.getInstance().isTypeVideo(path)) {
            long videoDuration = ContentUtil.getInstance().getMediaDuration(path);
            ContentInfo contentInfo = new ContentInfo();
            contentInfo.setDuration(videoDuration);

            String contentInfoText = GsonBuilder.getInstance().getContentInfoJson(contentInfo);
            messageEntity.setContentInfo(contentInfoText);
        }

        ChatEntity chatEntity = prepareChatEntityForText(userId, messageEntity,
                ContentUtil.getInstance().getContentMessageType(path));

        messageInsertionProcess(chatEntity);
    }

    /**
     * This method will take steps to insert chat entity to local db.
     * we didn't call to ui for update a message because
     * we use live data for continuous integrating chat messages
     * @param chatEntity : prepared chat data
     */

    @SuppressLint("CheckResult")
    private void messageInsertionProcess(ChatEntity chatEntity) {

        compositeDisposable.add(insertMessageData((MessageEntity) chatEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Log.v("FileMessage", "Insert msg: " + aLong);
                }, throwable -> {
                    Log.v("FileMessage", "Error: " + throwable.getMessage());
                }));

    }

    private Single<Long> insertMessageData(MessageEntity messageEntity) {
        return Single.fromCallable(() ->
                messageSourceData.insertOrUpdateData(messageEntity));
    }

    /**
     * Prepare a chat entity from text message.
     * @param meshId -
     * @param messageEntity -
     */
    private ChatEntity prepareChatEntityForText(String meshId, MessageEntity messageEntity,
                                                int messageType) {

        ChatEntity chatEntity;
        chatEntity = messageEntity;

        chatEntity.setMessageId(UUID.randomUUID().toString())
                .setFriendsId(meshId)
                .setIncoming(false)
                .setTime(TimeUtil.toCurrentTime())
                .setStatus(Constants.MessageStatus.STATUS_SENDING)
                .setMessageType(messageType);

        return chatEntity;
    }

    /**
     * Mark all un read message as read
     *
     * @param friendsId : mesh id
     */
    public void updateAllMessageStatus(@NonNull String friendsId) {
        compositeDisposable.add(updateMessageStatus(friendsId)
                .subscribeOn(Schedulers.io()).subscribe(aLong -> {
                    updateAllFailedMessageStatus(friendsId);
                }, Throwable::printStackTrace));
    }

    public void updateAllFailedMessageStatus(@NonNull String friendsId) {
        compositeDisposable.add(updateFailedMessageStatus(friendsId)
                .subscribeOn(Schedulers.io()).subscribe(aLong -> {
                }, Throwable::printStackTrace));
    }

    private Single<Long> updateMessageStatus(String friendsId) {
        if (isGroup) {
            return Single.fromCallable(() -> messageSourceData.updateUnreadToReadForGroup(friendsId));
        } else {
            return Single.fromCallable(() -> messageSourceData.updateUnreadToRead(friendsId));
        }
    }

    private Single<Long> updateFailedMessageStatus(String friendsId) {
        return Single.fromCallable(() -> messageSourceData.updateUnreadToReadFailed(friendsId));
    }

    @NonNull
    public LiveData<PagedList<ChatEntity>> getChatEntityWithDate() {
        return mutableChatList;
    }

    @NonNull
    public MutableLiveData<Boolean> getFinishForGroupLeave() {
        return finishForGroupLeave;
    }

    public MutableLiveData<List<UserEntity>> getGroupAllMembers() {
        return groupAllMembers;
    }

    public MutableLiveData<List<UserEntity>> getGroupLiveMembers() {
        return groupLiveMembers;
    }

    public MutableLiveData<List<UserEntity>> getGroupLiveMembersWithOutMe() {
        return groupLiveMembersWithOutMe;
    }

    /**
     * chunk by chunk data load will be applicable.
     * From db all the chat entity will be fetched
     * but we will not display all messages at a time.
     * Custom Paging will load chunk data and update view.
     *
     * @param chatEntityList -
     */
    public void prepareDateSpecificChat(@Nullable List<ChatEntity> chatEntityList) {

        if (chatEntityList != null) {

            List<ChatEntity> chatList = groupDataIntoHashMap(chatEntityList);

            ChatEntityListDataSource chatEntityListDataSource = new ChatEntityListDataSource(chatList);

            PagedList.Config myConfig = new PagedList.Config.Builder()
                    .setEnablePlaceholders(true)
                    .setPrefetchDistance(PREFETCH_DISTANCE)
                    .setPageSize(PAGE_SIZE)
                    .build();


            PagedList<ChatEntity> pagedStrings = new PagedList.Builder<>(chatEntityListDataSource, myConfig)
                    .setInitialKey(INITIAL_LOAD_KEY)
                    .setNotifyExecutor(new MainThreadExecutor()) //The executor defining where page loading updates are dispatched.
                    .setFetchExecutor(Executors.newSingleThreadExecutor())
                    .build();

            // here mutable live data is used to pass the updated value
            mutableChatList.postValue(pagedStrings);
        }
    }

    /**
     * group chat entities according to date.
     *  Set is used to ensure the list contain unique contents.
     * @param chatEntities -
     * @return -
     */
    private List<ChatEntity> groupDataIntoHashMap(List<ChatEntity> chatEntities) {

        LinkedHashMap<Long, Set<ChatEntity>> groupedHashMap = new LinkedHashMap<>();
        Set<ChatEntity> chatEntitySet;

        for (ChatEntity chatEntity : chatEntities) {

            if(chatEntity!= null){

                long hashMapKey = chatEntity.getTime();

                if (groupedHashMap.containsKey(hashMapKey)) {
                    // The key is already in the HashMap; add the pojo object
                    // against the existing key.
                    chatEntitySet = groupedHashMap.get(hashMapKey);
                    /*if (chatEntitySet != null) {
                        chatEntitySet.add(chatEntity);
                        groupedHashMap.put(hashMapKey, chatEntitySet);
                    }*/
                } else {
                    // The key is not there in the HashMap; create a new key-value pair
                    chatEntitySet = new LinkedHashSet<>();
                }
                if (chatEntitySet != null) {
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
        Date date1;
        Date date2;


        date1 = TimeUtil.getInstance().getDateFromMillisecond(TimeUtil.DEFAULT_MILLISEC);



        List<ChatEntity> consolidatedList = new ArrayList<>();

        for (long millisecond : groupedHashMap.keySet()) {


            date2 = TimeUtil.getInstance().getDateFromMillisecond(millisecond);

            if(date1 != null && date2 != null && !TimeUtil.getInstance().isSameDay(date1, date2)){

                date1 = date2;

                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setMessageType(Constants.MessageType.DATE_MESSAGE);
                messageEntity.setTime(millisecond);
                consolidatedList.add(messageEntity);

            }
            consolidatedList.addAll(Objects.requireNonNull(groupedHashMap.get(millisecond)));
        }
        return consolidatedList;
    }

}
