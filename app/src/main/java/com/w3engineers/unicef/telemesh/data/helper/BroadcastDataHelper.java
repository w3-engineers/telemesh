package com.w3engineers.unicef.telemesh.data.helper;

import androidx.annotation.NonNull;

import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.AckCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastMeta;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedContentModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.GeoLocation;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;
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

    private double mLatitude, mLongitude;

    public double constantLatitude = 22.824922; // This value will be used when no data found
    public double constantLongitude = 89.551327; // This value will be used when no data found

    private static BroadcastDataHelper broadcastDataHelper = new BroadcastDataHelper();
    private HashMap<String, FeedEntity> feedEntityHashMap;
    private List<String> downloadFeedContentQueue;
    private boolean isDownloadModeBusy = false;

    private static boolean isFeedPageEnable = false;

    private BroadcastDataHelper() {
        feedEntityHashMap = new HashMap<>();
        downloadFeedContentQueue = new ArrayList<>();
        //mLatitude = LocationTracker.getInstance().getLatitude();
        //mLongitude = LocationTracker.getInstance().getLongitude();
    }

    @NonNull
    public static BroadcastDataHelper getInstance() {
        return broadcastDataHelper;
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
        if (mLatitude == 0.0 || mLongitude == 0.0) {
   /*         LocationUtil.getInstance().init(TeleMeshApplication.getContext()).getLocation().addLocationListener((lat, lang) -> {

                LocationUtil.getInstance().removeListener();

                mLatitude = lat;
                mLongitude = lang;

                getLocalUserCount();
            });*/

            if (!CommonUtil.isEmulator()) {

                //mLatitude = LocationTracker.getInstance().getLatitude();
                //mLongitude = LocationTracker.getInstance().getLongitude();
                Location location = getLocationFromServiceApp();
                if(location != null){
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                }else {
                    Log.e("location_service","Location from service is null");
                }
            } else {
                mLatitude = 22.8456;
                mLongitude = 89.5403;
            }

            getLocalUserCount();
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

    private BroadcastCommand getBroadcastMsgRequestCommand(double lat, double lang,
                                                           List<String> localActiveUsers) {
        Payload payload = new Payload();

        GeoLocation geoLocation = new GeoLocation()
                .setLatitude(lat + "").setLongitude(lang + "");

        payload.setGeoLocation(geoLocation);

        if (localActiveUsers != null) {
            payload.setConnectedClients(String.valueOf(localActiveUsers.size()));
            payload.setConnectedClientEthIds(localActiveUsers);
        } else {
            payload.setConnectedClients("0");
            payload.setConnectedClientEthIds(new ArrayList<>());
        }

        String myMeshId = getMyMeshId();

        return new BroadcastCommand().setEvent("connect")
                .setToken(AppCredentials.getInstance().getBroadCastToken())
                .setBaseStationId(myMeshId)
                .setClientId(myMeshId)
                .setPayload(payload);
    }

    private void requestBroadcastMsg(List<String> localActiveUsers) {
//        if (NetworkMonitor.isOnline()) {
        OkHttpClient.Builder client1 = new OkHttpClient.Builder();

//            OkHttpClient client = client1.socketFactory(NetworkMonitor.getNetwork().getSocketFactory()).build();
        OkHttpClient client = client1.build();

        Request request = new Request.Builder().url(AppCredentials.getInstance().getBroadCastUrl()/*"http://192.168.10.203:8080/websocket"*/).build();
        BroadcastWebSocket listener = new BroadcastWebSocket();
        if (mLatitude == 0.0 && mLongitude == 0.0) {
            listener.setBroadcastCommand(getBroadcastMsgRequestCommand(constantLatitude, constantLongitude, localActiveUsers));
        } else {
            listener.setBroadcastCommand(getBroadcastMsgRequestCommand(mLatitude, mLongitude, localActiveUsers));
        }

        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
//        }
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
                broadcastContentPath = "https://dashboard.telemesh.net/message/download?filename="
                        + bulletinFeed.getFileName();

                downloadFeedContentQueue.add(broadcastContentPath);
            }

            FeedEntity feedEntity = new FeedEntity().prepareFeedEntity(bulletinFeed).setFeedReadStatus(false)
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
                                sendLocalBroadcast(feedEntity, null);
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

            sendLocalBroadcast(feedEntity, downloadContentPath);
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

/*    public void testLocalBroadcast() {
//        String path = "/storage/emulated/0/broad.jpg";
        String path = null;

        compositeDisposable.add(FeedDataSource.getInstance().getRowCount()
                .subscribeOn(Schedulers.newThread())
                .subscribe(integerSingle -> {

                    BulletinFeed bulletinFeed = new BulletinFeed();
                    bulletinFeed.setCreatedAt("2021-03-27T05:58:32.485Z")
                            .setUploaderInfo("W3 Engineers")
                            .setMessageType(Constants.BroadcastMessageType.TEXT_BROADCAST)
                            .setMessageTitle("Text broadcast test " + integerSingle)
                            .setMessageBody("Hello message " + (++i))
                            .setMessageId(UUID.randomUUID().toString())
                            .setExpiredAt("2021-05-1T05:58:32.485Z")
                            .setRange(10.0)
                            .setLatitude(22.815698421025385)
                            .setLongitude(89.55433391932894);

                    FeedEntity feedEntity = new FeedEntity().prepareFeedEntity(bulletinFeed)
                            .setFeedReadStatus(false)
                            .setFeedTimeMillis(TimeUtil.getServerTimeToMillis(bulletinFeed.getCreatedAt()));

                    compositeDisposable.add(Single.fromCallable(() -> FeedDataSource.getInstance()
                            .insertOrUpdateData(feedEntity))
                            .subscribeOn(Schedulers.newThread())
                            .subscribe());

                    sendLocalBroadcast(feedEntity, path);

                }));
    }*/

    private void sendLocalBroadcast(FeedEntity feedEntity, String contentPath) {

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();
        BroadcastMeta broadcastMeta = feedEntity.toBroadcastMeta();

        String broadcastMetaData = gsonBuilder.getBroadcastMetaJson(broadcastMeta);

        broadcastDataSend(feedEntity.getFeedId(), broadcastMetaData, contentPath, feedEntity.getLatitude(),
                feedEntity.getLongitude(), feedEntity.getRange(), feedEntity.getFeedExpireTime());
    }

    public void receiveLocalBroadcast(String broadcastId, String metaData, String contentPath, double latitude, double longitude, double range, String expiryTime) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        BroadcastMeta broadcastMeta = gsonBuilder.getBroadcastMetaObj(metaData);
        if (broadcastMeta != null) {

            FeedEntity existingFeedEntity = FeedDataSource.getInstance().getFeedById(broadcastId);
            if (existingFeedEntity != null)
                return;

            FeedEntity feedEntity = new FeedEntity().setFeedId(broadcastId).toFeedEntity(broadcastMeta)
                    .setFeedTimeMillis(TimeUtil.getServerTimeToMillis(broadcastMeta.getCreationTime()));

            if (!TextUtils.isEmpty(contentPath)) {
                contentPath = ContentUtil.getInstance().getCopiedFilePath(contentPath, false);
                FeedContentModel feedContentModel = new FeedContentModel().setContentPath(contentPath);

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
