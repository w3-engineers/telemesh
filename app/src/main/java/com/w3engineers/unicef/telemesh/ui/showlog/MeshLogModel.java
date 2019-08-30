package com.w3engineers.unicef.telemesh.ui.showlog;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class MeshLogModel {
    private int type;
    private String log;

    public MeshLogModel(int type, String log) {
        this.type = type;
        this.log = log;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
