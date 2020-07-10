package com.w3engineers.unicef.telemesh.data.local.grouptable;

import android.renderscript.BaseObj;

public class GroupMembersInfo {
    String i, n;
    int s;
    boolean a;

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

    public boolean isAdmin() {
        return a;
    }

    public GroupMembersInfo setIsAdmin(boolean isAdmin) {
        this.a = isAdmin;
        return this;
    }

    public String getUserName() {
        return n;
    }

    public GroupMembersInfo setUserName(String n) {
        this.n = n;
        return this;
    }
}
