package com.w3engineers.unicef.telemesh.data.local.messagetable;

import static org.junit.Assert.assertEquals;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class GroupModelTest {
    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);


    @Before
    public void setUp() {

    }

    @Test
    public void group_model_test() {

        addDelay();

        GroupContentEntity groupContentEntity = new GroupContentEntity();
        int id = 1005;
        String messageId = UUID.randomUUID().toString();
        String userId = "userId";
        groupContentEntity.setId(id)
                .setContentMessageId(messageId)
                .setContentId(messageId)
                .setSenderId(userId)
                .setReceiverId(userId);


        assertEquals(id, groupContentEntity.id);

        addDelay();

        // Group message entity test

        ArrayList<String> receiverList = new ArrayList<>();
        receiverList.add(userId);

        GroupMessageEntity groupMessageEntity = new GroupMessageEntity()
                .setContentStatus(1)
                .setContentThumb("thumb")
                .setReceivedUsers(receiverList).setContentPath("path")
                .setContentInfo(null)
                .setMessage("Hello")
                .setGroupId(messageId)
                .setOriginalSender(userId)
                .setContentProgress(100);

        int contentStatus = groupMessageEntity.getContentStatus();
        String groupId = groupMessageEntity.getGroupId();
        String contentPath = groupMessageEntity.getContentPath();
        String contentThumb = groupMessageEntity.getContentThumb();
        String contentInfo = groupMessageEntity.getContentInfo();
        ArrayList<String> users = groupMessageEntity.getReceivedUsers();
        int contentProgress = groupMessageEntity.getContentProgress();

        assertEquals(contentProgress, groupMessageEntity.getContentProgress());

        addDelay();


        MessageModel messageModel = groupMessageEntity.toMessageModel();

        GroupMessageEntity newMessageEntity = groupMessageEntity.toChatEntity(messageModel);

        assertEquals(messageModel.getGroupId(), newMessageEntity.getGroupId());

    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}