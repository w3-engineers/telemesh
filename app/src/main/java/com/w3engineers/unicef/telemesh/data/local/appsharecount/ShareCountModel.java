package com.w3engineers.unicef.telemesh.data.local.appsharecount;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class ShareCountModel {
    private String i, t;
    private int c;

    public String getId() {
        return i;
    }

    public ShareCountModel setId(String i) {
        this.i = i;
        return this;
    }

    public String getTime() {
        return t;
    }

    public ShareCountModel setTime(String t) {
        this.t = t;
        return this;
    }

    public int getCount() {
        return c;
    }

    public ShareCountModel setCount(int c) {
        this.c = c;
        return this;
    }
}
