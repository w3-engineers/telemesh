package com.w3engineers.unicef.telemesh.data.remote.helper;

import com.w3engineers.unicef.telemesh.data.remote.RemoteApi;
import com.w3engineers.unicef.telemesh.data.remote.helper.callback.RemoteCallback;
import com.w3engineers.unicef.telemesh.data.remote.model.MessageCountModel;
/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/9/2019 at 12:27 PM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose: To access method from Remote api.
 *  * This class need to handle data which are come from
 *  * remote call/ response.
 *  * It seems duplicate method of RemoteApi.
 *  * But purpose is different
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/9/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public class RemoteHelper {

    private static RemoteCallback remoteCallback;

    public static void saveMessageCount(MessageCountModel model) {
        RemoteApi.on().saveMessageCount(model);
    }


    static {
        remoteCallback = new RemoteCallback() {
            // If any call back need we can implement here
            // by writing interface in RemoteCallback
            // Or we can use Rx instead of interface.
        };
    }
}
