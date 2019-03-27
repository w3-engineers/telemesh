package com.w3engineers.unicef.telemesh.ui.inappshare;

import android.app.Application;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.util.helper.NetworkConfigureUtil;

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

    public InAppShareViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Stop the InApp share server
     * when In app share process is fully done
     */
    public void stopServerProcess() {
        InstantServer.getInstance().stopServer();
    }

    /**
     * Reset RM data source instance null
     * for configuring the RM service restart
     */
    public void resetRM() {
        if (NetworkConfigureUtil.getInstance().isRmOff()) {
            NetworkConfigureUtil.getInstance().setRmOff(false);
            ServiceLocator.getInstance().resetRmDataSourceInstance();
        }
    }
}
