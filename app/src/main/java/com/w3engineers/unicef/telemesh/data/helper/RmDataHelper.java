package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.location.Location;
import android.os.Environment;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.mesh.util.lib.mesh.DataManager;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.analytics.model.GroupCountParseModel;
import com.w3engineers.unicef.telemesh.data.analytics.parseapi.ParseConstant;
import com.w3engineers.unicef.telemesh.data.broadcast.BroadcastManager;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.AppInstaller;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdateModel;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.ShareCountModel;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupCountModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogDataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.GroupMessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageCount;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.ConnectivityUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.NotifyUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
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
public class RmDataHelper implements BroadcastManager.BroadcastSendCallback {

    private static RmDataHelper rmDataHelper = new RmDataHelper();
    public static MeshDataSource rightMeshDataSource;

    protected DataSource dataSource;


    @SuppressLint("UseSparseArrays")
    @NonNull
    public static HashMap<String, DataModel> rmDataMap = new HashMap<>();

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public RmDataHelper() {
        dataSource = Source.getDbSource();
        BroadcastManager.getInstance().setBroadcastSendCallback(this);
    }

    @NonNull
    public static RmDataHelper getInstance() {
        return rmDataHelper;
    }

    @NonNull
    public MeshDataSource initRM(@NonNull DataSource dataSource) {

        this.dataSource = dataSource;
        prepareRightMeshDataSource();

        return rightMeshDataSource;
    }

    protected void prepareRightMeshDataSource() {
        if (rightMeshDataSource == null) {
            rightMeshDataSource = MeshDataSource.getRmDataSource();
        }
    }


    // This method is developed only called from policy page
    // and should not call it from any other place in project
    @Deprecated
    public MeshDataSource startServiceFromPolicyPage(DataSource dataSource) {
        if (rightMeshDataSource == null) {
            this.dataSource = dataSource;
            rightMeshDataSource = MeshDataSource.getRmDataSource();
        } else {
            rightMeshDataSource.startTelemeshService();
        }
        return rightMeshDataSource;
    }


