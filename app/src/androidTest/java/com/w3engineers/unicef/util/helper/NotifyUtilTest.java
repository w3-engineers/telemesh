package com.w3engineers.unicef.util.helper;

import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class NotifyUtilTest {

    private Context mContext;
    private static final String CHANNEL_ID = "notification_channel_9";
    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testPrepareNotification(){
        assertTrue(true);
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        ChatEntity chatEntity = new ChatEntity();

        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setGroupName("test data");
        groupEntity.setMembersInfo(gsonBuilder.getGroupMemberInfoJson(gsonBuilder
                .getGroupMemberInfoObj("{}")));


        UserEntity userEntity = new UserEntity();
        userEntity.setMeshId("testMeshId");
        NotifyUtil.prepareNotification(builder,chatEntity,userEntity, groupEntity);
        assertTrue(true);
    }
}
