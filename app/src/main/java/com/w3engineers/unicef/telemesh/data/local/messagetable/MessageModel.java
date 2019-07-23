package com.w3engineers.unicef.telemesh.data.local.messagetable;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class MessageModel {

    private String m, i;
    private int t;

    public String getMessage() {
        return m;
    }

    public MessageModel setMessage(String m) {
        this.m = m;
        return this;
    }

    public String getId() {
        return i;
    }

    public MessageModel setId(String i) {
        this.i = i;
        return this;
    }

    public int getType() {
        return t;
    }

    public MessageModel setType(int t) {
        this.t = t;
        return this;
    }
}
