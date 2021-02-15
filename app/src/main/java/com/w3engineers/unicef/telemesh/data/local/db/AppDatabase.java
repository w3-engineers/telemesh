package com.w3engineers.unicef.telemesh.data.local.db;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.application.data.helper.local.base.BaseColumnNames;
import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDatabase;
import com.w3engineers.ext.strom.util.Text;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDao;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackDao;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDao;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDao;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDao;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogDao;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageDao;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDao;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.CommonUtil;

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
        UserEntity.class, MessageEntity.class, FeedEntity.class, BulletinTrackEntity.class,
        AppShareCountEntity.class, MeshLogEntity.class, FeedbackEntity.class, GroupEntity.class},
        version = BuildConfig.VERSION_CODE,
        exportSchema = false)
@TypeConverters(Converters.class)
//DB version will be aligned with App version,
// migration will be given by developer only when schema changes occur
public abstract class AppDatabase extends BaseDatabase {

    private static final int initialVersion = 1;

    @NonNull
    public abstract UserDao userDao();

    @NonNull
    public abstract MessageDao messageDao();

    @NonNull
    public abstract FeedDao feedDao();

    @NonNull
    public abstract BulletinTrackDao bulletinTrackDao();

    public abstract AppShareCountDao appShareCountDao();

    public abstract FeedbackDao feedbackDao();

    public abstract GroupDao groupDao();

    @NonNull
    public abstract MeshLogDao meshLogDao();

    private static String FEEDBACK_MIGRATION = "CREATE TABLE IF NOT EXISTS " + TableNames.FEEDBACK +
            " (" + ColumnNames.COLUMN_FEEDBACK_ID + " TEXT PRIMARY KEY NOT NULL, " + ColumnNames.COLUMN_FEEDBACK +
            " TEXT, " + ColumnNames.COLUMN_USER_ID + " TEXT, " + ColumnNames.COLUMN_USER_NAME +
            " TEXT, " + ColumnNames.TIMESTAMP + " INTEGER NOT NULL)";

    private static String USER_TABLE_MIGRATION_1 = "ALTER TABLE " + TableNames.USERS + " ADD COLUMN "
            + ColumnNames.COLUMN_USER_IS_FAVOURITE + " INTEGER NOT NULL DEFAULT 0";

    private static String USER_TABLE_MIGRATION_2 = "ALTER TABLE " + TableNames.USERS + " ADD COLUMN "
            + ColumnNames.COLUMN_USER_CONFIG_VERSION + " INTEGER NOT NULL DEFAULT 0";

    private static String MESSAGE_TABLE_MIGRATION_1 = "ALTER TABLE " + TableNames.MESSAGE + " ADD COLUMN "
            + ColumnNames.COLUMN_CONTENT_ID + " TEXT";

    private static String MESSAGE_TABLE_MIGRATION_2 = "ALTER TABLE " + TableNames.MESSAGE + " ADD COLUMN "
            + ColumnNames.COLUMN_CONTENT_PATH + " TEXT";

    private static String MESSAGE_TABLE_MIGRATION_3 = "ALTER TABLE " + TableNames.MESSAGE + " ADD COLUMN "
            + ColumnNames.COLUMN_CONTENT_THUMB_PATH + " TEXT";

    private static String MESSAGE_TABLE_MIGRATION_4 = "ALTER TABLE " + TableNames.MESSAGE + " ADD COLUMN "
            + ColumnNames.COLUMN_CONTENT_INFO + " TEXT";

    private static String MESSAGE_TABLE_MIGRATION_5 = "ALTER TABLE " + TableNames.MESSAGE + " ADD COLUMN "
            + ColumnNames.COLUMN_CONTENT_PROGRESS + " INTEGER NOT NULL DEFAULT 0";

    private static String MESSAGE_TABLE_MIGRATION_6 = "ALTER TABLE " + TableNames.MESSAGE + " ADD COLUMN "
            + ColumnNames.COLUMN_CONTENT_STATUS + " INTEGER NOT NULL DEFAULT 0";

    private static String MESSAGE_TABLE_MIGRATION_7 = "ALTER TABLE " + TableNames.MESSAGE + " ADD COLUMN "
            + ColumnNames.COLUMN_GROUP_ID + " TEXT";

