package com.w3engineers.unicef.telemesh.data.local.message;


import android.annotation.SuppressLint;

import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/15/2018 at 4:38 PM.
 *  *
 *  * Purpose: Message page date separator
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/15/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */
@SuppressLint("ParcelCreator")
public class SeparatorMessage extends MessageEntity {
    /**
     * Date value for separator message
     */
    public String date;

    public String getDate() {
        return date;
    }

    public SeparatorMessage setDate(String date) {
        this.date = date;
        return this;
    }
}
