package com.w3engineers.unicef.telemesh.data.analytics.model;

public class GroupCountParseModel {
    private String groupId;
    private String submittedBy;
    private String creationDate;
    private String groupOwner;
    private int memberCount;
    private boolean isDirectSend;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public boolean isDirectSend() {
        return isDirectSend;
    }

    public void setDirectSend(boolean directSend) {
        isDirectSend = directSend;
    }
}
