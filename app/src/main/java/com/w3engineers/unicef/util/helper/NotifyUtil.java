package com.w3engineers.unicef.util.helper;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.BroadcastDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class NotifyUtil {
    private static final String CHANNEL_NAME = "tele_mesh";
    private static final String CHANNEL_ID = "notification_channel_3";
    private static final UserDataSource userDataSource = UserDataSource.getInstance();
    private static final GroupDataSource groupDataSource = GroupDataSource.getInstance();

    private static final String broadcastMessageId = "broadcastMessageId";
    private static int broadcastMessageCount = 0;

    public static void showNotification(@NonNull ChatEntity chatEntity) {
        Context context = TeleMeshApplication.getContext();

        Intent intent = new Intent(context, ChatActivity.class);
        intent.setAction(Long.toString(TimeUtil.toCurrentTime()));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        UserEntity userEntity = userDataSource.getSingleUserById(chatEntity.getFriendsId());

        MessageEntity messageEntity = ((MessageEntity)chatEntity);
        boolean isGroup = messageEntity.messagePlaceGroup;
        if (userEntity != null) {
            if (isGroup) {
                intent.putExtra(UserEntity.class.getName(), messageEntity.getGroupId());
            } else {
                intent.putExtra(UserEntity.class.getName(), messageEntity.getFriendsId());
            }
            intent.putExtra(GroupEntity.class.getName(), isGroup);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = getNotificationBuilder(context);
            builder.setContentIntent(pendingIntent);
            boolean isSuccess = false;
            String id = null;
            if (isGroup) {
                GroupEntity groupEntity = groupDataSource.getGroupById(messageEntity.getGroupId());
                if (groupEntity != null) {
                    id = groupEntity.getGroupId();
                    isSuccess = prepareNotification(builder, chatEntity, userEntity, groupEntity);
                }
            } else {
                id = chatEntity.getFriendsId();
                isSuccess = prepareNotification(builder, chatEntity, userEntity);
            }

            if (isSuccess) {
                showNotification(context, builder, id);
            }
        }
    }

    public static void showGroupEventNotification(String userId, GroupEntity groupEntity) {
        Context context = TeleMeshApplication.getContext();

        Intent intent = new Intent(context, ChatActivity.class);
        intent.setAction(Long.toString(TimeUtil.toCurrentTime()));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        UserEntity userEntity = userDataSource.getSingleUserById(userId);

        intent.putExtra(UserEntity.class.getName(), groupEntity.getGroupId());
        intent.putExtra(GroupEntity.class.getName(), true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = getNotificationBuilder(context);
        builder.setContentIntent(pendingIntent);

        if (userEntity == null)
            return;

        String message = userEntity.getFullName() + " created a group";

        Bitmap imageBitmap = ImageUtil.getResourceImageBitmap(R.mipmap.group_blue_circle);
        setNotification(builder, message, "Group", imageBitmap);

        showNotification(context, builder, groupEntity.getGroupId());
    }

    public static void showBroadcastEventNotification() {
        Context context = TeleMeshApplication.getContext();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Long.toString(TimeUtil.toCurrentTime()));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BroadcastDataHelper.class.getSimpleName(), true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = getNotificationBuilder(context);
        builder.setContentIntent(pendingIntent);

        broadcastMessageCount = broadcastMessageCount + 1;

        String message = String.format(context.getResources().getString(R.string.broadcast_notification),
                broadcastMessageCount);;

        Bitmap imageBitmap = ImageUtil.getResourceImageBitmap(R.mipmap.group_blue_circle);
        setNotification(builder, message, "Broadcast", imageBitmap);

        showNotification(context, builder, broadcastMessageId);
    }

    public static void cancelBroadcastMessage() {
        broadcastMessageCount = 0;
        clearNotification(broadcastMessageId);
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context context) {
        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
            channelId = channel.getId();
        } else {
            channelId = CHANNEL_ID;
        }

        return new NotificationCompat.Builder(context, channelId);
    }

    private static boolean prepareNotification(NotificationCompat.Builder builder,
                                            ChatEntity chatEntity, UserEntity userEntity) {
        String message = "";

        Bitmap imageBitmap = ImageUtil.getUserImageBitmap(userEntity.avatarIndex);

        if (chatEntity instanceof MessageEntity) {
            message = ((MessageEntity) chatEntity).getMessage();
        }

        setNotification(builder, message, userEntity.getFullName(), imageBitmap);

        return true;
    }

    private static boolean prepareNotification(NotificationCompat.Builder builder, ChatEntity chatEntity,
                                            UserEntity userEntity, GroupEntity groupEntity) {
        String message = "";

        Bitmap imageBitmap = ImageUtil.getResourceImageBitmap(R.mipmap.group_blue_circle);

        if (chatEntity instanceof MessageEntity) {
            message = ((MessageEntity) chatEntity).getMessage();
        }

        String groupName = groupEntity.getGroupName();
        if (TextUtils.isEmpty(groupName)) {
            return false;
        }

        GroupMembersInfo groupMembersInfo = CommonUtil.getGroupMemberInfo(groupEntity.getMembersInfo(),
                userEntity.getMeshId());
        String userName = groupMembersInfo != null ? groupMembersInfo.getUserName() : "";

        GroupNameModel groupNameModel = GsonBuilder.getInstance().getGroupNameModelObj(groupName);

        groupName = groupNameModel.isGroupNameChanged() ? groupNameModel.getGroupName() : "a group";

        String title = userName + " messaged in " + groupName;
        setNotification(builder, message, title, imageBitmap);
        return true;
    }

    private static void setNotification(NotificationCompat.Builder builder, String message,
                                 String title, Bitmap imageBitmap) {
        builder.setWhen(TimeUtil.toCurrentTime())
                .setContentText(message)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH).setVibrate(new long[0])
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(imageBitmap);

        if (SharedPref.readBoolean(Constants.preferenceKey.IS_NOTIFICATION_ENABLED)) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
        }
    }

    /**
     * Responsible to show notification
     *
     * @param builder(required) need to
     * @param id(required)      notification id
     */
    private static void showNotification(Context context, NotificationCompat.Builder builder, String id) {

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyId = Math.abs(id.hashCode());
        if (notificationManager != null) {
            notificationManager.notify(notifyId, notification);
        }
    }

    public static void clearNotification(String notificationValue) {
        Context context = TeleMeshApplication.getContext();
        if (!TextUtils.isEmpty(notificationValue)) {
            int notificationId = Math.abs(notificationValue.hashCode());
            NotificationManager manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
    }
}
