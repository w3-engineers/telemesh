/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.w3engineers.unicef.telemesh.data.local.usertable;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Using the Room database as a data source.
 */

public class UserDataSource{

    private static UserDataSource userDataSource;
    private final UserDao mUserDao;

//    private static ExecutorService executorService;

//    private UserDataSource() {
//        mUserDao = AppDatabase.getInstance().userDao();
//    }

    /**
     * This constructor is restricted and only used in unit test class
     * @param userDao -> provide dao from unit test class
     */
    public UserDataSource(@NonNull UserDao userDao) {
        mUserDao = userDao;
    }

    @NonNull
    public static UserDataSource getInstance() {
        if (userDataSource == null) {
            userDataSource = getInstance(AppDatabase.getInstance().userDao());
        }
        return userDataSource;
    }

    /**
     * This constructor is restricted and only used in unit test class
     * @param userDao -> provide dao from unit test class
     */
    @NonNull
    public static UserDataSource getInstance(@NonNull UserDao userDao) {
        if (userDataSource == null) {
            userDataSource = new UserDataSource(userDao);
//            executorService = Executors.newSingleThreadExecutor();
        }
        return userDataSource;
    }

    @NonNull
    public Flowable<UserEntity> getLastData() {
        return mUserDao.getLastInsertedUser();
    }

    public long insertOrUpdateData(@NonNull UserEntity userEntity) {
        return mUserDao.writeUser(userEntity);
    }

    @NonNull
    public Flowable<List<UserEntity>> getAllUsers() {
        return mUserDao.getAllUsers();
    }

    @Nullable
    public UserEntity getSingleUserById(@NonNull String userId) {
        return mUserDao.getSingleUserById(userId);
    }

    @NonNull
    public Flowable<UserEntity> getUserById(@NonNull String userId) {
        return mUserDao.getUserById(userId);
    }

    // TODO convert it to RX pattern when 2.1.0 Room is available
    public int updateUserToOffline() {
        return mUserDao.updateUserOffline();
    }

    void deleteUser(@NonNull String userId) {
        mUserDao.deleteUser(userId);
    }

    public List<UserEntity.NewMeshUserCount> getUnSyncedUsers() {
        return mUserDao.getUnSyncedUsers();
    }

    public int updateUserSynced() {
        return mUserDao.updateUserToSynced();
    }

    @SuppressLint("LintError")
    @NonNull
    public List<UserEntity> getLivePeers(){

        return mUserDao.getLivePeers();

        /*try {
//            return executorService.submit(mUserDao::getLivePeers).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }*/

    }

    public int updateUserStatus(String userId, int activityStatus) {
        return mUserDao.updateUserStatus(userId, activityStatus);
    }
}
