package com.w3engineers.unicef.util.base.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;
import androidx.annotation.NonNull;
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

/**
 * Room database should start by extending this class`
 */
public abstract class BaseDatabase extends RoomDatabase {

    protected static volatile BaseDatabase sInstance;

    /**
     * call when db is created for the first time.
     * Good place to insert default data (if any). Call only once in the lifecycle
     */
    public interface IDbCreate {
        void onDbCreated();
    }

    /**
     * call when db is opened
     */
    public interface IDbopened {
        void onDbOpened();
    }

    protected static IDbCreate mIDbCreate;
    protected static IDbopened mIDbopened;


    /**
     * Convenient method to create Room DB
     * @param context
     * @param dbName
     * @param dbService
     * @param <T>
     * @return Database object
     *//*
    protected static <T extends RoomDatabase> T createDb(Context context, String dbName,
                                                         Class<T> dbService) {

        return createDb(context, dbName, dbService, -1, (BaseMigration) null);
    }
*/
    /**
     * Convenient method to create Room DB
     * @param context
     * @param dbName
     * @param dbService
     * @param initialVersion initial database version of the application
     * @param baseMigrations generated base migration object (pair of version and migration script)
     * @param <T>
     * @return
     */
    /*protected static <T extends RoomDatabase> T createDb(Context context, String dbName,
                                                         Class<T> dbService, int initialVersion,
                                                         BaseMigration... baseMigrations) {

        Builder<T> builder = Room.databaseBuilder(context, dbService, dbName);

        //handle migrations
        List<Migration> migrations = getMigrations(initialVersion, baseMigrations);
        if(migrations != null) {
            for (Migration migration : migrations) {
                builder.addMigrations(migration);
            }
        }

        //DB created and opened call back
        builder.addCallback(new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                if(mIDbCreate != null) {
                    mIDbCreate.onDbCreated();
                }
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
                if(mIDbopened != null) {
                    mIDbopened.onDbOpened();
                }
            }
        });

        return builder.build();
    }*/

    /**
     * Receive {@link BaseMigration} objects to generate corresponding {@link Migration}
     * @param initialVersion database's initial version
     * @param baseMigrations all base migration objects
     * @return list of Room migration to add
     */
    /*private static List<Migration> getMigrations(int initialVersion, BaseMigration... baseMigrations) {

        if(initialVersion < 1 || baseMigrations == null || baseMigrations.length < 1) {

            return null;
        }

        List<BaseMigration> baseMigrationList = Arrays.asList(baseMigrations);
        Collections.sort(baseMigrationList, (o1, o2) -> {

            if(o1 == null || o2 == null) {
                return -1;
            }

            //Sorts in ascending order
            return o1.getTargetedVersion() - o2.getTargetedVersion();
        });//Sorts whole list in ascending order based on version number

        List<Migration> migrationList = new ArrayList<>();

        Migration migration;
        int lastVersion = initialVersion;

        for (final BaseMigration baseMigration : baseMigrationList) {

            if(baseMigration != null) {
                migration = new Migration(lastVersion, baseMigration.getTargetedVersion()) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL(baseMigration.getQueryScript());
                    }
                };

                migrationList.add(migration);
                lastVersion = baseMigration.getTargetedVersion();
            }
        }

        return migrationList;
    }*/
}
