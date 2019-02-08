/*
package com.w3engineers.unicef.telemesh.data.local.usertable;

*/
/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/9/2018 at 12:49 PM.
 *  *
 *  * Purpose: To retrieve data from both user and message table
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/9/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 *//*


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;

*/
/**
 * Contains data from both message table and user table
 *//*

public class User extends UserEntity {

    public User(){
       super();
    }

    */
/**
     * Is there any unread message
     * for the specific user
     *//*

    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE_STATUS)
    private int messageStatus;

    @Ignore
    private boolean hasUnreadMessage;

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
        this.hasUnreadMessage = messageStatus == Constants.MessageStatus.STATUS_UNREAD ? true : false;

    }

    public boolean isHasUnreadMessage() {
        return hasUnreadMessage;
    }

    public void setHasUnreadMessage(boolean hasUnreadMessage) {
        this.hasUnreadMessage = hasUnreadMessage;
    }

}
*/
