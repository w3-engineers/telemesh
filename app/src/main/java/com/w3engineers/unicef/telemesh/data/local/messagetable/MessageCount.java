package com.w3engineers.unicef.telemesh.data.local.messagetable;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class MessageCount {
    private String i;
    private int c;
    private long t;

    public String getId() {
        return i;
    }

    public MessageCount setId(String i) {
        this.i = i;
        return this;
    }

    public int getCount() {
        return c;
    }

    public MessageCount setCount(int c) {
        this.c = c;
        return this;
    }

    public long getTime() {
        return t;
    }

    public MessageCount setTime(long t) {
        this.t = t;
        return this;
    }
}
