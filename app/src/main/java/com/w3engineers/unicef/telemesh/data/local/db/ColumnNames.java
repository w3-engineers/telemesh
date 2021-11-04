package com.w3engineers.unicef.telemesh.data.local.db;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import com.w3engineers.unicef.util.base.database.BaseColumnNames;

public interface ColumnNames extends BaseColumnNames {
    //Users table
    String COLUMN_USER_NAME = "user_name";
    String COLUMN_USER_LAST_NAME = "last_name";
    String COLUMN_USER_AVATAR = "avatar";
    String COLUMN_USER_MESH_ID = "mesh_id";
    String COLUMN_USER_CUSTOM_ID = "custom_id";
    String COLUMN_USER_LAST_ONLINE_TIME = "last_online_time";
    String COLUMN_USER_IS_ONLINE = "is_online";
    String COLUMN_USER_IS_FAVOURITE = "is_favourite";
    String COLUMN_USER_IS_SYNCED = "is_synced";
    String COLUMN_USER_REGISTRATION_TIME = "registration_time";
    String COLUMN_USER_CONFIG_VERSION = "config_version";

    //Users table
    String COLUMN_GROUP_NAME = "group_name";
    String COLUMN_GROUP_AVATAR = "avatar";
    String COLUMN_GROUP_ID = "group_id";
    String COLUMN_GROUP_OWN_STATUS = "group_own_status";
    String COLUMN_GROUP_CREATION_TIME = "group_creation_time";
    String COLUMN_GROUP_ADMIN_INFO = "group_admin_info";
    String COLUMN_GROUP_MEMBERS_INFO = "group_members_info";
    String COLUMN_GROUP_IS_SYNCED = "is_synced";

    /**
     * Message table column
     */
    String COLUMN_MESSAGE_ID = "message_id";
    String COLUMN_FRIENDS_ID = "friends_id";
    String COLUMN_MESSAGE = "message";
    String COLUMN_IS_INCOMING = "is_incoming";
    String COLUMN_MESSAGE_TYPE = "message_type";
    String COLUMN_MESSAGE_TIME = "time";
    String COLUMN_MESSAGE_STATUS = "message_status";
    String COLUMN_MESSAGE_PLACE = "message_place";

    String COLUMN_CONTENT_ID = "content_id";
    String COLUMN_CONTENT_PATH = "content_path";
    String COLUMN_CONTENT_THUMB_PATH = "content_thumb_path";
    String COLUMN_CONTENT_PROGRESS = "content_progress";
    String COLUMN_CONTENT_STATUS = "content_status";
    String COLUMN_CONTENT_INFO = "content_info";

    String COLUMN_ORIGINAL_SENDER = "original_sender";
    String COLUMN_RECEIVED_USERS = "received_user";
    String COLUMN_CONTENT_MESSAGE_ID = "content_message_id";
    String COLUMN_CONTENT__ID = "content_id";
    String COLUMN_SENDER_ID = "sender_id";
    String COLUMN_RECEIVER_ID = "receiver_id";

    /**
     * Message Feed table column
     */
    String COLUMN_FEED_PROVIDER_NAME = "feed_provider_name";
    String COLUMN_FEED_PROVIDER_LOGO = "feed_provider_logo";
    String COLUMN_FEED_ID = "feed_id";
    String COLUMN_FEED_TITLE = "feed_title";
    String COLUMN_FEED_DETAIL = "feed_detail";
    String COLUMN_FEED_TIME = "feed_time";
    String COLUMN_FEED_READ_STATUS = "feed_read_status";
    String COLUMN_FEED_CONTENT_INFO = "feed_content_info";
    String COLUMN_FEED_TIME_MILLIS = "feed_title_millis";
    String COLUMN_FEED_EXPIRE_TIME = "feed_exp_time";

    String COLUMN_FEED_LATITUDE = "latitude";
    String COLUMN_FEED_LONGITUDE = "longitude";

    String COLUMN_FEED_RANGE = "range";
    String COLUMN_FEED_BROADCASTADDRESS = "broadcastAddress";

    /**
     * Bulletin track table column
     */
    String COLUMN_BULLETIN_MESSAGE_ID = "bulletin_message_id";
    String COLUMN_BULLETIN_TRACK_USER_ID = "bulletin_track_user_id";
    String COLUMN_BULLETIN_ACK_STATUS = "bulletin_ack_status";
    String COLUMN_BULLETIN_OWNER_STATUS = "bulletin_owner_status";

    /*
     * AppShareCount
     * */

    String COLUMN_USER_ID = "user_id";
    String COLUMN_COUNT = "count";
    String COLUMN_DATE = "date";
    String COLUMN_IS_SEND = "is_send";


    /*
     * Mesh log file upload
     * */

    String LOG_NAME = "log_name";

    // Feedback

    String COLUMN_FEEDBACK = "feedback";
    String COLUMN_FEEDBACK_ID = "feedback_id";
    String TIMESTAMP = "timestamp";
}