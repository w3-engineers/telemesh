package com.w3engineers.unicef.telemesh.ui.test;

import android.arch.lifecycle.LiveData;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.List;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [14-Sep-2018 at 4:41 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [14-Sep-2018 at 4:41 PM].
 * * --> <Second Editor> on [14-Sep-2018 at 4:41 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [14-Sep-2018 at 4:41 PM].
 * * --> <Second Reviewer> on [14-Sep-2018 at 4:41 PM].
 * * ============================================================================
 **/
public class TestViewModel extends BaseRxViewModel {

    private UserDataSource userDataSource;
    private LiveData<List<UserEntity>> userLiveData;

    public TestViewModel(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;

//        TeleMeshRMHelper.getInstance().initHelper();
    }

    LiveData<List<UserEntity>> getUserLiveData() {
        return userLiveData;
    }

//    void DataSend(String data) {
//        TeleMeshRMHelper.getInstance().dataSend(data, (byte) 1);
//    }
}
