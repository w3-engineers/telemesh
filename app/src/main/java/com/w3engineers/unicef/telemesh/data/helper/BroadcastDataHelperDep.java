/*
package com.w3engineers.unicef.telemesh.data.helper;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinDataSource;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class BroadcastDataHelperDep extends RmDataHelper {

    private static BroadcastDataHelperDep broadcastDataHelperDep = new BroadcastDataHelperDep();

    public static BroadcastDataHelperDep getInstance() {
        return broadcastDataHelperDep;
    }

    public void syncBroadcastMsg(String userId) {
        compositeDisposable.add(Objects.requireNonNull(BulletinDataSource.getInstance()
                .getUnsentMessage(userId)).subscribeOn(Schedulers.newThread())
                .subscribe(feedEntities -> {
                    sendBroadcastMsgToLocalUser(feedEntities, userId);
                }, Throwable::printStackTrace));
    }

    private void sendBroadcastMsgToLocalUser(List<FeedEntity> feedEntities, String userId) {
        if (feedEntities != null) {
            for (FeedEntity feedEntity : feedEntities) {

                if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                    BulletinDataSource.getInstance().insertOrUpdate(
                            getOthersTrackEntity(feedEntity.getFeedId(), userId));
                }
                String bulletinString = new Gson().toJson(feedEntity.toTelemeshBulletin());
                dataSend(bulletinString.getBytes(), Constants.DataType.MESSAGE_FEED, userId, false);
            }
        }
    }

    private void sendBroadcastMsgToLocalUsers(@NonNull FeedEntity feedEntity) {

        List<String> userLists = new ArrayList<>();

        for (UserEntity userEntity : UserDataSource.getInstance().getLivePeers()) {

            userLists.add(userEntity.meshId);

            if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                BulletinDataSource.getInstance().insertOrUpdate(
                        getOthersTrackEntity(feedEntity.getFeedId(), userEntity.meshId));
            }
        }

        String bulletinString = new Gson().toJson(feedEntity.toTelemeshBulletin());

        DataModel rmDataModel = new DataModel()
                .setRawData(bulletinString.getBytes())
                .setDataType(Constants.DataType.MESSAGE_FEED);

        ExecutorService service = Executors.newSingleThreadExecutor();
        prepareRightMeshDataSource();
        service.execute(() -> rightMeshDataSource.DataSend(rmDataModel, userLists, false));
    }

    public void receiveBroadcastMsgFromLocal(byte[] rawBulletinData, String userId,
                                             boolean isNewMessage, boolean isAckSuccess) {
        try {

            String bulletinString = new String(rawBulletinData);
            BulletinModel bulletinModel = new Gson().fromJson(bulletinString, BulletinModel.class);

            FeedEntity feedEntity = new FeedEntity().toFeedEntity(bulletinModel);

            if (isNewMessage) {
                feedEntity.setFeedReadStatus(false);

                compositeDisposable.add(Single.fromCallable(() -> FeedDataSource.getInstance()
                        .insertOrUpdateData(feedEntity))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(aLong -> {
                            if (aLong != null) {
                                if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                                    BulletinDataSource.getInstance().insertOrUpdate(getMyTrackEntity(
                                            feedEntity.getFeedId()).setBulletinOwnerStatus(Constants.Bulletin.OTHERS)
                                            .setBulletinAckStatus(Constants.Bulletin.BULLETIN_SEND_TO_SERVER));
                                }
                            }
                        }, Throwable::printStackTrace));
            } else {
                if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                    BulletinDataSource.getInstance().insertOrUpdate(getOthersTrackEntity(
                            feedEntity.getFeedId(), userId).setBulletinAckStatus(isAckSuccess ?
                            Constants.Bulletin.BULLETIN_RECEIVED : Constants.Bulletin.DEFAULT));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    private void sendPendingAck() {
        compositeDisposable.add(Objects.requireNonNull(BulletinDataSource.getInstance()
                .getAllSuccessBulletin())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendToServer, Throwable::printStackTrace));
    }

    private void sendToServer(List<BulletinTrackEntity> bulletinTrackEntities) {
//        for (BulletinTrackEntity bulletinTrackEntity : bulletinTrackEntities) {
//            sendBroadcastAck(bulletinTrackEntity.getBulletinMessageId(),
//                    bulletinTrackEntity.getBulletinTrackUserId());
//        }
    }

    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    private BulletinTrackEntity getMyTrackEntity(String messageId) {
        return new BulletinTrackEntity()
                .setBulletinMessageId(messageId)
                .setBulletinTrackUserId(getMyMeshId())
                .setBulletinAckStatus(Constants.Bulletin.BULLETIN_RECEIVED)
                .setBulletinOwnerStatus(Constants.Bulletin.MINE);
    }

    private BulletinTrackEntity getOthersTrackEntity(String messageId, String userId) {
        return new BulletinTrackEntity()
                .setBulletinMessageId(messageId)
                .setBulletinTrackUserId(userId)
                .setBulletinAckStatus(Constants.Bulletin.BULLETIN_SEND)
                .setBulletinOwnerStatus(Constants.Bulletin.OTHERS);
    }
}
*/
