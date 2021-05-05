package com.w3engineers.unicef.telemesh.data.helper;

/*
 * ============================================================================
 * Copyright (C) 2021 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.util.helper.NotifyUtil;

import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MeshMessageDataHelper extends RmDataHelper{
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static class SingletonHelper{
        private static final MeshMessageDataHelper INSTANCE = new MeshMessageDataHelper();
    }

    public static MeshMessageDataHelper getInstance(){
        return SingletonHelper.INSTANCE;
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
                                dataSend(messageModelString.getBytes(), Constants.DataType.MESSAGE,
                                        messageEntity.getFriendsId(), true);
                            }
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

                        rightMeshDataSource.checkUserIsConnected(liveUserId); //TODO: could be removed

                        int userActiveType = rightMeshDataSource.checkUserConnectivityStatus(liveUserId);

                        HandlerUtil.postBackground(() -> {
                            MeshUserDataHelper.getInstance().updateUserActiveStatus(liveUserId, userActiveType);
                        });
                    }
                }, Throwable::printStackTrace));

        GroupDataHelper.getInstance().groupDataObserver();
    }

    public void setChatMessage(byte[] rawChatData, String userId, boolean isNewMessage, boolean isAckSuccess, int ackStatus) {
        try {

            String messageModelText = new String(rawChatData);
            MessageModel messageModel = new Gson().fromJson(messageModelText, MessageModel.class);

            ChatEntity chatEntity = new MessageEntity()
                    .toChatEntity(messageModel)
                    .setFriendsId(userId)
                    .setTime(System.currentTimeMillis())
                    .setIncoming(true);

            if (isNewMessage) {
                chatEntity.setStatus(Constants.MessageStatus.STATUS_READ).setIncoming(true);
                Timber.e("Read :: %s", chatEntity.getMessageId());
                //prepareDateSeparator(chatEntity);

                String currentThread = Source.getDbSource().getCurrentUser();

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
                Source.getDbSource().updateMessageStatus(chatEntity.getMessageId(), chatEntity.getStatus());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMessageStatus() {
        compositeDisposable.add(updateMessageStatusFailed()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    Log.v("MIMO_SAHA", "Msg send failed " + integer);
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
                    Log.v("MIMO_SAHA", "Msg receive failed " + integer);
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
                    Log.v("MIMO_SAHA", "Msg receive unread failed " + integer);
                }, Throwable::printStackTrace));
    }

    public void parseFeedbackText(byte[] rawData, boolean isAckSuccess) {
        if (isAckSuccess) return;
        String feedbackRawData = new String(rawData);
        FeedbackModel feedbackModel = new Gson().fromJson(feedbackRawData, FeedbackModel.class);

        AnalyticsDataHelper.getInstance().sendFeedbackToInternet(FeedbackEntity.toFeedbackEntity(feedbackModel), false);
    }

    public void deleteSentFeedback(byte[] rawData, boolean isAckSuccess) {
        if (isAckSuccess) return;
        String feedbackRawData = new String(rawData);
        FeedbackModel feedbackModel = new Gson().fromJson(feedbackRawData, FeedbackModel.class);
        FeedbackDataSource.getInstance().deleteFeedbackById(feedbackModel.getFeedbackId());
    }

    public void destroy() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable.dispose();
        }
    }

}
