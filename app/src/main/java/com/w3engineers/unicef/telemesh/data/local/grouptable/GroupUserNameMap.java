package com.w3engineers.unicef.telemesh.data.local.grouptable;

public class GroupUserNameMap {

    private String ui, un;

    public String getUserId() {
        return ui;
    }

    public GroupUserNameMap setUserId(String ui) {
        this.ui = ui;
        return this;
    }

    public String getUserName() {
        return un;
    }

    public GroupUserNameMap setUserName(String un) {
        this.un = un;
        return this;
    }
}
