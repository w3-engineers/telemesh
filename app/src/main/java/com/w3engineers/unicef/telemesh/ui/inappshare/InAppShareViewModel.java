package com.w3engineers.unicef.telemesh.ui.inappshare;

import android.app.Application;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [13-Mar-2019 at 12:23 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [13-Mar-2019 at 12:23 PM].
 * --> <Second Editor> on [13-Mar-2019 at 12:23 PM].
 * Reviewed by :
 * --> <First Reviewer> on [13-Mar-2019 at 12:23 PM].
 * --> <Second Reviewer> on [13-Mar-2019 at 12:23 PM].
 * ============================================================================
 **/
public class InAppShareViewModel extends BaseRxAndroidViewModel {

    private InstantServer.PercentCallback paPercentCallback;

    public InAppShareViewModel(@NonNull Application application, InstantServer.PercentCallback percentCallback) {
        super(application);
        this.paPercentCallback = percentCallback;
    }

    public void stopServerProcess() {
        InstantServer.getInstance().stopServer();
    }
}
