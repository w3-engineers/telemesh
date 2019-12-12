package com.w3engineers.unicef.telemesh.data.analytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.w3engineers.unicef.telemesh.data.analytics.CredentialHolder;
import com.w3engineers.unicef.telemesh.data.analytics.parseapi.ParseManager;
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/


public class InitParseManagerAsync extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public InitParseManagerAsync(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseManagerInit();
        return null;
    }

    /**
     * Init the parse server with the specific server parameter.
     */
    private void ParseManagerInit() {
        ParseManager.init(mContext, CredentialHolder.getParseServerUrl(),
                CredentialHolder.getParseAppId(),
                CredentialHolder.getParseClientKey());
    }
}
