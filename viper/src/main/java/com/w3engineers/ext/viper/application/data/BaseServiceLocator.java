package com.w3engineers.ext.viper.application.data;


import com.w3engineers.ext.viper.application.data.remote.BaseRmDataSource;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public abstract class BaseServiceLocator {

    public abstract BaseRmDataSource getRmDataSource();

}
