package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.w3engineers.mesh.util.DialogUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonUtil {

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static void showPermissionPopUp(Context mContext) {
        DialogUtil.showConfirmationDialog(mContext,
                mContext.getResources().getString(R.string.permission),
                mContext.getResources().getString(R.string.permission_for_signup),
                null,
                mContext.getString(R.string.ok),
                new DialogUtil.DialogButtonListener() {
                    @Override
                    public void onClickPositive() {
                       mContext.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }

                    @Override
                    public void onCancel() {

                    }
                    @Override
                    public void onClickNegative() {

                    }
                });
    }

   /* public static boolean isLocationGpsOn(Context context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }*/

    public static void showGpsOrLocationOffPopup(Context mContext) {
        DialogUtil.showConfirmationDialog(mContext,
                mContext.getResources().getString(R.string.gps_alert),
                mContext.getResources().getString(R.string.for_better_performance),
                null,
                mContext.getString(R.string.ok),
                new DialogUtil.DialogButtonListener() {
                    @Override
                    public void onClickPositive() {
                        //mContext.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }

                    @Override
                    public void onCancel() {

                    }
                    @Override
                    public void onClickNegative() {

                    }
                });
    }

    public static boolean isValidName(String name, Context context) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, context.getResources().getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (name.length() < 2) {
            Toast.makeText(context, context.getResources().getString(R.string.enter_valid_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static void dismissDialog(){
        DialogUtil.dismissDialog();
    }

    /*public static String getGroupName(List<GroupUserNameMap> userNameMaps) {
        String groupName = "";
        for (GroupUserNameMap groupUserNameMap : userNameMaps) {
            if (TextUtils.isEmpty(groupName)) {
                groupName = groupUserNameMap.getUserName();
            } else {
                groupName = groupName + ", " + groupUserNameMap.getUserName();
            }
        }
        return groupName;
    }*/

    public static String getGroupNameByUser(List<GroupMembersInfo> groupMembersInfos) {
        String groupName = "";
        for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
            if (groupMembersInfo.getMemberStatus() == Constants.GroupEvent.GROUP_JOINED) {
                if (TextUtils.isEmpty(groupName)) {
                    groupName = groupMembersInfo.getUserName();
                } else {
                    groupName = groupName + ", " + groupMembersInfo.getUserName();
                }
            }
        }
        return groupName;
    }

    public static List<String> getGroupMembersId(List<GroupMembersInfo> groupMembersInfos) {
        List<String> groupMembersId = new ArrayList<>();
        for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
            groupMembersId.add(groupMembersInfo.getMemberId());
        }
        return groupMembersId;
    }

    public static List<String> getGroupLiveMembersId(List<GroupMembersInfo> groupMembersInfos) {
        List<String> groupMembersId = new ArrayList<>();
        for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
            if (groupMembersInfo.getMemberStatus() == Constants.GroupEvent.GROUP_JOINED) {
                groupMembersId.add(groupMembersInfo.getMemberId());
            }
        }
        return groupMembersId;
    }

    public static ArrayList<GroupMembersInfo> mergeGroupMembersInfo(ArrayList<GroupMembersInfo> existingMembers,
                                             List<GroupMembersInfo> newMembers) {
        HashMap<String, GroupMembersInfo> groupMembersMap = new HashMap<>();
        for (GroupMembersInfo groupMembersInfo : newMembers) {
            groupMembersMap.put(groupMembersInfo.getMemberId(), groupMembersInfo);
        }

        for (int i = 0; i < existingMembers.size(); i++) {

            GroupMembersInfo groupMembersInfo = existingMembers.get(i);
            String userId = groupMembersInfo.getMemberId();
            GroupMembersInfo newMemberInfo = groupMembersMap.get(userId);

            if (newMemberInfo != null) {
                groupMembersInfo.setUserName(newMemberInfo.getUserName())
                        .setMemberStatus(newMemberInfo.getMemberStatus());
                existingMembers.set(i, groupMembersInfo);
                groupMembersMap.remove(userId);
            }
        }

        for (GroupMembersInfo groupMembersInfo : newMembers) {
            String userId = groupMembersInfo.getMemberId();
            GroupMembersInfo newMemberInfo = groupMembersMap.get(userId);
            if (newMemberInfo != null) {
                existingMembers.add(groupMembersInfo);
            }
        }
        return existingMembers;
    }

    public static String getGroupUsersName(List<UserEntity> userEntities, String myMeshId) {
        String groupName = "";
        if (userEntities != null) {
            for (UserEntity userEntity : userEntities) {
                String name = userEntity.getUserName();
                if (myMeshId.equals(userEntity.getMeshId())) {
                    name = "You";
                }
                if (TextUtils.isEmpty(groupName)) {
                    groupName = name;
                } else {
                    groupName = groupName + ", " + name;
                }
            }
        }
        return groupName;
    }

    public static String getUserName(GroupEntity groupEntity, String myUserId) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();
        if (groupEntity.lastPersonId.equals(myUserId)) {
            return "You";
        } else {
            if (!TextUtils.isEmpty(groupEntity.lastPersonName)) {
                return groupEntity.lastPersonName;
            } else {
                List<GroupMembersInfo> groupMembersInfos = gsonBuilder
                        .getGroupMemberInfoObj(groupEntity.getMembersInfo());
                for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
                    if (groupEntity.lastPersonId.equals(groupMembersInfo.getMemberId())) {
                        return groupMembersInfo.getUserName();
                    }
                }
            }
        }
        return "";
    }

    public static GroupMembersInfo getGroupMemberInfo(String groupMemberInfoText, String memberId) {
        List<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(groupMemberInfoText);
        for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
            if (groupMembersInfo.getMemberId().equals(memberId))
                return groupMembersInfo;
        }
        return null;
    }
}