    private static String MESSAGE_TABLE_MIGRATION_8 = "ALTER TABLE " + TableNames.MESSAGE + " ADD COLUMN "
            + ColumnNames.COLUMN_MESSAGE_PLACE + " INTEGER NOT NULL DEFAULT 0";

    private static String GROUP_TABLE_MIGRATION = "CREATE TABLE IF NOT EXISTS " + TableNames.GROUP +
            " (" + BaseColumnNames.ID + " INTEGER PRIMARY KEY NOT NULL, "
            + ColumnNames.COLUMN_GROUP_ID + " TEXT, "
            + ColumnNames.COLUMN_GROUP_NAME + " TEXT, "
            + ColumnNames.COLUMN_GROUP_AVATAR + " INTEGER NOT NULL, "
            + ColumnNames.COLUMN_GROUP_OWN_STATUS + " INTEGER NOT NULL, "
            + ColumnNames.COLUMN_GROUP_CREATION_TIME + " INTEGER NOT NULL, "
            + ColumnNames.COLUMN_GROUP_ADMIN_INFO + " TEXT, "
            + ColumnNames.COLUMN_GROUP_MEMBERS_INFO + " TEXT, "
            + "hasUnreadMessage INTEGER NOT NULL, " + "lastMessage TEXT, "
            + "lastPersonName TEXT, " + "lastPersonId TEXT, " + "lastMessageType INTEGER NOT NULL)";

    private static String FEED_TABLE_MIGRATION_1 = "ALTER TABLE " + TableNames.FEED + " ADD COLUMN "
            + ColumnNames.COLUMN_FEED_CONTENT_INFO + " TEXT";

    private static String FEED_TABLE_MIGRATION_2 = "ALTER TABLE " + TableNames.FEED + " ADD COLUMN "
            + ColumnNames.COLUMN_FEED_CONTENT_INFO + " INTEGER";

    @NonNull
    public static AppDatabase getInstance() {

        if (!BuildConfig.DEBUG) {
            if (CommonUtil.isEmulator())
                return (AppDatabase) sInstance;
        }

        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    Context context = App.getContext();

                    sInstance = createDbWithMigration(context); //normally initial version is always 21
                }
            }
        }

        return (AppDatabase) sInstance;
    }

    private static AppDatabase createDbWithMigration(Context context) {

        return createDb(context, context.getString(R.string.app_name), AppDatabase.class,
                initialVersion, new BaseMigration(BuildConfig.VERSION_CODE - 2, ""),
                new BaseMigration(BuildConfig.VERSION_CODE - 2, ""),
                new BaseMigration(BuildConfig.VERSION_CODE - 1, ""),
                new BaseMigration(BuildConfig.VERSION_CODE, MESSAGE_TABLE_MIGRATION_1,
                        MESSAGE_TABLE_MIGRATION_2, MESSAGE_TABLE_MIGRATION_3,
                        MESSAGE_TABLE_MIGRATION_4, MESSAGE_TABLE_MIGRATION_5,
                        MESSAGE_TABLE_MIGRATION_6, MESSAGE_TABLE_MIGRATION_7,
                        MESSAGE_TABLE_MIGRATION_8, GROUP_TABLE_MIGRATION,
                        FEED_TABLE_MIGRATION_1, FEED_TABLE_MIGRATION_2));

        /*return createDb(context, context.getString(R.string.app_name), AppDatabase.class
                , 21,
                new BaseMigration(BuildConfig.VERSION_CODE - 5, ""),
                new BaseMigration(BuildConfig.VERSION_CODE - 4, ""),
                new BaseMigration(BuildConfig.VERSION_CODE - 3, ""),
                new BaseMigration(BuildConfig.VERSION_CODE - 2, ""),
                new BaseMigration(BuildConfig.VERSION_CODE-1, FEEDBACK_MIGRATION, USER_TABLE_MIGRATION_1),
                new BaseMigration(BuildConfig.VERSION_CODE, USER_TABLE_MIGRATION_2));*/
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
                        if (baseMigration.getQueryScript().length > 0) {
                            for (String query : baseMigration.getQueryScript()) {
                                if (Text.isNotEmpty(query)) {
                                    Log.d("DatabaseMigration", "Query: " + query);
                                    database.execSQL(query);
                                }
                            }
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
