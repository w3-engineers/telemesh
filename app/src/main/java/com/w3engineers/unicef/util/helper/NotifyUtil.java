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
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 1/23/2018 at 11:24 AM.
 *  *
 *  * Last edited by : Mohd. Asfaq-E-Azam Rifat on 29-Jan-18.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

public class NotifyUtil {
    public static final String CHANNEL_NAME = "tele_mesh";
    public static final String CHANNEL_ID = "notification_channel";
    private static final UserDataSource userDataSource = UserDataSource.getInstance();
    private final String LOCAL_RESOURCE_SCHEME = "res";


    public static void showNotification(ChatEntity chatEntity) {
        Context context = TeleMeshApplication.getContext();

        Intent intent = new Intent(context, ChatActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));
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
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        return builder;
    }

    private static void prepareNotification(NotificationCompat.Builder builder,
                                            ChatEntity chatEntity, UserEntity userEntity) {
        String message = "";

        Bitmap imageBitmap = ImageUtil.getUserImageBitmap(userEntity.avatarIndex);

        if (chatEntity instanceof MessageEntity) {
            message = ((MessageEntity) chatEntity).getMessage();

        }

        builder.setWhen(System.currentTimeMillis())
                .setContentText(message)
                .setContentTitle(userEntity.getFullName())
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH).setVibrate(new long[0])
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.logo_telemesh)
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
