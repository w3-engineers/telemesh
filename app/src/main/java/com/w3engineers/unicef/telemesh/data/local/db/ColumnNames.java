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
//    String COLUMN_USER_LAST_NAME = "user_last_name";
    String COLUMN_USER_AVATAR = "avatar";
    String COLUMN_USER_MESH_ID = "mesh_id";
    String COLUMN_USER_CUSTOM_ID = "custom_id";
    String COLUMN_USER_LAST_ONLINE_TIME = "last_online_time";
    String COLUMN_USER_IS_ONLINE = "is_online";


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
     * Survey table column
     */
    String COLUMN_SENDER_ID = "sender_id";
    String COLUMN_SURVEY_ID = "survey_id";
    String COLUMN_SURVEY_TITLE = "survey_title";
    String COLUMN_SURVEY_FORM = "survey_form";
    String COLUMN_START_TIME = "start_time";
    String COLUMN_END_TIME = "end_time";
    String COLUMN_VENDOR_NAME = "vendor_name";
    String COLUMN_IS_SUBMITTED = "is_submitted";
    String COLUMN_SURVEY_ANS = "survey_answer";


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
     * Broadcast message table column
     */
    String COLUMN_BROADCAST_MESSAGE_ID = "broadcast_message_id";
    String COLUMN_BROADCAST_MESSAGE_TITLE = "broadcast_message_title";
    String COLUMN_BROADCAST_MESSAGE_DETAIL = "broadcast_message_detail";
    String COLUMN_BROADCAST_MESSAGE_RECEIVED_TIME = "broadcast_message_received time";
    String COLUMN_BROADCAST_MESSAGE_DEADLINE = "broadcast_message_deadline";
}