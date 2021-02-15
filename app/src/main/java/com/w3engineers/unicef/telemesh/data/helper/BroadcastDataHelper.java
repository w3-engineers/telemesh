package com.w3engineers.unicef.telemesh.data.helper;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.w3engineers.mesh.util.NetworkMonitor;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.AckCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedContentModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.GeoLocation;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.LocationUtil;
import com.w3engineers.unicef.util.helper.NotifyUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class BroadcastDataHelper extends RmDataHelper {

//    public String mLatitude = "22.824922";
//    public String mLongitude = "89.551327";

    public String mLatitude;
    public String mLongitude;

    private static BroadcastDataHelper broadcastDataHelper = new BroadcastDataHelper();
    private HashMap<String, FeedEntity> feedEntityHashMap;
    private List<String> downloadFeedContentQueue;
    private boolean isDownloadModeBusy = false;

    private static boolean isFeedPageEnable = false;

    private BroadcastDataHelper() {
        feedEntityHashMap = new HashMap<>();
        downloadFeedContentQueue = new ArrayList<>();
    }

    @NonNull
    public static BroadcastDataHelper getInstance() {
        return broadcastDataHelper;
    }

    public void broadcastDataReceive(byte broadcastType, String broadcastId, String metaData, String userId, String contentPath, String contentMeta) {
        switch (broadcastType) {
            case Constants.DataType.MESSAGE_FEED:
                receiveLocalBroadcast(userId, broadcastId, metaData, contentPath, contentMeta);
                break;
        }
    }

    public void setIsFeedPageEnable(boolean isFeedPageEnable) {
        BroadcastDataHelper.isFeedPageEnable = isFeedPageEnable;
        if (isFeedPageEnable) {
            NotifyUtil.cancelBroadcastMessage();
        }
    }

    public void requestForBroadcast() {
        requestInitBroadcastMsg();
    }

    private void requestInitBroadcastMsg() {
        if (TextUtils.isEmpty(mLatitude) || TextUtils.isEmpty(mLongitude)) {
            LocationUtil.getInstance().init(TeleMeshApplication.getContext()).getLocation().addLocationListener((lat, lang) -> {

                LocationUtil.getInstance().removeListener();

                mLatitude = lat;
                mLongitude = lang;

                getLocalUserCount();
            });
        } else {
            getLocalUserCount();
        }
    }

    private void getLocalUserCount() {
        compositeDisposable.add(Single.fromCallable(() ->
                UserDataSource.getInstance().getLocalUserCount())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::requestBroadcastMsg, Throwable::printStackTrace));
    }

    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    int i = 0;
    public void demoTextBroadcast() {
        BulletinFeed bulletinFeed = new BulletinFeed();
        bulletinFeed.setCreatedAt("2020-08-27T05:58:32.485Z")
                .setUploaderInfo("W3 Engineers")
                .setMessageType(Constants.BroadcastMessageType.TEXT_BROADCAST)
                .setMessageTitle("Text broadcast test")
                .setMessageBody("Hello message " + (++i))
                .setMessageId(UUID.randomUUID().toString());
        String bulletinData = new Gson().toJson(bulletinFeed);
        responseBroadcastMsg(bulletinData);
    }

    private BroadcastCommand getBroadcastMsgRequestCommand(String lat, String lang,
                                                           List<String> localActiveUsers) {
        Payload payload = new Payload();

        GeoLocation geoLocation = new GeoLocation()
                .setLatitude(lat).setLongitude(lang);

        payload.setGeoLocation(geoLocation);

        if (localActiveUsers != null) {
            payload.setConnectedClients(String.valueOf(localActiveUsers.size()));
            payload.setConnectedClientEthIds(localActiveUsers);
        } else {
            payload.setConnectedClients("0");
            payload.setConnectedClientEthIds(new ArrayList<>());
        }

        return new BroadcastCommand().setEvent("connect")
                .setToken(AppCredentials.getInstance().getBroadCastToken())
                .setBaseStationId(getMyMeshId())
                .setClientId(getMyMeshId())
                .setPayload(payload);
    }

    private void requestBroadcastMsg(List<String> localActiveUsers) {
        if (NetworkMonitor.isOnline()) {
            OkHttpClient.Builder client1 = new OkHttpClient.Builder();
            OkHttpClient client = client1.socketFactory(NetworkMonitor.getNetwork().getSocketFactory()).build();

            Request request = new Request.Builder().url(AppCredentials.getInstance().getBroadCastUrl()).build();
            BroadcastWebSocket listener = new BroadcastWebSocket();
            listener.setBroadcastCommand(getBroadcastMsgRequestCommand(mLatitude, mLongitude, localActiveUsers));
            client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
        }
    }

    public void responseBroadcastMsg(@NonNull String broadcastText) {
        try {
            BulletinFeed bulletinFeed = new Gson().fromJson(broadcastText, BulletinFeed.class);

            sendBroadcastAck(bulletinFeed.getMessageId(), getMyMeshId());

            FeedEntity existingFeedEntity = FeedDataSource.getInstance().getFeedById(bulletinFeed.getMessageId());
            if (existingFeedEntity != null)
                return;

            String broadcastContentPath;

            if (bulletinFeed.getMessageType() <= Constants.BroadcastMessageType.TEXT_BROADCAST) {
                broadcastContentPath = "";
            } else {
                String messageBody = bulletinFeed.getMessageBody();
                if (TextUtils.isEmpty(messageBody)) {
                    bulletinFeed.setMessageBody(Constants.BroadcastMessage.IMAGE_BROADCAST);
                }
//                https://dashboard.telemesh.net/message/read_image?filename=photo_1599560858132-18920443-images.jpeg%27
                broadcastContentPath = "https://dashboard.telemesh.net/message/download?filename="
                        + bulletinFeed.getFileName();

                downloadFeedContentQueue.add(broadcastContentPath);
            }

            FeedEntity feedEntity = new FeedEntity().prepareFeedEntity(bulletinFeed)
                    .setFeedReadStatus(false)
                    .setFeedTimeMillis(TimeUtil.getServerTimeToMillis(bulletinFeed.getCreatedAt()));

            if (!TextUtils.isEmpty(broadcastContentPath)) {
                FeedContentModel feedContentModel = new FeedContentModel().setContentUrl(broadcastContentPath);
                String contentInfo = GsonBuilder.getInstance().getFeedContentModelJson(feedContentModel);
                feedEntity.setFeedContentInfo(contentInfo);

                feedEntityHashMap.put(broadcastContentPath, feedEntity);
            }

            compositeDisposable.add(Single.fromCallable(() -> FeedDataSource.getInstance()
                    .insertOrUpdateData(feedEntity))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(insertedFeedEntity -> {
                        if (insertedFeedEntity != null) {
                            if (!TextUtils.isEmpty(insertedFeedEntity.getFeedContentInfo())) {
                                initBroadcastContentDownload();
                            } else {
                                sendLocalBroadcast(feedEntity, null, null);
                            }
                        }
                    }, Throwable::printStackTrace));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    private void initBroadcastContentDownload() {
        if (downloadFeedContentQueue.isEmpty())
            return;

        if (isDownloadModeBusy)
            return;

        isDownloadModeBusy = true;

        compositeDisposable.add(Single.fromCallable(() -> ContentUtil.getInstance()
                .getContentFromUrl(downloadFeedContentQueue.get(0)))
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::actionOnDownloadedBroadcastContent,
                        Throwable::printStackTrace));
    }

    private void actionOnDownloadedBroadcastContent(String downloadContentPath) {
        String downloadContentUrl = downloadFeedContentQueue.get(0);

        FeedEntity feedEntity = feedEntityHashMap.get(downloadContentUrl);
        if (feedEntity != null) {

            FeedEntity existingFeedEntity = FeedDataSource.getInstance().getFeedById(feedEntity.getFeedId());
            if (existingFeedEntity != null) {
                feedEntity = existingFeedEntity;
            }

            GsonBuilder gsonBuilder = GsonBuilder.getInstance();
            String contentInfo = feedEntity.getFeedContentInfo();

            FeedContentModel feedContentModel;
            if (!TextUtils.isEmpty(contentInfo)) {
                feedContentModel = gsonBuilder.getFeedContentModelObj(contentInfo);
            } else {
                feedContentModel = new FeedContentModel();
            }
            feedContentModel.setContentPath(downloadContentPath)
                    .setContentUrl(downloadContentUrl);

            contentInfo = gsonBuilder.getFeedContentModelJson(feedContentModel);
            feedEntity.setFeedContentInfo(contentInfo);

            sendLocalBroadcast(feedEntity, downloadContentUrl, downloadContentPath);
            FeedEntity finalFeedEntity = feedEntity;
            compositeDisposable.add(Single.fromCallable(() -> FeedDataSource.getInstance()
                    .insertOrUpdateData(finalFeedEntity))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe());
        }
        isDownloadModeBusy = false;
        downloadFeedContentQueue.remove(0);
        initBroadcastContentDownload();
    }

    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    private BroadcastCommand getBroadcastAckRequestCommand(String messageId, String userId) {
        Payload payload = new Payload().setMessageId(messageId);
        return new BroadcastCommand().setEvent("ack_msg_received")
                .setToken(AppCredentials.getInstance().getBroadCastToken())
                .setBaseStationId(getMyMeshId())
                .setClientId(userId)
                .setPayload(payload);
    }

    private void sendBroadcastAck(String messageId, String userId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(AppCredentials.getInstance().getBroadCastUrl()).build();
        BroadcastWebSocket listener = new BroadcastWebSocket();
        listener.setBroadcastCommand(getBroadcastAckRequestCommand(messageId, userId));
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    public void responseBroadcastAck(@NonNull String ackText) {
        AckCommand ackCommand = new Gson().fromJson(ackText, AckCommand.class);
        if (ackCommand != null && ackCommand.getStatus() == 1) {
            compositeDisposable.add(BulletinDataSource.getInstance()
                    .setFullSuccess(ackCommand.getAckMsgId(), ackCommand.getClientId())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(integer -> {
                    }, Throwable::printStackTrace));
        }
    }

    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    private void sendLocalBroadcast(FeedEntity feedEntity, String contentUrl, String contentPath) {
//        if (!TextUtils.isEmpty(contentPath))
//            contentPath = contentPath.substring(1);

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();
        BulletinModel bulletinModel = feedEntity.toTelemeshBulletin().setContentUrl(contentUrl);

        String metaData = gsonBuilder.getBulletinModelJson(bulletinModel);
        broadcastDataSend(feedEntity.getFeedId(), Constants.DataType.MESSAGE_FEED, metaData,
                contentPath, "contentUrl", feedEntity.getFeedExpireTime(), true);
    }

    private void receiveLocalBroadcast(String userId, String broadcastId, String metaData, String contentPath, String contentMeta) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        BulletinModel bulletinModel = gsonBuilder.getBulletinModelObj(metaData);
        if (bulletinModel != null) {

            FeedEntity existingFeedEntity = FeedDataSource.getInstance().getFeedById(bulletinModel.getId());
            if (existingFeedEntity != null)
                return;

            FeedEntity feedEntity = new FeedEntity().toFeedEntity(bulletinModel)
                    .setFeedTimeMillis(TimeUtil.getServerTimeToMillis(bulletinModel.getTime()));

            if (!TextUtils.isEmpty(contentPath)) {
                contentPath = ContentUtil.getInstance().getCopiedFilePath(contentPath, false);
                FeedContentModel feedContentModel = new FeedContentModel().setContentPath(contentPath)
                        .setContentUrl(bulletinModel.getContentUrl());
                String contentInfo = gsonBuilder.getFeedContentModelJson(feedContentModel);
                feedEntity.setFeedContentInfo(contentInfo);
            }

            if (!isFeedPageEnable) {
                NotifyUtil.showBroadcastEventNotification();
            }

            compositeDisposable.add(Single.fromCallable(() -> FeedDataSource.getInstance()
                    .insertOrUpdateData(feedEntity))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(aLong -> {
                    }, Throwable::printStackTrace));
        }
    }
}
