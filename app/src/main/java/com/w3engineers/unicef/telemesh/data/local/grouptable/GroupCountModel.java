package com.w3engineers.unicef.telemesh.data.local.grouptable;

public class GroupCountModel {
    String i, sb, cd, gw;
    int mc;
    boolean ds;

    public String getGroupId() {
        return i;
    }
    public GroupCountModel setGroupId(String id) {
        this.i = id;
        return this;
    }

    public String getGroupOwnerId() {
        return gw;
    }
    public GroupCountModel setGroupOwnerId(String ownerId) {
        this.gw = ownerId;
        return this;
    }

    public int getMemberCount() {
        return mc;
    }
    public GroupCountModel setMemberCount(int memberCount) {
        this.mc = memberCount;
        return this;
    }

    public String getCreatedTime() {
        return cd;
    }
    public GroupCountModel setCreatedTime(String createdTime) {
        this.cd = createdTime;
        return this;
    }

    public String getSubmittedBy() {
        return sb;
    }
    public GroupCountModel setSubmittedBy(String submittedBy) {
        this.sb = submittedBy;
        return this;
    }

    public boolean getDirectSend() {
        return ds;
    }
    public GroupCountModel setDirectSend(boolean directSend) {
        this.ds = directSend;
        return this;
    }
}
