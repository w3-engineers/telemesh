package com.w3engineers.unicef.telemesh.data.remote.parseapi;

import android.content.Context;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.w3engineers.unicef.telemesh.data.remote.model.MessageCountModel;
/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/8/2019 at 6:26 PM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose: This class is responsible to send or receive data
 *  * from parse server.
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/8/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public class ParseManager {
    private static volatile ParseManager sInstance;
    private static final Object mutex = new Object();
    private static Context mContext;
    private static String TAG = ParseManager.class.getName();

    public synchronized static void init(Context context, String serverUrl, String appId, String clientKey) {

        synchronized (mutex) {

            if (sInstance == null) sInstance = new ParseManager();
        }
        mContext = context;
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(appId)
                .clientKey(clientKey)
                .server(serverUrl)
                .enableLocalDataStore()
                .build()
        );
    }

    public static ParseManager on() {

        return sInstance;
    }

    /**
     * This method is responsible to send Message count analytics data
     * to server of each user
     * And here we user saveEventually to handle poor connection
     * or no connection (Internet)
     *
     * @param model {@link MessageCountModel}
     */
    public void saveMessageCount(MessageCountModel model) {
        ParseObject parseObject = new ParseMapper().MessageCountToParse(model);

        parseObject.saveEventually(e -> {
            if (e == null) {
                Log.d(TAG, "Data Save complete");
            } else {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        });

    }
}
