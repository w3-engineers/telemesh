package com.w3engineers.unicef.telemesh.data.local.grouptable;

public class GroupNameModel {
    private String n;
    private boolean c;

    public String getGroupName() {
        return n;
    }

    public GroupNameModel setGroupName(String groupName) {
        this.n = groupName;
        return this;
    }

    public boolean isGroupNameChanged() {
        return c;
    }

    public GroupNameModel setGroupNameChanged(boolean isChanged) {
        this.c = isChanged;
        return this;
    }
}
