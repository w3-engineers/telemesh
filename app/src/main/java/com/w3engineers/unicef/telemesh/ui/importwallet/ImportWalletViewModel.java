package com.w3engineers.unicef.telemesh.ui.importwallet;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class ImportWalletViewModel extends AndroidViewModel {

    public ImportWalletViewModel(@NonNull Application application) {
        super(application);
    }

    boolean storeData(@Nullable String address, String password, String publicKey) {

        // Store name and image on PrefManager
        SharedPref sharedPref = SharedPref.getSharedPref(getApplication().getApplicationContext());

        sharedPref.write(Constants.preferenceKey.MY_WALLET_ADDRESS, address);
        sharedPref.write(Constants.preferenceKey.MY_PUBLIC_KEY, publicKey);
        return true;
    }
}
