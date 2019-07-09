package com.w3engineers.unicef.telemesh.data.remote;

import android.content.Context;
import android.os.AsyncTask;

import com.w3engineers.unicef.telemesh.data.helper.InitManagerAsync;
import com.w3engineers.unicef.telemesh.data.remote.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.remote.parseapi.ParseManager;
/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/8/2019 at 6:41 PM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose: This is main api platform of Remote call.
 *  * It is not depend on Parse or Manager class.
 *  * If any change of manager layer one manger fully replaced
 *  * here we have to change manager class
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/8/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public class RemoteApi {
    private static volatile RemoteApi ourInstance;
    private static final Object mutex = new Object();

    private RemoteApi(Context context) {
        new InitManagerAsync(context).execute();
    }

    public synchronized static void init(Context context) {
        synchronized (mutex) {
            if (ourInstance == null)
                ourInstance = new RemoteApi(context);
        }
    }

    public static RemoteApi on() {
        return ourInstance;
    }

    public void saveMessageCount(MessageCountModel model) {
        AsyncTask.execute(() -> ParseManager.on().saveMessageCount(model));
    }
}
