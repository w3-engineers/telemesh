package com.w3engineers.unicef.telemesh.data.local.feed;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class BroadcastMeta {

    private String m, t, h, u, a;

    public String getUploaderName() {
        return u;
    }

    public BroadcastMeta setUploaderName(String u) {
        this.u = u;
        return this;
    }

    public String getMessageTitle() {
        return h;
    }

    public BroadcastMeta setMessageTitle(String h) {
        this.h = h;
        return this;
    }

    public String getMessageBody() {
        return m;
    }

    public BroadcastMeta setMessageBody(String m) {
        this.m = m;
        return this;
    }

    public String getCreationTime() {
        return t;
    }

    public BroadcastMeta setCreationTime(String t) {
        this.t = t;
        return this;
    }

    public String getBroadcastAddress() {
        return a;
    }

    public BroadcastMeta setBroadcastAddress(String a) {
        this.a = a;
        return this;
    }
}
