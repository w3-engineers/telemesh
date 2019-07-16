package com.w3engineers.unicef.telemesh.data.local.appsharecount;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.List;

import io.reactivex.Single;

public class AppShareCountDataService {
    AppShareCountDao appShareCountDao;

    private static AppShareCountDataService appShareCountDataService = new AppShareCountDataService();

    private AppShareCountDataService() {
        appShareCountDao = AppDatabase.getInstance().appShareCountDao();
    }

    public static AppShareCountDataService getInstance() {
        return appShareCountDataService;
    }

    public long insertAppShareCount(AppShareCountEntity entity) {
        return appShareCountDao.insertOrUpdate(entity);
    }

    public Single<List<AppShareCountEntity>> getTodayAppShareCount(String date) {
        return appShareCountDao.getTodayAppShareCount(date);
    }

    public boolean isCountExist(String userId, String date) {
        return appShareCountDao.isCountExist(userId, date) > 1;
    }

    public int updateCount(String userID, String date) {
        return appShareCountDao.updateCount(userID, date);
    }

    public int deleteCount(String userId, String date) {
        return appShareCountDao.deleteAppShareCount(userId, date);
    }

    public int updateSentShareCount(String userId, String date) {
        return appShareCountDao.updateSentShareCount(userId, date);
    }
}
