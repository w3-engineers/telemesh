package com.w3engineers.unicef.telemesh.data.remote.model;

/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/9/2019 at 1:12 PM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose: This class is the container of Message count analytics data
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/9/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

public class MessageCountModel {
    private String userId;
    private int msgCount;
    private long msgTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
