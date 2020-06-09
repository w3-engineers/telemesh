package com.w3engineers.unicef.telemesh.data.local.grouptable;

public class GroupMembersInfo {
    String i;
    int s;

    public String getMemberId() {
        return i;
    }

    public void setMemberId(String userId) {
        this.i = userId;
    }

    public int getMemberStatus() {
        return s;
    }

    public void setMemberStatus(int status) {
        this.s = status;
    }
}
