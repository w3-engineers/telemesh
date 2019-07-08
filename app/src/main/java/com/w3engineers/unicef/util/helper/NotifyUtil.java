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
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class NotifyUtil {
    private static final String CHANNEL_NAME = "tele_mesh";
    private static final String CHANNEL_ID = "notification_channel";
    private static final UserDataSource userDataSource = UserDataSource.getInstance();

    public static void showNotification(@NonNull ChatEntity chatEntity) {
        Context context = TeleMeshApplication.getContext();

        Intent intent = new Intent(context, ChatActivity.class);
        intent.setAction(Long.toString(TimeUtil.toCurrentTime()));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        UserEntity userEntity = userDataSource.getSingleUserById(chatEntity.getFriendsId());

        if (userEntity != null) {
            intent.putExtra(UserEntity.class.getName(), userEntity);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = getNotificationBuilder(context);
            builder.setContentIntent(pendingIntent);
            prepareNotification(builder, chatEntity, userEntity);
            showNotification(context, builder, chatEntity.getFriendsId());
        }
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

    private static void prepareNotification(NotificationCompat.Builder builder,
                                            ChatEntity chatEntity, UserEntity userEntity) {
        String message = "";

        Bitmap imageBitmap = ImageUtil.getUserImageBitmap(userEntity.avatarIndex);

        if (chatEntity instanceof MessageEntity) {
            message = ((MessageEntity) chatEntity).getMessage();

        }

        builder.setWhen(TimeUtil.toCurrentTime())
                .setContentText(message)
                .setContentTitle(userEntity.getFullName())
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH).setVibrate(new long[0])
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_app_launcher)
                .setLargeIcon(imageBitmap);

        if(SharedPref.getSharedPref(TeleMeshApplication.getContext()).readBoolean(Constants.preferenceKey.IS_NOTIFICATION_ENABLED)){
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

}
