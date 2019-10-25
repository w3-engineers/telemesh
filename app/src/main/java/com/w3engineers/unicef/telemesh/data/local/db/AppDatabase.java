package com.w3engineers.unicef.telemesh.data.local.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDatabase;
import com.w3engineers.ext.strom.application.data.helper.local.base.BaseMigration;
import com.w3engineers.ext.strom.util.Text;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


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
                            , 21,
                            new BaseMigration(BuildConfig.VERSION_CODE - 1, ""),
                            new BaseMigration(BuildConfig.VERSION_CODE, ""));//normally initial version is always 21
                }
            }
        }
        return (AppDatabase) sInstance;
    }


    // FIXME Here is for testing migration we copy the library module DB. After after library db we will remove ite
    protected static <T extends RoomDatabase> T createDb(Context context, String dbName,
                                                         Class<T> dbService, int initialVersion,
                                                         BaseMigration... baseMigrations) {

        RoomDatabase.Builder<T> builder = Room.databaseBuilder(context, dbService, dbName);

        //handle migrations
        List<Migration> migrations = getMigrations(initialVersion, baseMigrations);
        if (migrations != null) {
            for (Migration migration : migrations) {
                builder.addMigrations(migration);
            }
        }

        //DB created and opened call back
        builder.addCallback(new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                if (mIDbCreate != null) {
                    mIDbCreate.onDbCreated();
                }
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
                if (mIDbopened != null) {
                    mIDbopened.onDbOpened();
                }
            }
        });

        return builder.build();
    }

    /**
     * Receive {@link BaseMigration} objects to generate corresponding {@link Migration}
     *
     * @param initialVersion database's initial version
     * @param baseMigrations all base migration objects
     * @return list of Room migration to add
     */
    private static List<Migration> getMigrations(int initialVersion, BaseMigration... baseMigrations) {

        if (initialVersion < 1 || baseMigrations == null || baseMigrations.length < 1) {

            return null;
        }

        List<BaseMigration> baseMigrationList = Arrays.asList(baseMigrations);
        Collections.sort(baseMigrationList, (o1, o2) -> {

            if (o1 == null || o2 == null) {
                return -1;
            }

            //Sorts in ascending order
            return o1.getTargetedVersion() - o2.getTargetedVersion();
        });//Sorts whole list in ascending order based on version number

        List<Migration> migrationList = new ArrayList<>();

        Migration migration;
        int lastVersion = initialVersion;

        for (final BaseMigration baseMigration : baseMigrationList) {

            if (baseMigration != null) {
                migration = new Migration(lastVersion, baseMigration.getTargetedVersion()) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        if (Text.isNotEmpty(baseMigration.getQueryScript())) {
                            database.execSQL(baseMigration.getQueryScript());
                        }
                    }
                };

                migrationList.add(migration);
                lastVersion = baseMigration.getTargetedVersion();
            }
        }

        return migrationList;
    }

}
