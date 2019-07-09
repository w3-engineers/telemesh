package com.w3engineers.unicef.telemesh.data.helper;

import android.content.Context;
import android.os.AsyncTask;

import com.w3engineers.unicef.telemesh.data.remote.CredentialHolder;
import com.w3engineers.unicef.telemesh.data.remote.parseapi.ParseManager;
/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/8/2019 at 6:32 PM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/8/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public class InitManagerAsync extends AsyncTask<Void, Void, Void> {
    private static Context mContext;

    public InitManagerAsync(Context context) {
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
