package com.w3engineers.unicef.util.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupAdminInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;
import com.w3engineers.unicef.util.helper.model.ContentInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public String getGroupMemberInfoJson(ArrayList<GroupMembersInfo> groupMembersInfos) {
        return gson.toJson(groupMembersInfos);
    }

    public ArrayList<GroupMembersInfo> getGroupMemberInfoObj(String memberInfoText) {
        Type founderListType = new TypeToken<ArrayList<GroupMembersInfo>>(){}.getType();
        return gson.fromJson(memberInfoText, founderListType);
    }

    public String getGroupAdminInfoJson(ArrayList<GroupAdminInfo> groupAdminInfos) {
        return gson.toJson(groupAdminInfos);
    }

    public ArrayList<GroupAdminInfo> getGroupAdminInfoObj(String adminInfoText) {
        Type founderListType = new TypeToken<ArrayList<GroupAdminInfo>>(){}.getType();
        return gson.fromJson(adminInfoText, founderListType);
    }

    public String getGroupModelJson(GroupModel groupModel) {
        return gson.toJson(groupModel);
    }

    public GroupModel getGroupModelObj(String groupModelText) {
        return gson.fromJson(groupModelText, GroupModel.class);
    }
}
