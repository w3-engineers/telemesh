package com.w3engineers.unicef.telemesh.data.local.grouptable;

public class GroupMembersInfo {
    String i;
    int s;

    public String getMemberId() {
        return i;
    }

    public GroupMembersInfo setMemberId(String userId) {
        this.i = userId;
        return this;
    }

    public int getMemberStatus() {
        return s;
    }

    public GroupMembersInfo setMemberStatus(int status) {
        this.s = status;
        return this;
    }
}
