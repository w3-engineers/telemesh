package com.w3engineers.unicef.telemesh.data.local.grouptable;

public class GroupModel {

    String n, i, ai, mi, ii;
    int a;
    long c;
    boolean is;

    public boolean isSynced() {
        return is;
    }

    public GroupModel setSynced(boolean is) {
        this.is = is;
        return this;
    }

    public String getGroupName() {
        return n;
    }

    public GroupModel setGroupName(String name) {
        this.n = name;
        return this;
    }

    public String getGroupId() {
        return i;
    }

    public GroupModel setGroupId(String id) {
        this.i = id;
        return this;
    }

    public String getAdminInfo() {
        return ai;
    }

    public GroupModel setAdminInfo(String adminInfo) {
        this.ai = adminInfo;
        return this;
    }

    public String getMemberInfo() {
        return mi;
    }

    public GroupModel setMemberInfo(String memberInfo) {
        this.mi = memberInfo;
        return this;
    }

    public String getInfoId() {
        return ii;
    }

    public GroupModel setInfoId(String infoId) {
        this.ii = infoId;
        return this;
    }

    public int getAvatar() {
        return a;
    }

    public GroupModel setAvatar(int avatar) {
        this.a = avatar;
        return this;
    }

    public long getCreatedTime() {
        return c;
    }

    public GroupModel setCreatedTime(long createdTime) {
        this.c = createdTime;
        return this;
    }
}
