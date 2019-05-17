package com.w3engineers.unicef.telemesh.ui.dataplan.model;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class BuyerUser {

    private String userName, usageData;
    private int activeMode;

    public String getUserName() {
        return userName;
    }

    public BuyerUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUsageData() {
        return usageData;
    }

    public BuyerUser setUsageData(String usageData) {
        this.usageData = usageData;
        return this;
    }

    public int getActiveMode() {
        return activeMode;
    }

    public BuyerUser setActiveMode(int active) {
        activeMode = active;
        return this;
    }
}
