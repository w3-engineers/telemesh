package com.w3engineers.unicef.telemesh.data.local.grouptable;

public class GroupAdminInfo {
    String i;
    boolean s;

    public String getAdminId() {
        return i;
    }

    public void setAdminId(String userId) {
        this.i = userId;
    }

    public boolean getAdminStatus() {
        return s;
    }

    public void setAdminStatus(boolean status) {
        this.s = status;
    }
}
