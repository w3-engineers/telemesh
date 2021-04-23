package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.ApiEvent;
import com.w3engineers.mesh.application.data.AppDataObserver;
import com.w3engineers.mesh.application.data.local.DataPlanConstants;
import com.w3engineers.mesh.application.data.model.BroadcastEvent;
import com.w3engineers.mesh.application.data.model.DataAckEvent;
import com.w3engineers.mesh.application.data.model.DataEvent;
import com.w3engineers.mesh.application.data.model.FilePendingEvent;
import com.w3engineers.mesh.application.data.model.FileProgressEvent;
import com.w3engineers.mesh.application.data.model.FileReceivedEvent;
import com.w3engineers.mesh.application.data.model.FileTransferEvent;
import com.w3engineers.mesh.application.data.model.PeerRemoved;
import com.w3engineers.mesh.application.data.model.PermissionInterruptionEvent;
import com.w3engineers.mesh.application.data.model.ServiceDestroyed;
import com.w3engineers.mesh.application.data.model.ServiceUpdate;
import com.w3engineers.mesh.application.data.model.TransportInit;
import com.w3engineers.mesh.application.data.model.UserInfoEvent;
import com.w3engineers.mesh.application.data.model.WalletCreationEvent;
import com.w3engineers.mesh.application.data.model.WalletLoaded;
import com.w3engineers.mesh.util.DialogUtil;
import com.w3engineers.mesh.util.MeshLog;
import com.w3engineers.mesh.util.lib.mesh.DataManager;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.mesh.util.lib.mesh.ViperClient;
import com.w3engineers.models.ContentMetaInfo;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;
import com.w3engineers.unicef.telemesh.data.helper.ContentModel;
import com.w3engineers.unicef.telemesh.data.helper.ContentPendingModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.model.ViperBroadcastData;
import com.w3engineers.unicef.util.helper.model.ViperContentData;
import com.w3engineers.unicef.util.helper.model.ViperData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public abstract class ViperUtil {

    private ViperClient viperClient;
    private String myUserId;
    private Context context;
    private String DEVICE_NAME = "xiaomi";

    protected ViperUtil(UserModel userModel) {
        try {
            context = MainActivity.getInstance() != null ? MainActivity.getInstance() : TeleMeshApplication.getContext();

            String AUTH_USER_NAME = AppCredentials.getInstance().getAuthUserName();
            String AUTH_PASSWORD = AppCredentials.getInstance().getAuthPassword();
            String FILE_REPO_LINK = AppCredentials.getInstance().getFileRepoLink();

            initObservers();

            viperClient = ViperClient.on(context, userModel.getName(), userModel.getImage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObservers() {

        AppDataObserver.on().startObserver(ApiEvent.TRANSPORT_INIT, event -> {
            TransportInit transportInit = (TransportInit) event;

            if (transportInit.success) {
                myUserId = transportInit.nodeId;

                onMesh(myUserId);
            }
        });

        AppDataObserver.on().startObserver(ApiEvent.WALLET_LOADED, event -> {
            WalletLoaded walletLoaded = (WalletLoaded) event;

            if (walletLoaded.success) {
                myUserId = walletLoaded.walletAddress;
                onMeshPrepared(walletLoaded.walletAddress);
            }
        });

       /* AppDataObserver.on().startObserver(ApiEvent.PEER_ADD, event -> {
            PeerAdd peerAdd = (PeerAdd) event;
            peerDiscoveryProcess(peerAdd.peerId, true);
        });*/

        AppDataObserver.on().startObserver(ApiEvent.PEER_REMOVED, event -> {
            PeerRemoved peerRemoved = (PeerRemoved) event;
            peerDiscoveryProcess(peerRemoved.peerId, false);
        });

        AppDataObserver.on().startObserver(ApiEvent.DATA, event -> {

            DataEvent dataEvent = (DataEvent) event;

            dataReceive(dataEvent.peerId, dataEvent.data);
        });

        AppDataObserver.on().startObserver(ApiEvent.DATA_ACKNOWLEDGEMENT, event -> {

            DataAckEvent dataAckEvent = (DataAckEvent) event;

            onAck(dataAckEvent.dataId, dataAckEvent.status);

        });

        AppDataObserver.on().startObserver(ApiEvent.USER_INFO, event -> {

            UserInfoEvent userInfoEvent = (UserInfoEvent) event;

            UserModel userModel = new UserModel().setName(userInfoEvent.getUserName())
                    .setImage(userInfoEvent.getAvatar())
                    .setTime(userInfoEvent.getRegTime());

            peerAdd(userInfoEvent.getAddress(), userModel);
        });

        // TODO update configuration process need to switch in service layer - mimo
        /*AppDataObserver.on().startObserver(ApiEvent.CONFIG_SYNC, event -> {

            ConfigSyncEvent configSyncEvent = (ConfigSyncEvent) event;

            if (configSyncEvent != null) {
                configSync(configSyncEvent.isUpdate(), configSyncEvent.getConfigurationCommand());
            }
        });*/

        AppDataObserver.on().startObserver(ApiEvent.SERVICE_UPDATE, event -> {


            ServiceUpdate serviceUpdate = (ServiceUpdate) event;
            MeshLog.v("SERVICE_UPDATE  " + serviceUpdate.isNeeded);
            if (serviceUpdate.isNeeded) {
                showServiceUpdateAvailable(MainActivity.getInstance());
            }

        });

        AppDataObserver.on().startObserver(ApiEvent.PERMISSION_INTERRUPTION, event -> {

            Log.v("MIMO_SAHA::", "Permission<><> 2");
            PermissionInterruptionEvent permissionInterruptionEvent = (PermissionInterruptionEvent) event;
            if (permissionInterruptionEvent != null) {
                HandlerUtil.postForeground(() -> showPermissionEventAlert(permissionInterruptionEvent.hardwareState, permissionInterruptionEvent.permissions, MainActivity.getInstance()));
            }
        });


        AppDataObserver.on().startObserver(ApiEvent.WALLET_CREATION_EVENT, event -> {
            WalletCreationEvent walletCreationEvent = (WalletCreationEvent) event;
            if (walletCreationEvent != null) {
                HandlerUtil.postForeground(this::openAlertForWalletCreation);
            }
        });

        AppDataObserver.on().startObserver(ApiEvent.FILE_RECEIVED_EVENT, event -> {
            FileReceivedEvent fileReceivedEvent = (FileReceivedEvent) event;
            if (fileReceivedEvent != null) {
                contentReceiveStart(fileReceivedEvent.getFileMessageId(),
                        fileReceivedEvent.getFilePath(), fileReceivedEvent.getSourceAddress(),
                        fileReceivedEvent.getMetaData());
            }
        });

        AppDataObserver.on().startObserver(ApiEvent.FILE_PROGRESS_EVENT, event -> {
            FileProgressEvent fileProgressEvent = (FileProgressEvent) event;
            if (fileProgressEvent != null) {
                contentReceiveInProgress(fileProgressEvent.getFileMessageId(),
                        fileProgressEvent.getPercentage());
            }
        });

        AppDataObserver.on().startObserver(ApiEvent.FILE_TRANSFER_EVENT, event -> {
            FileTransferEvent fileTransferEvent = (FileTransferEvent) event;
            if (fileTransferEvent != null) {
                contentReceiveDone(fileTransferEvent.getFileMessageId(),
                        fileTransferEvent.isSuccess());
            }
        });

        AppDataObserver.on().startObserver(ApiEvent.SERVICE_DESTROYED, event -> {
            ServiceDestroyed serviceDestroyed = (ServiceDestroyed) event;
            if (serviceDestroyed != null) {

            }
        });

        AppDataObserver.on().startObserver(ApiEvent.FILE_PENDING_EVENT, event -> {
            FilePendingEvent filePendingEvent = (FilePendingEvent) event;
            if (filePendingEvent != null) {

                ContentPendingModel contentPendingModel = new ContentPendingModel();
                contentPendingModel.setContentId(filePendingEvent.getContentId());
                contentPendingModel.setContentPath(filePendingEvent.getContentPath());
                contentPendingModel.setSenderId(filePendingEvent.getSenderId());

                contentPendingModel.setProgress(filePendingEvent.getProgress());
                contentPendingModel.setState(filePendingEvent.getState());
                contentPendingModel.setContentMetaInfo(filePendingEvent.getContentMetaInfo());
                contentPendingModel.setIncoming(filePendingEvent.isIncoming());

                pendingContents(contentPendingModel);
            }
        });

        AppDataObserver.on().startObserver(ApiEvent.BROADCAST_EVENT, event -> {
            BroadcastEvent broadcastEvent = (BroadcastEvent) event;
            if (broadcastEvent != null) {
                receiveBroadcast(broadcastEvent.getUserId(), broadcastEvent.getBroadcastId(),
                        broadcastEvent.getLatitude(), broadcastEvent.getLongitude(),
                        broadcastEvent.getMetaData(), broadcastEvent.getContentPath(),
                        broadcastEvent.getContentMeta());
            }
        });
    }

    public void showPermissionEventAlert(int hardwareEvent, List<String> permissions, Activity activity) {

        if (activity == null) return;
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.alert_hardware_permission, null);
        dialogBuilder.setView(dialogView);

        android.app.AlertDialog alertDialog = dialogBuilder.create();

        TextView title = dialogView.findViewById(R.id.interruption_title);
        TextView message = dialogView.findViewById(R.id.interruption_message);
        Button okay = dialogView.findViewById(R.id.okay_button);

        String finalTitle = "", finalMessage = "";

        boolean isPermission = false;

        if (permissions == null || permissions.isEmpty()) {

            String event = "";

            if (hardwareEvent == DataPlanConstants.INTERRUPTION_EVENT.USER_DISABLED_BT) {
                event = "Bluetooth";
            } else if (hardwareEvent == DataPlanConstants.INTERRUPTION_EVENT.USER_DISABLED_WIFI) {
                event = "Wifi";
            } else if (hardwareEvent == DataPlanConstants.INTERRUPTION_EVENT.LOCATION_PROVIDER_OFF) {
                event = "Location ";
            }

            if (!TextUtils.isEmpty(event)) {
                finalMessage = String.format(activity.getResources().getString(R.string.hardware_interruption), event);
                finalTitle = String.format(activity.getResources().getString(R.string.interruption_title), "Hardware");
            }

        } else {

            String event = "";
            for (String permission : permissions) {
                if (!TextUtils.isEmpty(permission)) {
                    if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        event = "Location";
                    } else if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        event = "Storage";
                    }
                }
            }

            if (!TextUtils.isEmpty(event)) {
                finalMessage = String.format(activity.getResources().getString(R.string.permission_interruption), event);
                finalTitle = String.format(activity.getResources().getString(R.string.interruption_title), "Permission");
            }

            isPermission = true;

        }

        boolean finalIsPermission = isPermission;
        okay.setOnClickListener(v -> {
            if (isPermissionNeeded(DEVICE_NAME)) {
                showPermissionPopupForXiaomi(MainActivity.getInstance());
            } else if (finalIsPermission) {
                DataManager.on().allowMissingPermission(permissions);
                alertDialog.dismiss();
            } else {
                alertDialog.dismiss();
            }
        });

        if (!TextUtils.isEmpty(finalTitle) && !TextUtils.isEmpty(finalMessage)) {
            title.setText(finalTitle);
            message.setText(finalMessage);

            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    private void peerDiscoveryProcess(String nodeId, boolean isActive) {
        HandlerUtil.postBackground(() -> {

            int userConnectivityStatus = isActive ? getUserActiveStatus(nodeId) : 0;
            boolean isUserExist = isNodeAvailable(nodeId, userConnectivityStatus);

            if (!isUserExist) {
                if (isActive) {
//                    pingedNodeId(nodeId);
                } else {
                    peerRemove(nodeId);
                }
            }

            if (isUserExist && !isActive) {
                peerRemove(nodeId);
            }
        });
    }

    public void openAlertForWalletCreation() {
        Context context = MainActivity.getInstance();
        DialogUtil.showConfirmationDialog(context, "Wallet Create",
                "Do you want to create wallet?",
                "No",
                "Yes",
                new DialogUtil.DialogButtonListener() {
                    @Override
                    public void onClickPositive() {
                        viperClient.openWalletCreationUI();
                        DialogUtil.dismissDialog();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onClickNegative() {
                        DialogUtil.dismissDialog();
                    }
                });
    }

    private void dataReceive(String senderId, byte[] frameData) {
        if (frameData != null) {

            ViperData viperData = ViperDataProcessor.getInstance().setDataFormatFromJson(frameData);

            if (viperData != null) {

                if (viperData.rawData != null) {
                    onData(senderId, viperData);
                }
            }
        }
    }

    private void sendDataToMesh(String nodeId, ViperData viperData, String sendId) {
        byte[] data = ViperDataProcessor.getInstance().getDataFormatToJson(viperData);

        boolean isNotificationEnable = viperData.isNotificationEnable;

        try {
            viperClient.sendMessage(myUserId, nodeId, sendId, data, isNotificationEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendLocalBroadcast(ViperBroadcastData viperBroadcastData) {
        try {
            viperClient.sendBroadcastData(viperBroadcastData.broadcastId,
                    viperBroadcastData.latitude, viperBroadcastData.longitude,
                    viperBroadcastData.range, viperBroadcastData.broadcastAddress,
                    viperBroadcastData.metaData, viperBroadcastData.contentPath,
                    viperBroadcastData.contentMeta, viperBroadcastData.expiryTime,
                    viperBroadcastData.isNotificationEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUserActiveStatus(String nodeId) {
        try {
            return viperClient.getLinkTypeById(nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String sendMeshData(String peerId, ViperData viperData) {
        if (viperData != null) {
            String sendId = UUID.randomUUID().toString();
            sendDataToMesh(peerId, viperData, sendId);
            return sendId;
        }
        return null;
    }

    public List<String> getAllSellers() {
        List<String> allInternetSellers = null;
        try {
            allInternetSellers = viperClient.getInternetSellers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allInternetSellers == null || allInternetSellers.size() == 0) {
            return new ArrayList<>();
        } else {
            return allInternetSellers;
        }
    }

    public void stopMeshService() {
        try {
            viperClient.stopMesh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartMeshService() {
        try {
            viperClient.restartMesh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveUserInfo(UserModel userModel) {

        try {
            SharedPref sharedPref = SharedPref.getSharedPref(context);

            String address = sharedPref.read(Constants.preferenceKey.MY_USER_ID);

            viperClient.updateMyInfo(userModel.getName(), userModel.getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveOtherUserInfo(UserModel userModel) {

        if (viperClient != null) {
            viperClient.updateUserInfo(userModel.getUserId(), userModel.getName(), userModel.getImage());
        }
    }

    public void sendTokenGuidelineInfoToViper(String guideLine) {
        if (guideLine != null && viperClient != null) {
            viperClient.sendPointGuidelineForUpdate(guideLine);
        }
    }

    protected void checkUserConnectionStatus(String userId) {
        try {
            viperClient.checkConnectionStatus(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int checkUserConnectivityStatus(String userId) {
        try {
            return viperClient.checkUserConnectivityStatus(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void showServiceUpdateAvailable(Activity activity) {
        if (activity == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<b>" + activity.getString(R.string.service_app_alert_title_text) + "</b>"));
                builder.setMessage(activity.getString(R.string.service_app_update_message));
                builder.setPositiveButton(Html.fromHtml("<b>" + activity.getString(R.string.button_postivive) + "<b>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.w3engineers.meshservice");
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                });

                builder.setNegativeButton(Html.fromHtml("<b>" + activity.getString(R.string.button_later) + "<b>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });

                builder.setCancelable(false);
                builder.create();
                builder.show();
            }
        });

    }

    public boolean isPermissionNeeded(String deviceName) {
        String manufacturer = android.os.Build.MANUFACTURER;
        boolean isPermissionNeeded = false;
        try {

            if (deviceName.equalsIgnoreCase(manufacturer)) {
                isPermissionNeeded = !SharedPref.getSharedPref(context).readBoolean(Constants.preferenceKey.IS_SETTINGS_PERMISSION_DONE);
            }
        } catch (Exception e) { e.printStackTrace(); }

        return isPermissionNeeded;
    }

    public void showPermissionPopupForXiaomi(Activity activity) {
        if (activity == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(Html.fromHtml("<b>" + "<font color='#FF7F27'>Please allow permissions</font>" + "</b>"));
        builder.setMessage(activity.getString(R.string.permission_xiomi));
        builder.setPositiveButton(Html.fromHtml("<b>" + activity.getString(com.w3engineers.mesh.R.string.ok) + "<b>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                SharedPref.getSharedPref(context).write(Constants.preferenceKey.IS_SETTINGS_PERMISSION_DONE, true);
                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 100);
            }
        });
        builder.create();
        builder.show();
    }

    public String sendContentMessage(String peerId, ViperContentData viperContentData) {
        if (viperContentData != null) {
            ContentModel contentModel = viperContentData.contentModel;

            ContentMetaInfo contentMetaInfo = new ContentMetaInfo()
                    .setMessageId(contentModel.getMessageId())
                    .setMessageType(contentModel.getMessageType())
                    .setIsContent(contentModel.isContent())
                    .setMetaInfo(contentModel.getContentInfo());
            String contentPath;

            if (contentModel.isThumbSend()) {
                contentPath = contentModel.getContentPath();
                contentMetaInfo.setContentType(Constants.DataType.CONTENT_MESSAGE);
            } else {
                contentPath = contentModel.getThumbPath();
                contentMetaInfo.setContentType(Constants.DataType.CONTENT_THUMB_MESSAGE);
            }

            String contentMessageString = new Gson().toJson(contentMetaInfo);
            try {

                if (contentModel.isResendMessage()) {
                    if (contentModel.isRequestFromReceiver()) {
                        String contentId = contentModel.getContentId();
                        viperClient.sendFileResumeRequest(contentId, contentMessageString.getBytes());
                    } else {
                        String contentId = contentModel.getContentId();
                        viperClient.sendFileResumeRequest(contentId, contentMessageString.getBytes());
                        return contentId;
                    }
                } else {
                    String sendId = viperClient.sendFileMessage(peerId, contentPath, contentMessageString.getBytes());
                    Timber.tag("FileMessage").v("SendId: " + sendId);
                    return sendId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void removeSendContents(String contentId) {
        try {
            viperClient.removeSendContent(contentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    protected abstract void onMesh(String myMeshId);

    protected abstract void onMeshPrepared(String myWalletAddress);

    protected abstract void offMesh();

    protected abstract void peerAdd(String peerId, byte[] peerData);

    protected abstract void peerAdd(String peerId, UserModel userModel);

    protected abstract void peerRemove(String nodeId);

    protected abstract void onData(String peerId, ViperData viperData);

    protected abstract void onAck(String messageId, int status);

    protected abstract boolean isNodeAvailable(String nodeId, int userActiveStatus);

    protected abstract void contentReceiveStart(String contentId, String contentPath,
                                                String userId, byte[] metaData);

    protected abstract void contentReceiveInProgress(String contentId, int progress);

    protected abstract void contentReceiveDone(String contentId, boolean contentStatus);

    protected abstract void pendingContents(ContentPendingModel contentPendingModel);

    protected abstract void receiveBroadcast(String userId, String broadcastId, double latitude,  double longitude, String metaData, String contentPath, String contentMeta);
}
