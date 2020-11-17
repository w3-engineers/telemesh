package com.w3engineers.unicef.util.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.w3engineers.unicef.telemesh.data.helper.BroadcastDataModel;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedContentModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.ForwardGroupModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.RelayGroupModel;
import com.w3engineers.unicef.util.helper.model.ContentInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

    public String getRelayGroupModelJson(RelayGroupModel relayGroupModel) {
        return gson.toJson(relayGroupModel);
    }

    public RelayGroupModel getRelayGroupModelObj(String relayGroupModelText) {
        return gson.fromJson(relayGroupModelText, RelayGroupModel.class);
    }

    public String getForwarderGroupModelJson(ForwardGroupModel forwardGroupModel) {
        return gson.toJson(forwardGroupModel);
    }

    public ForwardGroupModel getForwarderGroupModelObj(String forwardGroupModelText) {
        return gson.fromJson(forwardGroupModelText, ForwardGroupModel.class);
    }

    public String getGroupModelJson(GroupModel groupModel) {
        return gson.toJson(groupModel);
    }

    public GroupModel getGroupModelObj(String groupModelText) {
        return gson.fromJson(groupModelText, GroupModel.class);
    }

    public String getGroupNameModelJson(GroupNameModel groupNameModel) {
        return gson.toJson(groupNameModel);
    }

    public GroupNameModel getGroupNameModelObj(String groupNameModelText) {
        return gson.fromJson(groupNameModelText, GroupNameModel.class);
    }

    public String getFeedContentModelJson(FeedContentModel relayGroupModel) {
        return gson.toJson(relayGroupModel);
    }

    public FeedContentModel getFeedContentModelObj(String feedContentModel) {
        return gson.fromJson(feedContentModel, FeedContentModel.class);
    }

    public String getBulletinModelJson(BulletinModel bulletinModel) {
        return gson.toJson(bulletinModel);
    }

    public BulletinModel getBulletinModelObj(String bulletinModel) {
        return gson.fromJson(bulletinModel, BulletinModel.class);
    }

    public String getBroadcastDataModelJson(BroadcastDataModel broadcastDataModel) {
        return gson.toJson(broadcastDataModel);
    }

    public BroadcastDataModel getBroadcastDataModelObj(String broadcastDataModel) {
        return gson.fromJson(broadcastDataModel, BroadcastDataModel.class);
    }
}
