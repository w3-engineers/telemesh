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

public class ParseConstant {

    interface MessageCount {
        String TABLE = "MessageCount";
        String USER_ID = "user_id";
        String MESSAGE_COUNT = "msg_count";
        String MSG_TIME = "msg_time";
    }

    interface NewNodeUser {
        String TABLE = "NewNodeUser";
        String USER_ID = "user_id";
        String USER_ADDING_TIME = "user_adding_time";
    }
}
