package com.w3engineers.unicef.telemesh.ui.chat;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;
import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.GroupMessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.ChatEntityListDataSource;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.ui.editprofile.EditProfileViewModel;
import com.w3engineers.unicef.util.helper.StatusHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatPagedAdapterRevisedTest {

    private String userId = "0xaa2dd785fc60eeb8151f65b3ded59ce3c2f12ca4";
    @Rule
    public ActivityTestRule<ChatActivity> rule = new ActivityTestRule<ChatActivity>(ChatActivity.class) {

        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra(UserEntity.class.getName(), userId);
            return intent;
        }
    };
    private ChatPagedAdapterRevised adapterRevised;
    private Context mContext;
    private static ChatViewModel SUT = null;
    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 70;
    private static final int PREFETCH_DISTANCE = 30;
    private UserDataSource userDataSource;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        //SUT = new ChatViewModel(rule.getActivity().getApplication());
        userDataSource = UserDataSource.getInstance();
        addSampleUser();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void message_incoming_outgoing_test() {

        addDelay(1000);

        currentActivity = getActivityInstance();

        RecyclerView recyclerView = currentActivity.findViewById(R.id.chat_rv);
        adapterRevised = (ChatPagedAdapterRevised) recyclerView.getAdapter();

        MessageEntity messageVideoIncomingEntity = new MessageEntity();
        messageVideoIncomingEntity.setIncoming(true);
        messageVideoIncomingEntity.setContentPath("path");
        messageVideoIncomingEntity.setMessageType(Constants.MessageType.VIDEO_MESSAGE);
        messageVideoIncomingEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        messageVideoIncomingEntity.setFriendsId("userId");

        MessageEntity messageVideoOutgoingEntity = new MessageEntity();
        messageVideoOutgoingEntity.setIncoming(false);
        messageVideoOutgoingEntity.setContentPath("path");
        messageVideoOutgoingEntity.setMessageType(Constants.MessageType.VIDEO_MESSAGE);
        messageVideoOutgoingEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        messageVideoOutgoingEntity.setFriendsId("userId");


        GroupMessageEntity groupTextInEntity = new GroupMessageEntity();
        groupTextInEntity.setIncoming(true);
        groupTextInEntity.setMessageType(Constants.MessageType.TEXT_MESSAGE);
        groupTextInEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        groupTextInEntity.setFriendsId("userId");


        GroupMessageEntity groupImageInEntity = new GroupMessageEntity();
        groupImageInEntity.setIncoming(true);
        groupImageInEntity.setMessageType(Constants.MessageType.IMAGE_MESSAGE);
        groupImageInEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        groupImageInEntity.setFriendsId("userId");

        GroupMessageEntity groupImageOutEntity = new GroupMessageEntity();
        groupImageOutEntity.setIncoming(true);
        groupImageOutEntity.setMessageType(Constants.MessageType.IMAGE_MESSAGE);
        groupImageOutEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        groupImageOutEntity.setFriendsId("userId");


        GroupMessageEntity groupVideoInEntity = new GroupMessageEntity();
        groupVideoInEntity.setIncoming(true);
        groupVideoInEntity.setMessageType(Constants.MessageType.VIDEO_MESSAGE);
        groupVideoInEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        groupVideoInEntity.setFriendsId("userId");

        GroupMessageEntity groupVideoOutEntity = new GroupMessageEntity();
        groupVideoOutEntity.setIncoming(true);
        groupVideoOutEntity.setMessageType(Constants.MessageType.VIDEO_MESSAGE);
        groupVideoOutEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        groupVideoOutEntity.setFriendsId("userId");


        List<ChatEntity> chatList = new ArrayList<>();
        chatList.add(messageVideoIncomingEntity);
        chatList.add(messageVideoOutgoingEntity);
        chatList.add(groupTextInEntity);
        chatList.add(groupImageInEntity);
        chatList.add(groupImageOutEntity);
        chatList.add(groupVideoInEntity);
        chatList.add(groupVideoOutEntity);


        addDelay(4000);

        ChatEntityListDataSource chatEntityListDataSource = new ChatEntityListDataSource(chatList);

        PagedList.Config myConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setPageSize(PAGE_SIZE)
                .build();


        PagedList<ChatEntity> pagedStrings = new PagedList.Builder<>(chatEntityListDataSource, myConfig)
                .setInitialKey(INITIAL_LOAD_KEY)
                .setNotifyExecutor(new MainThreadExecutor()) //The executor defining where page loading updates are dispatched.
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        adapterRevised.submitList(pagedStrings);

        addDelay(2000);

        StatusHelper.out("Test message_incoming_outgoing_test executed");
    }

    private UserEntity addSampleUser() {
        UserEntity userEntity = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_MESH_ONLINE)
                .setMeshId("0xaa2dd785fc60eeb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Daniel")
                .setUserLastName("Craig")
                .setIsFavourite(Constants.FavouriteStatus.UNFAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());
        //userEntity.setId(0);

        userDataSource.insertOrUpdateData(userEntity);

        return userEntity;
    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Activity currentActivity = null;

    public Activity getActivityInstance() {
        getInstrumentation().runOnMainSync(() -> {
            Collection resumedActivities =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = (Activity) resumedActivities.iterator().next();
            }
        });

        return currentActivity;
    }
}