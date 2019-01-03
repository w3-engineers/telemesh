package com.w3engineers.ext.viper.application.data;


import com.w3engineers.ext.viper.application.data.remote.BaseRmDataSource;

/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-17 at 10:23 AM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-17 at 10:23 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-17 at 10:23 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public abstract class BaseServiceLocator {

    public abstract BaseRmDataSource getRmDataSource();

}
