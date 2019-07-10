package com.w3engineers.unicef.telemesh.data.remote.helper;

import com.w3engineers.unicef.telemesh.data.remote.RemoteApi;
import com.w3engineers.unicef.telemesh.data.remote.helper.callback.RemoteCallback;
import com.w3engineers.unicef.telemesh.data.remote.model.MessageCountModel;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Purpose: To access method from Remote api.
 * This class need to handle data which are come from
 * If any change of manager layer one manger fully replaced
 * remote call/ response.
 * It seems duplicate method of RemoteApi.
 * But purpose is different
 * ============================================================================
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
