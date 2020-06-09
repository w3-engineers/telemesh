package com.w3engineers.unicef.util.helper;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupAdminInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.util.helper.model.ContentInfo;

public class GsonBuilder {

    private static Gson gson = new Gson();
    private static GsonBuilder gsonBuilder = new GsonBuilder();

    public static GsonBuilder getInstance() {
        return gsonBuilder;
    }

    public String getContentInfoJson(ContentInfo contentInfo) {
        return gson.toJson(contentInfo);
    }

    public ContentInfo getContentInfoObj(String contentInfoText) {
        return gson.fromJson(contentInfoText, ContentInfo.class);
    }

    public String getGroupMemberInfoJson(GroupMembersInfo groupMembersInfo) {
        return gson.toJson(groupMembersInfo);
    }

    public GroupMembersInfo getGroupMemberInfoObj(String memberInfoText) {
        return gson.fromJson(memberInfoText, GroupMembersInfo.class);
    }

    public String getGroupAdminInfoJson(GroupAdminInfo groupAdminInfo) {
        return gson.toJson(groupAdminInfo);
    }

    public GroupAdminInfo getGroupAdminInfoObj(String adminInfoText) {
        return gson.fromJson(adminInfoText, GroupAdminInfo.class);
    }
}
