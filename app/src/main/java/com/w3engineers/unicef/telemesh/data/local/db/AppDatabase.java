package com.w3engineers.unicef.telemesh.data.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDatabase;
import com.w3engineers.ext.strom.application.data.helper.local.base.BaseMigration;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDao;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackDao;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDao;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogDao;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageDao;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDao;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


//DB version will be aligned with App version,
// migration will be given by developer only when schema changes occur
@Database(entities = {
        UserEntity.class, MessageEntity.class, FeedEntity.class, BulletinTrackEntity.class, AppShareCountEntity.class, MeshLogEntity.class},
        version = BuildConfig.VERSION_CODE,
        exportSchema = false)
@TypeConverters(Converters.class)
//DB version will be aligned with App version,
// migration will be given by developer only when schema changes occur
public abstract class AppDatabase extends BaseDatabase {

    @NonNull
    public abstract UserDao userDao();

    @NonNull
    public abstract MessageDao messageDao();

    @NonNull
    public abstract FeedDao feedDao();

    @NonNull
    public abstract BulletinTrackDao bulletinTrackDao();

    public abstract AppShareCountDao appShareCountDao();

    @NonNull
    public abstract MeshLogDao meshLogDao();

    @NonNull
    public static AppDatabase getInstance() {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    Context context = App.getContext();
                    sInstance = createDb(context, context.getString(R.string.app_name), AppDatabase.class
                            , 1, null);//normally initial version is always 1
                }
            }
        }
        return (AppDatabase) sInstance;
    }

}
