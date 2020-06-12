package com.w3engineers.unicef.telemesh.data.local.grouptable;

import java.util.ArrayList;
import java.util.List;

public class GroupNameModel {
    private String gn;
    private List<GroupUserNameMap> gm;

    public String getGroupName() {
        return gn;
    }

    public GroupNameModel setGroupName(String gn) {
        this.gn = gn;
        return this;
    }

    public List<GroupUserNameMap> getGroupUserMap() {
        return gm;
    }

    public GroupNameModel setGroupUserMap(List<GroupUserNameMap> gm) {
        this.gm = gm;
        return this;
    }
}
