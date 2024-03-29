package com.w3engineers.unicef.util.helper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastMeta;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedContentModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.ForwardGroupModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupCountModel;
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
        try{
            Type founderListType = new TypeToken<ArrayList<GroupMembersInfo>>(){}.getType();
            return gson.fromJson(memberInfoText, founderListType);
        }catch(JsonSyntaxException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getGroupCountJson(ArrayList<GroupCountModel> groupCountModels) {
        return gson.toJson(groupCountModels);
    }

    public ArrayList<GroupCountModel> getGroupCountModels(String groupCountText) {
        Type founderListType = new TypeToken<ArrayList<GroupCountModel>>(){}.getType();
        return gson.fromJson(groupCountText, founderListType);
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
        try{
            return gson.fromJson(groupNameModelText, GroupNameModel.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getFeedContentModelJson(FeedContentModel relayGroupModel) {
        return gson.toJson(relayGroupModel);
    }

    public FeedContentModel getFeedContentModelObj(String feedContentModel) {
        return gson.fromJson(feedContentModel, FeedContentModel.class);
    }

    public String getBroadcastMetaJson(BroadcastMeta broadcastMeta) {
        return gson.toJson(broadcastMeta);
    }

    public BroadcastMeta getBroadcastMetaObj(String bulletinModel) {
        return gson.fromJson(bulletinModel, BroadcastMeta.class);
    }
}
