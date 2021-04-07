package com.w3engineers.unicef.telemesh.data.helper;

/*
 * ============================================================================
 * Copyright (C) 2021 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import android.support.annotation.NonNull;
import android.util.Log;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MeshUserDataHelper {
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static MeshUserDataHelper meshUserDataHelper;

    public static MeshUserDataHelper getInstance() {
        if (meshUserDataHelper == null) {
            meshUserDataHelper = new MeshUserDataHelper();
            return meshUserDataHelper;
        }
        return meshUserDataHelper;
    }

    /**
     * This api is responsible for insert users in database when users is added
     *
     * @param userModel -> contains all of info about users
     */

    public void userAdd(@NonNull UserModel userModel) {

        String userId = userModel.getUserId();
        if (userId == null || !userId.startsWith("0x")) {
            Log.e("User Add: ", "Error id: " + userId);
            return;
        }

        int userActiveStatus = MeshDataSource.getRmDataSource().getUserActiveStatus(userId);

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
    }

    public void onDemandUserAdd(String userId) {
        if (userId == null || !userId.startsWith("0x")) {
            Log.e("Forward User Id: ", "Error id: " + userId);
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
        String userId = SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.MY_USER_ID);

        SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());
        String name = sharedPref.read(Constants.preferenceKey.USER_NAME);
        int avatarIndex = sharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
        long regTime = sharedPref.readLong(Constants.preferenceKey.MY_REGISTRATION_TIME);

        UserEntity userEntity = new UserEntity().setUserName(name)
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

    public void updateUserActiveStatus(String userId, int userActiveStatus) {
        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        Log.v("MIMO_SAHA","S_State: " + userActiveStatus + " T_State: " + userConnectivityStatus);
        UserEntity userEntity = UserDataSource.getInstance().getSingleUserById(userId);
        if (userEntity != null) {
            userEntity.setOnlineStatus(userConnectivityStatus);
            UserDataSource.getInstance().insertOrUpdateData(userEntity);
        }
    }

    public void resetUserToOfflineBasedOnService() {
        boolean isServiceEnable = RmDataHelper.getInstance().isMeshServiceRunning();
        if (!isServiceEnable) {
            updateUserStatus(false);
        }
    }

    public void updateUserStatus(boolean isServiceStop) {
        compositeDisposable.add(updateUserToOffline()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    /*if (isServiceStop) {
                        stopMeshProcess();
                    }*/
                    Log.v("MIMO_SAHA", "User offline " + integer);
                  MeshMessageDataHelper.getInstance().updateMessageStatus();
                }, Throwable::printStackTrace));
    }

    public Single<Integer> updateUserToOffline() {
        return Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserToOffline());
    }


    public void destroy() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable.dispose();
        }
    }


    /**
     * This APi is responsible for returning connectivity type
     *
     * @param userActiveStatus
     * @return
     */

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
}