    /**
     * This constructor is restricted and only used in unit test class
     *
     * @param dataSource -> provide mock dataSource from unit test class
     */
    public void initSource(@NonNull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This api is responsible for insert users in database when users is added
     *
     * @param userModel -> contains all of info about users
     */

    public void userAdd(@NonNull UserModel userModel) {

        String userId = userModel.getUserId();
        if (userId == null || !userId.startsWith("0x")) {
            Timber.tag("User Add: ").e("Error id: %s", userId);
            return;
        }
        int userActiveStatus = rightMeshDataSource.getUserActiveStatus(userId);

        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        UserEntity previousEntity = UserDataSource.getInstance().getSingleUserById(userId);

        UserEntity userEntity;

        if (previousEntity != null) {

            userEntity = previousEntity
                    .toUserEntity(userModel)
                    .setOnlineStatus(userConnectivityStatus);

        } else {
            userEntity = new UserEntity()
                    .toUserEntity(userModel)
                    .setOnlineStatus(userConnectivityStatus);
        }


        UserDataSource.getInstance().insertOrUpdateData(userEntity);

//        BroadcastDataHelper.getInstance().syncBroadcastMsg(userId);
    }

    public void onDemandUserAdd(String userId) {
        if (userId == null || !userId.startsWith("0x")) {
            Timber.tag("Forward User Id: ").e("Error id: %s", userId);
            return;
        }
        UserEntity userEntity = UserDataSource.getInstance().getSingleUserById(userId);
        if (userEntity == null) {
            userEntity = new UserEntity().setMeshId(userId)
                    .setOnlineStatus(getActiveStatus(0));
            UserDataSource.getInstance().insertOrUpdateData(userEntity);
        }
    }

    public void myUserInfoAdd() {
        String userId = getMyMeshId();

        String name = SharedPref.read(Constants.preferenceKey.USER_NAME);
        String lastName = SharedPref.read(Constants.preferenceKey.LAST_NAME);
        int avatarIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
        long regTime = SharedPref.readLong(Constants.preferenceKey.MY_REGISTRATION_TIME);

        UserEntity userEntity = new UserEntity().setUserName(name)
                .setUserLastName(lastName)
                .setAvatarIndex(avatarIndex)
                .setMeshId(userId)
                .setRegistrationTime(regTime)
                .setOnlineStatus(Constants.UserStatus.OFFLINE);

        UserDataSource.getInstance().insertOrUpdateData(userEntity);
    }

    public boolean userExistedOperation(String userId, int userActiveStatus) {

        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        int updateId = UserDataSource.getInstance()
                .updateUserStatus(userId, userConnectivityStatus);

        if (updateId > 0 && (userConnectivityStatus == Constants.UserStatus.WIFI_ONLINE
                || userConnectivityStatus == Constants.UserStatus.WIFI_MESH_ONLINE
                || userConnectivityStatus == Constants.UserStatus.BLE_ONLINE
                || userConnectivityStatus == Constants.UserStatus.BLE_MESH_ONLINE
                || userConnectivityStatus == Constants.UserStatus.HB_ONLINE
                || userConnectivityStatus == Constants.UserStatus.HB_MESH_ONLINE)) {
//            BroadcastDataHelper.getInstance().syncBroadcastMsg(userId);
        }

        return updateId > 0;
    }

    public int getActiveStatus(int userActiveStatus) {

        switch (userActiveStatus) {
            case 1:
                return Constants.UserStatus.WIFI_ONLINE;
            case 10:
                return Constants.UserStatus.BLE_ONLINE;
            case 3:
                return Constants.UserStatus.WIFI_MESH_ONLINE;
            case 11:
                return Constants.UserStatus.BLE_MESH_ONLINE;
            case 5:
                return Constants.UserStatus.INTERNET_ONLINE;
            case 6:
                return Constants.UserStatus.HB_ONLINE;
            case 7:
                return Constants.UserStatus.HB_MESH_ONLINE;
            default:
                return Constants.UserStatus.OFFLINE;
        }
    }

    /**
     * This api is responsible for update user info in database
     * when users is gone in mesh network
     *
     * @param peerId -> network peer id
     */

    public void userLeave(@NonNull String peerId) {

        // FAILED MAINTAINED

        MessageSourceData.getInstance().changeSendMessageStatusByUserId(
                Constants.MessageStatus.STATUS_SENDING_START,
                Constants.MessageStatus.STATUS_FAILED, peerId);

        MessageSourceData.getInstance().changeMessageStatusByUserId(
                Constants.ContentStatus.CONTENT_STATUS_RECEIVING,
                Constants.MessageStatus.STATUS_FAILED, peerId);

        MessageSourceData.getInstance().changeUnreadMessageStatusByUserId(
                Constants.ContentStatus.CONTENT_STATUS_RECEIVING,
                Constants.MessageStatus.STATUS_UNREAD_FAILED, peerId);
    }

    public void meshInitiated() {
        if (dataSource != null) {
            dataSource.setMeshInitiated(true);
        }
    }

    /**
     * after inserting the message to the db
     * here we will fetch the last inserted message that will be
     * sent via RM.
     * <p>
     * Only for outgoing message this method will be responsible
     */
    @SuppressLint("CheckResult")
    public void prepareDataObserver() {

        AnalyticsDataHelper.getInstance().analyticsDataObserver();
        // Group text message send
        compositeDisposable.add(dataSource.getLastGroupMessage()
                .subscribeOn(Schedulers.io())
                .subscribe(groupMsgEntity -> {
                    if (groupMsgEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
                        String messageModelString = new Gson().toJson(groupMsgEntity.toMessageModel());
                        GroupDataHelper.getInstance().sendTextMessageToGroup(groupMsgEntity.getGroupId(),
                                messageModelString);
                    } else {
                        GroupDataHelper.getInstance().prepareAndSendGroupContent(groupMsgEntity, true);
                    }
                }, Throwable::printStackTrace));


        // This observer only for message send
        compositeDisposable.add(dataSource.getLastChatData()
                .subscribeOn(Schedulers.io())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming() && (chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING)) {

                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        if (messageEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
                            String messageModelString = new Gson().toJson(messageEntity.toMessageModel());
                            if (messageEntity.getMessagePlace()) {
                                GroupDataHelper.getInstance().sendTextMessageToGroup(messageEntity.getGroupId(),
                                        messageModelString);
                            } else {
                                Log.d("MessageSendTest", "messageId " + chatEntity.messageId);
                                dataSend(messageModelString.getBytes(), Constants.DataType.MESSAGE,
                                        messageEntity.getFriendsId(), true);
                            }

                            //Update message status sending to SEND. because we handover this message
                            //to library and it is library responsible to send message when user available
                            chatEntity.setStatus(Constants.MessageStatus.STATUS_SEND);

                            MessageSourceData.getInstance().insertOrUpdateData(chatEntity);

                        } else {
                            ContentDataHelper.getInstance().prepareContentObserver(messageEntity, true);
                        }

                    }
                }, Throwable::printStackTrace));

        // This observer only for re send
        compositeDisposable.add(Objects.requireNonNull(dataSource.getReSendMessage())
                .subscribeOn(Schedulers.newThread())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming() && chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING) {
                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        if (messageEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
                            String messageModelString = new Gson().toJson(messageEntity.toMessageModel());

                            dataSend(messageModelString.getBytes(),
                                    Constants.DataType.MESSAGE, chatEntity.getFriendsId(), true);
                        }
                    } else {
                        ContentDataHelper.getInstance().prepareContentObserver((MessageEntity) chatEntity, false);
                    }
                }, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getLiveUserId())
                .subscribeOn(Schedulers.newThread())
                .subscribe(liveUserId -> {

                    if (!TextUtils.isEmpty(liveUserId)) {
                        prepareRightMeshDataSource();
                        rightMeshDataSource.checkUserIsConnected(liveUserId);
                        int userActiveType = rightMeshDataSource.checkUserConnectivityStatus(liveUserId);

                        HandlerUtil.postBackground(() -> {
                            updateUserActiveStatus(liveUserId, userActiveType);
                        });
                    }
                }, Throwable::printStackTrace));

        GroupDataHelper.getInstance().groupDataObserver();
    }

    private void updateUserActiveStatus(String userId, int userActiveStatus) {
        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        Timber.tag("MIMO_SAHA").v("S_State: " + userActiveStatus + " T_State: " + userConnectivityStatus);
        UserEntity userEntity = UserDataSource.getInstance().getSingleUserById(userId);
        if (userEntity != null) {
            userEntity.setOnlineStatus(userConnectivityStatus);
            UserDataSource.getInstance().insertOrUpdateData(userEntity);
        }
    }

    /**
     * This api is responsible for creating a data model and send data to RM
     *
     * @param data -> raw data
     * @param type -> data type
     */
    protected void dataSend(@NonNull byte[] data, byte type, String userId, boolean isNotificationEnable) {

        DataModel dataModel = new DataModel()
                .setRawData(data)
                .setDataType(type);

        prepareRightMeshDataSource();

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> rightMeshDataSource.DataSend(dataModel, userId, isNotificationEnable));
    }

    protected void broadcastDataSend(String broadcastId, String metaData, String contentPath, double latitude, double longitude,
                                     double range, String expiryTime) {
        prepareRightMeshDataSource();
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> rightMeshDataSource.broadcastDataSend(broadcastId, metaData, contentPath, latitude, longitude, range, expiryTime));
    }

    /**
     * During receive any data to from RM this API is manipulating data based on application
     *
     * @param dataModel -> contains all of info about receive data
     */

    public void dataReceive(@NonNull DataModel dataModel, boolean isNewMessage) {

        int dataType = dataModel.getDataType();
        byte[] rawData = dataModel.getRawData();
        String userId = dataModel.getUserId();
        boolean isAckSuccess = dataModel.isAckSuccess();
        int ackStatus = dataModel.getDataAckStatus();

        switch (dataType) {
            case Constants.DataType.MESSAGE:
                setChatMessage(rawData, userId, isNewMessage, isAckSuccess, ackStatus);
                break;

            case Constants.DataType.MESSAGE_FEED:
//                BroadcastDataHelper.getInstance().receiveBroadcastMsgFromLocal(rawData, userId, isNewMessage, isAckSuccess);
                break;

            case Constants.DataType.MESSAGE_COUNT:
                setAnalyticsMessageCount(rawData, isAckSuccess);
                break;
            case Constants.DataType.APP_SHARE_COUNT:
                saveAppShareCount(rawData, isAckSuccess);
                break;
            case Constants.DataType.VERSION_HANDSHAKING:
                versionCrossMatching(rawData, userId, isAckSuccess);
                break;
            case Constants.DataType.SERVER_LINK:
                startAppUpdate(rawData, isAckSuccess, userId);
                break;
            case Constants.DataType.FEEDBACK_TEXT:
                parseFeedbackText(rawData, isAckSuccess);
                break;
            case Constants.DataType.FEEDBACK_ACK:
                deleteSentFeedback(rawData, isAckSuccess);
                break;
            case Constants.DataType.USER_UPDATE_INFO:
                parseUpdatedInformation(rawData, userId, isNewMessage);
                break;
            case Constants.DataType.SUCCESS_CONTENT_MESSAGE:
                ContentDataHelper.getInstance().contentMessageSuccessResponse(rawData);
                break;

            case Constants.DataType.EVENT_GROUP_CREATION:
            case Constants.DataType.EVENT_GROUP_LEAVE:
            case Constants.DataType.EVENT_GROUP_RENAME:
            case Constants.DataType.EVENT_GROUP_MEMBER_ADD:
            case Constants.DataType.EVENT_GROUP_MEMBER_REMOVE:
            case Constants.DataType.EVENT_GROUP_DATA_RELAY:
            case Constants.DataType.EVENT_GROUP_DATA_FORWARD:
            case Constants.DataType.EVENT_GROUP_COUNT_SYNC_REQUEST:
            case Constants.DataType.EVENT_GROUP_COUNT_SYNC_ACK:
            case Constants.DataType.EVENT_GROUP_COUNT_SYNCED:
                GroupDataHelper.getInstance().groupDataReceive(dataType, userId, rawData, isNewMessage);
                break;
        }
    }


    private void setChatMessage(byte[] rawChatData, String userId, boolean isNewMessage, boolean isAckSuccess, int ackStatus) {
        try {

            String messageModelText = new String(rawChatData);
            MessageModel messageModel = new Gson().fromJson(messageModelText, MessageModel.class);
            ChatEntity chatEntity = null;
            if (messageModel.isGroup()) {
                if (messageModel.getType() != Constants.MessageType.TEXT_MESSAGE) {
                    if (!isAckSuccess) {
                        saveDummyContent(messageModel, userId);
                    }

                    return;
                }
                chatEntity = new GroupMessageEntity()
                        .toChatEntity(messageModel)
                        .setFriendsId(userId)
                        .setTime(System.currentTimeMillis())
                        .setIncoming(true);
            } else {
                chatEntity = new MessageEntity()
                        .toChatEntity(messageModel)
                        .setFriendsId(userId)
                        .setTime(System.currentTimeMillis())
                        .setIncoming(true);
            }

            /*ChatEntity chatEntity = new MessageEntity()
                    .toChatEntity(messageModel)
                    .setFriendsId(userId)
                    .setTime(System.currentTimeMillis())
                    .setIncoming(true);
*/
            if (isNewMessage) {
                chatEntity.setStatus(Constants.MessageStatus.STATUS_READ).setIncoming(true);
                Timber.e("Read :: %s", chatEntity.getMessageId());
                //prepareDateSeparator(chatEntity);

                String currentThread = dataSource.getCurrentUser();

                if (messageModel.isGroup()) {
                    if (TextUtils.isEmpty(currentThread) || TextUtils.isEmpty(messageModel.getGroupId())
                            || !messageModel.getGroupId().equals(currentThread)) {
                        NotifyUtil.showNotification(chatEntity);
                        chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
                    }
                } else {
                    if (TextUtils.isEmpty(currentThread) || !userId.equals(currentThread)) {
                        NotifyUtil.showNotification(chatEntity);
                        chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
                    }
                }

                MessageSourceData.getInstance().insertOrUpdateData(chatEntity);

            } else {

                /*
                 * for delivery status update we don't need to replace the message and insert again.
                 * If we do so then paging library Diff Callback can't properly work
                 */

                chatEntity.setStatus(ackStatus).setIncoming(false);
                Timber.e("Delivered :: %s", chatEntity.getMessageId());

                if (messageModel.isGroup()) {
                    dataSource.updateGroupMessageStatus(chatEntity.getMessageId(), chatEntity.getStatus());
                } else {
                    dataSource.updateMessageStatus(chatEntity.getMessageId(), chatEntity.getStatus());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDummyContent(MessageModel messageModel, String senderId) {
        ChatEntity chatEntity = new GroupMessageEntity()
                .toChatEntity(messageModel)
                .setFriendsId(senderId)
                .setTime(System.currentTimeMillis())
                .setIncoming(true);

        chatEntity.setStatus(Constants.MessageStatus.STATUS_READ).setIncoming(true);

        String currentThread = dataSource.getCurrentUser();


        if (TextUtils.isEmpty(currentThread) || TextUtils.isEmpty(messageModel.getGroupId())
                || !messageModel.getGroupId().equals(currentThread)) {
            NotifyUtil.showNotification(chatEntity);
            chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
        }
        MessageSourceData.getInstance().insertOrUpdateData(chatEntity);

    }

    private void setAnalyticsMessageCount(byte[] rawMessageCountAnalyticsData, boolean isAck) {
        try {
            if (!isAck) {

                String messageCountString = new String(rawMessageCountAnalyticsData);

                MessageCount messageCount = new Gson().fromJson(messageCountString, MessageCount.class);

                MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity = new MessageEntity
                        .MessageAnalyticsEntity().toMessageAnalyticsEntity(messageCount);

                AnalyticsDataHelper.getInstance().processMessageForAnalytics(false, messageAnalyticsEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAppShareCount(byte[] rawData, boolean isAckSuccess) {
        try {

            String shareCountString = new String(rawData);

            ShareCountModel shareCountModel = new Gson().fromJson(shareCountString, ShareCountModel.class);
            AppShareCountEntity entity = new AppShareCountEntity().toAppShareCountEntity(shareCountModel);

            if (!isAckSuccess) {
                compositeDisposable.add(Single.fromCallable(() -> AppShareCountDataService.getInstance()
                        .insertAppShareCount(entity))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(longResult -> {
                            if (longResult > 1) {
//                                Timber.tag("AppShareCount").d("Data saved");
                            }
                        }, Throwable::printStackTrace));


            } else {
                compositeDisposable.add(Single.fromCallable(() -> AppShareCountDataService.getInstance()
                        .updateSentShareCount(entity.getUserId(), entity.getDate()))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(longResult -> {
                            if (longResult > 1) {
//                                Timber.tag("AppShareCount").d("Data Deleted");
                            }
                        }, Throwable::printStackTrace));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * When we got any ack message from RM this API is responsible
     * for updating med message status which already sent
     *
     * @param dataModel -> Contains received message id
     */
    public void ackReceive(@NonNull DataModel dataModel, int status) {

        String dataSendId = dataModel.getDataTransferId();

        if (dataSendId != null && rmDataMap.get(dataSendId) != null) {

            DataModel prevRMDataModel = rmDataMap.get(dataSendId);

            if (prevRMDataModel != null) {

                prevRMDataModel.setAckSuccess(dataModel.isAckSuccess());

                if (status == Constant.MessageStatus.SEND) {
                    prevRMDataModel.setDataAckStatus(Constants.MessageStatus.STATUS_SEND);
                } else if (status == Constant.MessageStatus.DELIVERED) {
                    prevRMDataModel.setDataAckStatus(Constants.MessageStatus.STATUS_DELIVERED);
                } else if (status == Constant.MessageStatus.RECEIVED) {
                    prevRMDataModel.setDataAckStatus(Constants.MessageStatus.STATUS_RECEIVED);
                    rmDataMap.remove(dataSendId);
                }

                dataReceive(prevRMDataModel, false);
                return;
            }
        }
    }

   /* public void stopMeshService() {
        updateUserStatus(true);
    }*/

    public void updateUserStatus(boolean isServiceStop) {
        compositeDisposable.add(updateUserToOffline()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    /*if (isServiceStop) {
                        stopMeshProcess();
                    }*/
                    Timber.tag("MIMO_SAHA").v("User offline %s", integer);
                    updateMessageStatus();
                }, Throwable::printStackTrace));
    }

    private Single<Integer> updateUserToOffline() {
        return Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserToOffline());
    }

    public void destroyMeshService() {
        compositeDisposable.add(updateUserToOffline()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    updateMessageStatus();
                }, Throwable::printStackTrace));
    }

    private void updateMessageStatus() {
        compositeDisposable.add(updateMessageStatusFailed()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    Timber.tag("MIMO_SAHA").v("Msg send failed %s", integer);
                    updateReceiveMessageStatusFailed();
                }, Throwable::printStackTrace));
    }

    private Single<Long> updateMessageStatusFailed() {
        return Single.fromCallable(() ->
                MessageSourceData.getInstance().changeMessageStatusFrom(
                        Constants.MessageStatus.STATUS_SENDING_START,
                        Constants.MessageStatus.STATUS_FAILED));
    }

    private void updateReceiveMessageStatusFailed() {
        // FAILED MAINTAINED
        compositeDisposable.add(Single.fromCallable(() ->
                MessageSourceData.getInstance().changeMessageStatusByContentStatus(
                        Constants.ContentStatus.CONTENT_STATUS_RECEIVING,
                        Constants.MessageStatus.STATUS_FAILED))
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    Timber.tag("MIMO_SAHA").v("Msg receive failed %s", integer);
                    updateReceiveUnreadMessageStatusFailed();
                }, Throwable::printStackTrace));
    }

    private void updateReceiveUnreadMessageStatusFailed() {
        // FAILED MAINTAINED
        compositeDisposable.add(Single.fromCallable(() ->
                MessageSourceData.getInstance().changeUnreadMessageStatusByContentStatus(
                        Constants.ContentStatus.CONTENT_STATUS_RECEIVING,
                        Constants.MessageStatus.STATUS_UNREAD_FAILED))
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    Timber.tag("MIMO_SAHA").v("Msg receive unread failed %s", integer);
                }, Throwable::printStackTrace));
    }

    /**
     * Concern for this api stopping RM service from app layer
     */
    public void stopRmService() {
        rightMeshDataSource.stopMeshService();
    }

    public void startMesh() {
        rightMeshDataSource.startMesh();
    }


    /**
     * This api called when all of app layer dependencies are removed,
     * i.e. update user status to offline successfully then called this method
     */
    private void stopMeshProcess() {
        prepareRightMeshDataSource();
        // TODO app needed to stop full process when close the service from library
//        rightMeshDataSource.stopMeshProcess();
    }

    public void resetUserToOfflineBasedOnService() {
        boolean isServiceEnable = isMeshServiceRunning();
        if (!isServiceEnable) {
            updateUserStatus(false);
        }
    }

    public boolean isMeshServiceRunning() {
        Context context = TeleMeshApplication.getContext();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals("com.w3engineers.meshrnd.TeleMeshService")) {
                return true;
            }
        }
        return false;
    }


  /*  public void startTelemeshService() {
        rightMeshDataSource.startTelemeshService();
    }*/

    public void launchActivity(int activityType) {
        prepareRightMeshDataSource();
        rightMeshDataSource.launchActivity(activityType);
    }

    public Location getLocationFromServiceApp() {
        prepareRightMeshDataSource();
        return rightMeshDataSource.getLocationFromService();
    }

    public boolean isWalletBackupDone() {
        prepareRightMeshDataSource();
        return rightMeshDataSource.isWalletBackupDone();
    }

    /**
     * For ReInitiating RM service need to reset rightmesh data source instance
     */
    public void restartMesh() {
        prepareRightMeshDataSource();
        rightMeshDataSource.restartMeshService();
    }

    protected String getMyMeshId() {
        return SharedPref.read(Constants.preferenceKey.MY_USER_ID);
    }

    ////////////////////////////////////////////////////////////////
    ////////////////// Analytics data Process //////////////////////
    ////////////////////////////////////////////////////////////////

    public void analyticsDataSendToSellers(MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity) {

        MessageCount messageCount = messageAnalyticsEntity.toAnalyticMessageCount();
        String messageCountString = new Gson().toJson(messageCount);

        prepareRightMeshDataSource();

        for (String sellersId : rightMeshDataSource.getAllSellers()) {
            dataSend(messageCountString.getBytes(), Constants.DataType.MESSAGE_COUNT, sellersId, false);
        }
    }

    public void sendAppShareCount() {

        ConnectivityUtil.isInternetAvailable(TeleMeshApplication.getContext(), (s, isConnected) -> {
            if (isConnected) {
                sendAppShareCountAnalytics();
            } else {
                compositeDisposable.add(AppShareCountDataService.getInstance().getTodayAppShareCount(TimeUtil.getDateString(System.currentTimeMillis()))
                        .subscribeOn(Schedulers.newThread()).subscribe(this::sendAppShareCountToSellers, Throwable::printStackTrace));
            }
        });

        sendPendingFeedback();
    }

    /**
     * Send data to local user (Seller)
     */
    public void sendAppShareCountToSellers(List<AppShareCountEntity> entityList) {
        for (AppShareCountEntity entity : entityList) {
            ShareCountModel appShareCount = entity.toAnalyticAppShareCount();
            String shareCountString = new Gson().toJson(appShareCount);
            prepareRightMeshDataSource();
            for (String sellersId : rightMeshDataSource.getAllSellers()) {
                dataSend(shareCountString.getBytes(), Constants.DataType.APP_SHARE_COUNT, sellersId, false);
            }
        }
    }

    /**
     * Send data to server
     */
    public void sendAppShareCountAnalytics() {
        compositeDisposable.add(AppShareCountDataService.getInstance()
                .getTodayAppShareCount(TimeUtil.getDateString(System.currentTimeMillis()))
                .subscribeOn(Schedulers.newThread())
                .subscribe(countList -> {
                    AnalyticsDataHelper.getInstance().sendAppShareCountAnalytics(countList);
                }, Throwable::printStackTrace));
    }

    public void newUserAnalyticsSend() {
        compositeDisposable.add(Single.fromCallable(() ->
                UserDataSource.getInstance().getUnSyncedUsers(getMyMeshId()))
                .subscribeOn(Schedulers.newThread())
                .subscribe(newMeshUserCounts -> {
                    AnalyticsDataHelper.getInstance().processNewNodesForAnalytics(newMeshUserCounts);
                }, Throwable::printStackTrace));
    }

    public void updateSyncedUser() {
        compositeDisposable.add(Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserSynced(getMyMeshId()))
                .subscribeOn(Schedulers.newThread())
                .subscribe(syncedUserCounts -> {
                }, Throwable::printStackTrace));
    }

    public void sendFeedbackToInternetUser(FeedbackEntity entity) {
        FeedbackModel model = entity.toFeedbackModel();
        String feedbackJson = new Gson().toJson(model);
        prepareRightMeshDataSource();
        for (String sellersId : rightMeshDataSource.getAllSellers()) {
            dataSend(feedbackJson.getBytes(), Constants.DataType.FEEDBACK_TEXT, sellersId, false);
        }
    }

    public void sendFeedbackAck(FeedbackModel model) {
        String feedbackJson = new Gson().toJson(model);
        dataSend(feedbackJson.getBytes(), Constants.DataType.FEEDBACK_ACK, model.getUserId(), false);
    }

    public void sendPendingFeedback() {
        compositeDisposable
                .add(Single.fromCallable(() -> FeedbackDataSource.getInstance().getFirstFeedback())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(feedback -> {
                            AnalyticsDataHelper.getInstance().sendFeedback(feedback);
                        }, Throwable::printStackTrace));

        sendPendingGroupCount();
    }

    public void sendGroupCountToInternetUser(List<GroupEntity> groupEntities) {

        String myId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);
        ArrayList<GroupCountModel> groupCountModels = new ArrayList<>();
        for (GroupEntity groupEntity : groupEntities) {
            GroupCountModel groupCountModel = groupEntity.toGroupCountModel()
                    .setSubmittedBy(myId)
                    .setDirectSend(false);
            groupCountModels.add(groupCountModel);
        }
        String groupCountJson = GsonBuilder.getInstance().getGroupCountJson(groupCountModels);

        prepareRightMeshDataSource();
        for (String sellersId : rightMeshDataSource.getAllSellers()) {
            dataSend(groupCountJson.getBytes(), Constants.DataType.EVENT_GROUP_COUNT_SYNC_REQUEST, sellersId, false);
        }
    }

    public void sendGroupCountSyncAck(ArrayList<GroupCountParseModel> modelList) {
        ArrayList<GroupCountModel> groupCountModels = new ArrayList<>();
        for (GroupCountParseModel groupCountParseModel : modelList) {
            GroupCountModel groupCountModel = new GroupCountModel().setGroupId(groupCountParseModel.getGroupId());
            groupCountModels.add(groupCountModel);
        }
        String groupCountJson = GsonBuilder.getInstance().getGroupCountJson(groupCountModels);
        dataSend(groupCountJson.getBytes(), Constants.DataType.EVENT_GROUP_COUNT_SYNC_ACK, modelList.get(0).getSubmittedBy(), false);
    }

    public void sendPendingGroupCount() {
        compositeDisposable
                .add(Single.fromCallable(() -> GroupDataSource.getInstance().getUnsyncedGroups())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(groupEntities -> {
                            if (!groupEntities.isEmpty()) {
                                AnalyticsDataHelper.getInstance().sendGroupCount(groupEntities);
                            }
                        }, Throwable::printStackTrace));
    }

    public void notifyGroupMembersAsSyncedGroup(String groupId) {

        compositeDisposable
                .add(Single.fromCallable(() -> GroupDataSource.getInstance().getGroupById(groupId))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(groupEntity -> {

                            GsonBuilder gsonBuilder = GsonBuilder.getInstance();
                            ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder.getGroupMemberInfoObj(groupEntity.getMembersInfo());

                            for (int i = 0; i < groupMembersInfos.size(); i++) {
                                GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);
                                dataSend(groupEntity.getGroupId().getBytes(), Constants.DataType.EVENT_GROUP_COUNT_SYNCED, groupMembersInfo.getMemberId(), false);
                            }
                        }, Throwable::printStackTrace));
    }

    public void uploadLogFile() {
        // Below method commented out because now we not upload log file in server

        compositeDisposable.add(Single.fromCallable(() ->
                MeshLogDataSource.getInstance().getAllUploadedLogList())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::uploadLogFile, Throwable::printStackTrace));
    }

    public void appUpdateFromOtherServer(int type, String normalUpdateJson) {

        // check app update for internet;

        if (type == Constants.AppUpdateType.BLOCKER) {
            if (DataManager.on().isNetworkOnline()) {
                InAppUpdate.getInstance(MainActivity.getInstance()).setAppUpdateProcess(true);

                AppInstaller.downloadApkFile(AppCredentials.getInstance().getFileRepoLink(),
                        MainActivity.getInstance(), DataManager.on().getNetwork());
            }

        } else {
            HandlerUtil.postForeground(() -> {
                if (!InAppUpdate.getInstance(TeleMeshApplication.getContext()).isAppUpdating()) {
                    //InAppUpdate.getInstance(TeleMeshApplication.getContext()).setAppUpdateProcess(true);
                    if (MainActivity.getInstance() == null) return;

//                    SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());
                    if (SharedPref.readBoolean(Constants.preferenceKey.ASK_ME_LATER)) {
                        long saveTime = SharedPref.readLong(Constants.preferenceKey.ASK_ME_LATER_TIME);
                        long dif = System.currentTimeMillis() - saveTime;
                        long days = dif / (24 * 60 * 60 * 1000);

                        if (days <= 2) return;
                    }

                    // We can show the dialog directly by creating a json file

                    InAppUpdate.getInstance(MainActivity.getInstance()).showAppInstallDialog(normalUpdateJson, MainActivity.getInstance());

                    // InAppUpdate.getInstance(TeleMeshApplication.getContext()).checkForUpdate(MainActivity.getInstance(), InAppUpdate.LIVE_JSON_URL);
                }
            }, TimeUnit.SECONDS.toMillis(5));
        }

    }

    private void uploadLogFile(List<String> previousList) {
        if (previousList == null) {
            previousList = new ArrayList<>();
        }

        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() +
                "/Telemesh/MeshLog");
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!previousList.contains(file.getName())
                        && Constant.CURRENT_LOG_FILE_NAME != null
                        && !Constant.CURRENT_LOG_FILE_NAME.equalsIgnoreCase(file.getName())) {
                    AnalyticsDataHelper.getInstance().sendLogFileInServer(file, TextUtils.isEmpty(getMyMeshId()) ? "Test User" : getMyMeshId(), Constants.getDeviceName());
                }
            }
        }
    }

    @Override
    public void dataSent(@NonNull DataModel rmDataModel, String dataSendId) {
        rmDataMap.put(dataSendId, rmDataModel);
    }

    @Override
    public void contentSent(ContentModel contentModel, String dataSendId) {

        ContentDataHelper.getInstance().contentDataSend(dataSendId, contentModel);
    }

    @Override
    public void onGroupContentSend(ContentModel contentModel, String result) {
        ContentDataHelper.getInstance().onGroupContentDataSend(result, contentModel);
    }

    private void parseUpdatedInformation(byte[] rawData, String userId, boolean isNewMessage) {
        if (!isNewMessage) return;

        String updatedUserData = new String(rawData);
        UserModel userModel = new Gson().fromJson(updatedUserData, UserModel.class);
        userModel.setUserId(userId);

        UserEntity userEntity = UserDataSource.getInstance().getSingleUserById(userId);

        if (userEntity != null) {

            if (TextUtils.isEmpty(userEntity.getUserName())) {
                userEntity = userEntity.updateUserEntity(userModel);
            } else {
                userEntity = userEntity.updateUserEntity(userModel);
                prepareRightMeshDataSource();
                rightMeshDataSource.saveUpdateOtherUserInfo(userEntity.getMeshId(), userEntity.getUserName(), userEntity.getAvatarIndex());

                UserDataSource.getInstance().insertOrUpdateData(userEntity);
            }
            GroupDataHelper.getInstance().updateGroupUserInfo(userEntity);
        }
    }

 /*   public void showMeshLog(String log) {
        // LogProcessUtil.getInstance().writeLog(log);
    }*/

    public void saveMyInfo() {
        prepareRightMeshDataSource();
        rightMeshDataSource.saveUpdateUserInfo();
    }

    public void broadcastUpdateProfileInfo(@NonNull String userName, String lastName) {

        // Save current my information in SDK layer
        prepareRightMeshDataSource();
        rightMeshDataSource.saveUpdateUserInfo();

        UserModel userModel = new UserModel();
        userModel.setImage(0);
        userModel.setName(userName);
        userModel.setLastName(lastName);

        String updateInfo = new Gson().toJson(userModel);

        DataModel dataModel = new DataModel()
                .setRawData(updateInfo.getBytes())
                .setDataType(Constants.DataType.USER_UPDATE_INFO);


        compositeDisposable.add(UserDataSource.getInstance().getAllFabMessagedActiveUserIds()
                .subscribeOn(Schedulers.newThread())
                .subscribe(users -> {

                    GroupDataHelper.getInstance().updateMyUserInfo();
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(() -> rightMeshDataSource.DataSend(dataModel, users, false));

                }, Throwable::printStackTrace));

        compositeDisposable.add(GroupDataHelper.getInstance()
                .syncUserUpdateInfoForUnDiscovered(updateInfo, Constants.DataType.USER_UPDATE_INFO)
                .subscribeOn(Schedulers.newThread())
                .subscribe());
    }

    // APP update process

    public void versionMessageHandshaking(String userId) {
        InAppUpdateModel model = InAppUpdate.getInstance(TeleMeshApplication.getContext()).getAppVersion();

        int versionCode = SharedPref.readInt(Constants.preferenceKey.APP_UPDATE_VERSION_CODE);
        if (BuildConfig.VERSION_CODE == versionCode) {
            model.setUpdateType(SharedPref.readInt(Constants.preferenceKey.APP_UPDATE_TYPE));
        }

        String data = new Gson().toJson(model);
        dataSend(data.getBytes(), Constants.DataType.VERSION_HANDSHAKING, userId, false);
    }

    private void versionCrossMatching(byte[] rawData, String userId, boolean isAckSuccess) {
        if (isAckSuccess) return;

        String appVersionData = new String(rawData);
        Timber.tag("InAppUpdateTest").d("version rcv: " + appVersionData + " userId: " + userId);
        InAppUpdateModel versionModel = new Gson().fromJson(appVersionData, InAppUpdateModel.class);

        InAppUpdateModel myVersionModel = InAppUpdate.getInstance(TeleMeshApplication.getContext()).getAppVersion();

        InAppUpdate instance = InAppUpdate.getInstance(TeleMeshApplication.getContext());

        String myServerLink = instance.getMyLocalServerLink();
        Timber.tag("InAppUpdateTest").d("My version Code: %s", myVersionModel.getVersionCode());
        if (myVersionModel.getVersionCode() > versionModel.getVersionCode()) {

            if (myServerLink != null) {
                // start my server
                if (!instance.isServerRunning()) {
                    instance.prepareLocalServer();
                }

                InAppUpdateModel model = new InAppUpdateModel();
                model.setUpdateLink(myServerLink);
                String data = new Gson().toJson(model);
                dataSend(data.getBytes(), Constants.DataType.SERVER_LINK, userId, false);

                Timber.tag("InAppUpdateTest").d("My version is Big: ");
            }
        } else if (versionModel.getVersionCode() > myVersionModel.getVersionCode()) {
            if (versionModel.getUpdateType() == Constants.AppUpdateType.BLOCKER) {
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().openAppBlocker(versionModel.getVersionName());
                }
            }
        }
    }

    private void startAppUpdate(byte[] rawData, boolean isAckSuccess, String userId) {
        if (isAckSuccess) return;

        int userActiveStatus = rightMeshDataSource.getUserActiveStatus(userId);

        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        if (userConnectivityStatus != Constants.UserStatus.WIFI_ONLINE) {
            return;
        }

        if (SharedPref.readBoolean(Constants.preferenceKey.ASK_ME_LATER)) {
            long saveTime = SharedPref.readLong(Constants.preferenceKey.ASK_ME_LATER_TIME);
            long days = (System.currentTimeMillis() - saveTime) / (24 * 60 * 60 * 1000);

            if (days <= 2) return;
        }

        String appVersionData = new String(rawData);
        InAppUpdateModel versionModel = new Gson().fromJson(appVersionData, InAppUpdateModel.class);

        //AppInstaller.downloadApkFile(versionModel.getUpdateLink(), MainActivity.getInstance());

        if (!InAppUpdate.getInstance(TeleMeshApplication.getContext()).isAppUpdating()) {
            //InAppUpdate.getInstance(TeleMeshApplication.getContext()).setAppUpdateProcess(true);
            if (MainActivity.getInstance() == null) return;
            InAppUpdate.getInstance(TeleMeshApplication.getContext()).checkForUpdate(MainActivity.getInstance(), versionModel.getUpdateLink());
        }

    }

    private void parseFeedbackText(byte[] rawData, boolean isAckSuccess) {
        if (isAckSuccess) return;
        String feedbackRawData = new String(rawData);
        FeedbackModel feedbackModel = new Gson().fromJson(feedbackRawData, FeedbackModel.class);

        AnalyticsDataHelper.getInstance().sendFeedbackToInternet(FeedbackEntity.toFeedbackEntity(feedbackModel), false);
    }

    private void deleteSentFeedback(byte[] rawData, boolean isAckSuccess) {
        if (isAckSuccess) return;
        String feedbackRawData = new String(rawData);
        FeedbackModel feedbackModel = new Gson().fromJson(feedbackRawData, FeedbackModel.class);
        FeedbackDataSource.getInstance().deleteFeedbackById(feedbackModel.getFeedbackId());
    }

    public void destroy() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
//            compositeDisposable.dispose();
        }
    }


    public void onWalletBackupDone() {
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().onWalletBackupDone();
        }
    }

    public void onWalletPrepared(boolean isOldAccount) {
        dataSource.setWalletPrepared(isOldAccount);
    }
}
