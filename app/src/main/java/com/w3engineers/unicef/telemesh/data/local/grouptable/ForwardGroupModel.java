package com.w3engineers.unicef.telemesh.data.local.grouptable;

public class ForwardGroupModel {
    String d, s;
    byte t;

    public String getData() {
        return d;
    }

    public ForwardGroupModel setData(String d) {
        this.d = d;
        return this;
    }

    public String getSender() {
        return s;
    }

    public ForwardGroupModel setSender(String s) {
        this.s = s;
        return this;
    }

    public byte getType() {
        return t;
    }

    public ForwardGroupModel setType(byte type) {
        this.t = type;
        return this;
    }
}
