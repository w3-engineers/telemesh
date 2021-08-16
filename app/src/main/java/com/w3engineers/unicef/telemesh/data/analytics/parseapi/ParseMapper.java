package com.w3engineers.unicef.telemesh.data.analytics.parseapi;

import android.text.TextUtils;

import com.parse.ParseObject;
import com.w3engineers.unicef.telemesh.data.analytics.model.AppShareCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.GroupCountParseModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    ParseObject MessageCountToParse(MessageCountModel model) {

        ParseObject parseObject = new ParseObject(ParseConstant.MessageCount.TABLE);
        parseObject.put(ParseConstant.MessageCount.USER_ID, model.getUserId());
        parseObject.put(ParseConstant.MessageCount.MESSAGE_COUNT, model.getMsgCount());
        //parseObject.put(ParseConstant.MessageCount.MSG_TIME, model.getMsgTime());

        return parseObject;
    }

    ParseObject GroupCountToParse(GroupCountParseModel model) {

        ParseObject parseObject = new ParseObject(ParseConstant.GroupCount.TABLE);
        parseObject.put(ParseConstant.GroupCount.GROUP_ID, model.getGroupId());
        parseObject.put(ParseConstant.GroupCount.GROUP_OWNER, model.getGroupOwner());
        parseObject.put(ParseConstant.GroupCount.MEMBER_COUNT, model.getMemberCount());
        parseObject.put(ParseConstant.GroupCount.CREATION_DATE, model.getCreationDate());
        parseObject.put(ParseConstant.GroupCount.SUBMITTED_BY, model.getSubmittedBy());

        return parseObject;
    }

    ParseObject NewNodeToParse(List<NewNodeModel> nodeList) {
        ParseObject object = new ParseObject(ParseConstant.NewNodeUser.TABLE);
        List<String> userIdList = new ArrayList<>();
        List<Long> userTime = new ArrayList<>();
        for (NewNodeModel model : nodeList) {
            if (!TextUtils.isEmpty(model.getUserId())) {
                userIdList.add(model.getUserId());
                userTime.add(model.getUserAddingTime());
            }
        }
        object.put(ParseConstant.NewNodeUser.USER_ID, userIdList);
        object.put(ParseConstant.NewNodeUser.USER_ADDING_TIME, userTime);
        return object;
    }

    ParseObject AppShareCountToParse(List<AppShareCountModel> modelList) {
        ParseObject object = new ParseObject(ParseConstant.AppShareCount.TABLE);
        List<String> userIdList = new ArrayList<>();
        List<Date> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        for (AppShareCountModel model : modelList) {
            userIdList.add(model.getUserId());
            countList.add(model.getCount());
            dateList.add(TimeUtil.stringToDate(model.getDate()));
        }

        object.put(ParseConstant.AppShareCount.USER_ID, userIdList);
        object.put(ParseConstant.AppShareCount.COUNT, countList);
        object.put(ParseConstant.AppShareCount.DATE, dateList);
        return object;
    }
}
