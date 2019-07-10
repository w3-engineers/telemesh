package com.w3engineers.unicef.telemesh.data.remote.parseapi;

import com.parse.ParseObject;
import com.w3engineers.unicef.telemesh.data.remote.model.MessageCountModel;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Purpose: Convert Remote model to ParseModel like
 * ParseObject or ParseUser etc
 * ============================================================================
 */

public class ParseMapper {

    public ParseObject MessageCountToParse(MessageCountModel model) {

        ParseObject parseObject = new ParseObject(ParseConstant.MessageCount.TABLE);
        parseObject.put(ParseConstant.MessageCount.USER_ID, model.getUserId());
        parseObject.put(ParseConstant.MessageCount.MESSAGE_COUNT, model.getMsgCount());
        parseObject.put(ParseConstant.MessageCount.MSG_TIME, model.getMsgTime());

        return parseObject;
    }
}
