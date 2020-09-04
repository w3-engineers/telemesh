package com.w3engineers.unicef.telemesh.data.local.feed;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class BulletinModel {

    private String i, m, t, h, c, u;

    public String getId() {
        return i;
    }

    public BulletinModel setId(String i) {
        this.i = i;
        return this;
    }

    public String getMessage() {
        return m;
    }

    public BulletinModel setMessage(String m) {
        this.m = m;
        return this;
    }

    public String getTime() {
        return t;
    }

    public BulletinModel setTime(String t) {
        this.t = t;
        return this;
    }

    public String getHeaderTitle() {
        return h;
    }

    public BulletinModel setHeaderTitle(String h) {
        this.h = h;
        return this;
    }

    public String getContentUrl() {
        return c;
    }

    public BulletinModel setContentUrl(String c) {
        this.c = c;
        return this;
    }

    public String getUploaderName() {
        return u;
    }

    public BulletinModel setUploaderName(String u) {
        this.u = u;
        return this;
    }
}
