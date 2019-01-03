package com.w3engineers.ext.viper.application.ui.base.rm;


/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-17 at 10:17 AM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-17 at 10:17 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-17 at 10:17 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.data.remote.BaseRmDataSource;

/**
 * Currently this Activity only serves to show service notification only.
 * This is a base to adapt common tasks for RM at activity layer over the time
 * https://code.leftofthedot.com/azim/android-framework/issues/5
 */
public abstract class RmBaseActivity extends BaseActivity {

    protected abstract BaseServiceLocator getServiceLocator();

    @Override
    protected void onPause() {
        super.onPause();
        setServiceForeground(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setServiceForeground(false);
    }

    /**
     * Exposed to child Activity so that showing service behavior can be modified as desired
     * @param isForeground
     */
    protected void setServiceForeground(boolean isForeground) {

        BaseServiceLocator baseServiceLocator = getServiceLocator();
        if(baseServiceLocator != null) {

            BaseRmDataSource baseRmDataSource = baseServiceLocator.getRmDataSource();

            if(baseRmDataSource != null && baseRmDataSource.isServiceConnected()) {

                baseRmDataSource.setServiceForeground(isForeground);

            }

        }

    }
}
