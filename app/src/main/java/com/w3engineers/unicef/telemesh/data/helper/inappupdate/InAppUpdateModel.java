package com.w3engineers.unicef.telemesh.data.helper.inappupdate;

/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class InAppUpdateModel {
    private String vn;
    private int vc;
    private String un;
    private int ut;

    public String getVersionName() {
        return vn;
    }

    public void setVersionName(String vn) {
        this.vn = vn;
    }

    public int getVersionCode() {
        return vc;
    }

    public void setVersionCode(int vc) {
        this.vc = vc;
    }

    public String getUpdateLink() {
        return un;
    }

    public void setUpdateLink(String un) {
        this.un = un;
    }

    public void setUpdateType(int type){
        this.ut = type;
    }

    public int getUpdateType(){
        return ut;
    }
}
