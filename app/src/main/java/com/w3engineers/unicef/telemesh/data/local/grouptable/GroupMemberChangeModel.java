package com.w3engineers.unicef.telemesh.data.local.grouptable;

import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberChangeModel {
    public GroupEntity groupEntity;
    public ArrayList<GroupMembersInfo> newMembersInfo;
    public UserEntity removeUser;
    public String infoId;
}
