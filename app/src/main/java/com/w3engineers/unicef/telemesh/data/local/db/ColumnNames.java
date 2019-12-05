package com.w3engineers.unicef.telemesh.data.local.db;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseColumnNames;

public interface ColumnNames extends BaseColumnNames {
    //Users table
    String COLUMN_USER_NAME = "user_name";
    String COLUMN_USER_AVATAR = "avatar";
    String COLUMN_USER_MESH_ID = "mesh_id";
    String COLUMN_USER_CUSTOM_ID = "custom_id";
    String COLUMN_USER_LAST_ONLINE_TIME = "last_online_time";
    String COLUMN_USER_IS_ONLINE = "is_online";
    String COLUMN_USER_IS_FAVOURITE = "is_favourite";
    String COLUMN_USER_IS_SYNCED = "is_synced";
    String COLUMN_USER_REGISTRATION_TIME = "registration_time";


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