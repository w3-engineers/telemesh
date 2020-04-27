package com.w3engineers.unicef.telemesh.data.helper;

public class ContentMessageModel {
    private String i;
    private byte t;
    private int mt;

    public String getMessageId() {
        return i;
    }

    public ContentMessageModel setMessageId(String i) {
        this.i = i;
        return this;
    }

    public byte getContentType() {
        return t;
    }

    public ContentMessageModel setContentType(byte t) {
        this.t = t;
        return this;
    }

    public int getMessageType() {
        return mt;
    }

    public ContentMessageModel setMessageType(int mt) {
        this.mt = mt;
        return this;
    }
}
