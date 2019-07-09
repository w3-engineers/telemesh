package com.w3engineers.unicef.telemesh.data.remote.parseapi;

import com.parse.ParseObject;
import com.w3engineers.unicef.telemesh.data.remote.model.MessageCountModel;
/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/9/2019 at 1:13 PM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose: Convert Remote model to ParseModel like
 *  * ParseObject or ParseUser etc
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/9/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
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
