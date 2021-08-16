package com.w3engineers.unicef.telemesh.data.analytics.parseapi;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Purpose: This class contains all Parse related Constant value.
 * Like table, Column information
 * ============================================================================
 */

public interface ParseConstant {

    interface MessageCount {
        String TABLE = "MessageCount";
        String USER_ID = "user_id";
        String MESSAGE_COUNT = "msg_count";
        String MSG_TIME = "msg_time";
    }

    interface GroupCount {
        String TABLE = "GroupCount";
        String GROUP_ID = "group_id";
        String SUBMITTED_BY = "submitted_by";
        String CREATION_DATE = "creation_date";
        String GROUP_OWNER = "group_owner";
        String  MEMBER_COUNT = "member_count";
    }

    interface NewNodeUser {
        String TABLE = "NewNodeUser";
        String USER_ID = "user_id";
        String USER_ADDING_TIME = "user_adding_time";
    }

    interface AppShareCount {
        String TABLE = "AppShareCountEntity";
        String USER_ID = "user_id";
        String DATE = "date";
        String COUNT = "count";
    }

    interface MeshLog {
        String TABLE = "MeshLog";
        String USER_ID = "user_id";
        String LOG_FILE = "log_file";
        String DEVICE_NAME = "device_name";
        String DEVICE_OS = "os_version";
    }

    interface Feedback {
        String TABLE = "Feedback";
        String USER_ID = "user_id";
        String USER_NAME = "user_name";
        String USER_FEEDBACK = "user_feedback";
        String FEEDBACK_ID = "feed_back_id";
    }
}
