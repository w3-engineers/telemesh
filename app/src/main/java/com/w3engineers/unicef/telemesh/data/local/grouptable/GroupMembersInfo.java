package com.w3engineers.unicef.telemesh.data.local.grouptable;

import android.renderscript.BaseObj;

public class GroupMembersInfo {
    String i, n, ln;
    int s, p;
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

    public int getAvatarPicture() {
        return p;
    }

    public GroupMembersInfo setAvatarPicture(int picture) {
        this.p = picture;
        return this;
    }

    public GroupMembersInfo setLastName(String lastName) {
        this.ln = lastName;
        return this;
    }

    public String getLsatName() {
        return ln;
    }
}
