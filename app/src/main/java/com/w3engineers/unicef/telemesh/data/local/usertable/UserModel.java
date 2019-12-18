package com.w3engineers.unicef.telemesh.data.local.usertable;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class UserModel {

    private String n, userId;
    private int i, cv;
    private long t;

    public String getName() {
        return n;
    }

    public UserModel setName(String name) {
        this.n = name;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public UserModel setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public int getImage() {
        return i;
    }

    public UserModel setImage(int image) {
        this.i = image;
        return this;
    }

    public long getTime() {
        return t;
    }

    public UserModel setTime(long time) {
        this.t = time;
        return this;
    }

    public int getConfigVersion() {
        return cv;
    }

    public UserModel setConfigVersion(int cv) {
        this.cv = cv;
        return this;
    }
}
