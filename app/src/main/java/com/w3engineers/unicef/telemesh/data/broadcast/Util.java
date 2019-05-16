package com.w3engineers.unicef.telemesh.data.broadcast;

import android.os.Bundle;
import android.os.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Frank Tan on 10/04/2016.
 *
 * A helper class with static properties and methods
 */
public class Util {

    public static final String LOG_TAG = "BackgroundThread";
    public static final int MESSAGE_ID = 1;
    public static final String MESSAGE_BODY = "MESSAGE_BODY";
    public static final String EMPTY_MESSAGE = "<EMPTY_MESSAGE>";

    public static String getReadableTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    public static Message createMessage(int id, String dataString) {
        Bundle bundle = new Bundle();
        bundle.putString(Util.MESSAGE_BODY, dataString);
        Message message = new Message();
        message.what = id;
        message.setData(bundle);

        return message;
    }
}
