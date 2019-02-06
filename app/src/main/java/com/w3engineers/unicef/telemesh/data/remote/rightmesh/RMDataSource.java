//package io.left.core.telemesh.data.remote.rightmesh;
//
//import android.annotation.SuppressLint;
//import android.os.RemoteException;
//
//import com.google.protobuf.InvalidProtocolBufferException;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import core.left.io.framework.App;
//import core.left.io.framework.application.data.remote.BaseRmDataSource;
//import core.left.io.framework.application.data.remote.model.BaseMeshData;
//import core.left.io.framework.application.data.remote.model.MeshAcknowledgement;
//import core.left.io.framework.application.data.remote.model.MeshData;
//import core.left.io.framework.application.data.remote.model.MeshPeer;
//import io.left.core.telemesh.TeleMeshUser.RMDataModel;
//import io.left.core.telemesh.TeleMeshUser.RMUserModel;
//import io.left.core.telemesh.data.helper.constants.Constants;
//import io.left.core.telemesh.data.local.message.MessageBase;
//import io.left.core.telemesh.data.local.message.MessageService;
//import io.left.core.telemesh.data.local.message.MessageDataSource;
//import io.left.core.telemesh.data.local.usertable.UserEntity;
//import io.left.core.telemesh.data.provider.RMTaskProvider;
//import io.left.core.util.helper.AppLog;
//import io.left.core.util.helper.NotifyUtil;
//import io.reactivex.Flowable;
//import io.reactivex.Single;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.disposables.Disposable;
//
///**
// * * ============================================================================
// * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
// * * Unauthorized copying of this file, via any medium is strictly prohibited
// * * Proprietary and confidential
// * * ----------------------------------------------------------------------------
// * * Created by: Mimo Saha on [10-Sep-2018 at 3:01 PM].
// * * ----------------------------------------------------------------------------
// * * Project: TeleMesh.
// * * Code Responsibility: <Purpose of code>
// * * ----------------------------------------------------------------------------
// * * Edited by :
// * * --> <First Editor> on [10-Sep-2018 at 3:01 PM].
// * * --> <Second Editor> on [10-Sep-2018 at 3:01 PM].
// * * ----------------------------------------------------------------------------
// * * Reviewed by :
// * * --> <First Reviewer> on [10-Sep-2018 at 3:01 PM].
// * * --> <Second Reviewer> on [10-Sep-2018 at 3:01 PM].
// * * ============================================================================
// **/
//public class RMDataSource extends BaseRmDataSource implements MessageDataSource {
//
//    /**
//     * <h1>Instance variable scope</h1>
//     */
//    private static RMDataSource rmDataSource;
//
//    private RMTaskProvider rmTaskProvider;
//    private List<String> userIds;
//    private static UserEntity mCurrentUser;
//    private CompositeDisposable compositeDisposable;
//    private static Object mutex = new Object();
//
//    /**
//     * <h1><Single obj private constructor/h1>
//     *
//     * @param profileInfo : self information
//     */
//    private RMDataSource(byte[] profileInfo) {
//        super(App.getContext(), profileInfo);
//        userIds = new ArrayList<>();
//        rmTaskProvider = RMTaskProvider.getInstance();
//        compositeDisposable = new CompositeDisposable();
//    }
//
//    /**
//     * <h1>Thread safe static method to create instance</h1>
//     *
//     * @return : RMDataSource
//     */
//    public static RMDataSource getRmDataSource() {
//        RMDataSource instance = rmDataSource;
//        if (instance == null) {
//            synchronized (mutex) {
//                instance = rmDataSource;
//                if (instance == null) {
//                    byte[] profileData = UserEntity.toProtoUser();
//                    instance = rmDataSource = new RMDataSource(profileData);
//                }
//            }
//        }
//        return instance;
//    }
//
//    /**
//     * During send data to peer
//     *
//     * @param rmDataModel -> A generic data model which contains userData, type and peerId
//     * @return return the send message id
//     */
//    public int DataSend(RMDataModel rmDataModel) {
//
//        try {
//
//            MeshData meshData = new MeshData();
//            meshData.mType = (byte) rmDataModel.getDataType();
//            meshData.mData = rmDataModel.getRawData().toByteArray();
//            meshData.mMeshPeer = new MeshPeer(rmDataModel.getUserMeshId());
//            return sendMeshData(meshData);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//
//        return -1;
//    }
//
//    /**
//     * During receive a peer this time onPeer api is execute
//     *
//     * @param profileInfo -> Got a peer data (profile information and meshId)
//     */
//    @SuppressLint("TimberArgCount")
//    @Override
//    protected void onPeer(BaseMeshData profileInfo) {
//
//        try {
//            String userId = profileInfo.mMeshPeer.getPeerId();
//
//            if (!userIds.contains(userId)) {
//
//                userIds.add(userId);
//
//                RMUserModel.Builder rmUserModel = RMUserModel.newBuilder()
//                        .mergeFrom(profileInfo.mData);
//
//                if (rmUserModel != null) {
//
//                    rmUserModel.setUserId(userId);
//                    Disposable disposable = rmTaskProvider
//                            .userAdded(rmUserModel.build()).subscribe();
//
//                    compositeDisposable.add(disposable);
//                }
//            }
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * When a peer is gone or switched the another network
//     * this time onPeerGone api is executed
//     *
//     * @param meshPeer - > It contains the peer id which is currently inactive in mesh
//     */
//    @Override
//    protected void onPeerGone(MeshPeer meshPeer) {
//
//        String userId = meshPeer.getPeerId();
//
//        if (userIds.contains(userId)) {
//            Disposable disposable = rmTaskProvider
//                    .userGone(meshPeer).subscribe();
//
//            compositeDisposable.add(disposable);
//        }
//    }
//
//    /**
//     * This api execute during we receive data from network
//     *
//     * @param meshData -> Contains data and peer info also
//     */
//    @Override
//    protected void onData(MeshData meshData) {
//        switch (meshData.mType) {
//            case Constants.DataType.MESSAGE:
//                onMessageReceived(meshData);
//                break;
//            case Constants.DataType.SURVEY:
//                break;
//        }
//
//    }
//
//    /**
//     * The sending data status is success this time we got a success ack using this api
//     *
//     * @param meshAcknowledgement -> Contains the success data id and user id
//     */
//    @Override
//    protected void onAcknowledgement(MeshAcknowledgement meshAcknowledgement) {
//        onReceivedAck(meshAcknowledgement);
//    }
//
//    @Override
//    public void onRmOff() {
//        super.onRmOff();
//        AppLog.v("RM off callback here");
//        clearDisposable();
//    }
//
//    private void clearDisposable() {
//        if (compositeDisposable != null) {
//            compositeDisposable.clear();
//        }
//    }
//
//
//    private int sendMessage(String meshId, byte type, byte[] data) {
//        try {
//            MeshData meshData = new MeshData();
//            meshData.mType = type;
//            meshData.mData = data;
//            meshData.mMeshPeer = new MeshPeer(meshId);
//            return sendMeshData(meshData);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return -1;
//    }
//
//    @Override
//    public Flowable<List<MessageBase>> getAllMessages(String meshId) {
//        return MessageService.on().getAllMessages(meshId);
//    }
//
//    @Override
//    public Single<Boolean> initCurrentUser(UserEntity userEntity) {
//        //AppLog.v("Current user init =" + userEntity);
//        return Single.create(e -> RMDataSource.mCurrentUser = userEntity);
//    }
//
//    @Override
//    public long onMessageReceived(MeshData meshData) {
//        boolean isCurrentUserMessage = mCurrentUser != null
//                && mCurrentUser.meshId.equals(meshData.mMeshPeer.getPeerId());
//        MessageBase messageBase = MessageService.on().receivedMessage(meshData, isCurrentUserMessage);
//        if (!isCurrentUserMessage) {
//            NotifyUtil.showNotification(messageBase);
//        }
//        return 1L;
//    }
//
//    @Override
//    public void onReceivedAck(MeshAcknowledgement acknowledgement) {
//        AppLog.v("Received ack for ="+acknowledgement.id );
//        MessageService.on().receivedAck(acknowledgement);
//    }
//
//    @Override
//    public Single<MessageBase> sendMessage(MessageBase messageBase, String meshId) {
//        return Single.create(e -> {
//            byte[] data = messageBase.toProtoMessage().toByteArray();
//            int sendMessageTag = sendMessage(meshId, Constants.DataType.MESSAGE, data);
//            if (sendMessageTag == -1) {
//                messageBase.messageStatus = Constants.MessageStatus.STATUS_FAILED;
//            }
//            long index = MessageService.on().insertOrUpdateMessage(messageBase, sendMessageTag);
//        });
//    }
//
//    @Override
//    public Single<Long> changeMessageStatusFrom(String meshId, int oldStatus, int currentStatus) {
//        return Single.create(e -> MessageService.on().changeMessageStatusFrom(meshId, oldStatus, currentStatus));
//    }
//}
