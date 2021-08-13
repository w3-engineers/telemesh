package com.w3engineers.unicef.telemesh.data.helper;

import static org.junit.Assert.assertTrue;

import android.widget.TextView;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.models.ContentMetaInfo;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.StatusHelper;
import com.w3engineers.unicef.util.helper.model.ContentInfo;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class ContentDataHelperTest {

    private RandomEntityGenerator randomEntityGenerator;
    private UserDataSource userDataSource;
    private AppDatabase appDatabase;
    private MessageSourceData messageSourceData;

    private ContentDataHelper contentDataHelper;
    private String userId = "0xaa2dd785fc60eeb8151f65b3ded59ce3c2f12ca4";

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setUp() throws Exception {
        randomEntityGenerator = new RandomEntityGenerator();
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();
        userDataSource = UserDataSource.getInstance();
        messageSourceData = MessageSourceData.getInstance();
        contentDataHelper = ContentDataHelper.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        appDatabase.close();
    }

    @Test
    public void test_prepare_content_and_observer() {

        UserEntity entity = addSampleUser();
        addDelay(1000);

        ChatEntity chatEntity = randomEntityGenerator.createOutgoingContent(entity.getMeshId());

        contentDataHelper.prepareContentObserver((MessageEntity) chatEntity, true);

        addDelay(2000);

        chatEntity.setStatus(Constants.MessageStatus.STATUS_RESEND_START);


        chatEntity.setIncoming(true);
        contentDataHelper.prepareContentObserver((MessageEntity) chatEntity, false);

        addDelay(2000);

        chatEntity.setFriendsId(SharedPref.read(Constants.preferenceKey.MY_USER_ID));
        chatEntity.setIncoming(false);
        contentDataHelper.prepareContentObserver((MessageEntity) chatEntity, false);

        addDelay(2000);

        chatEntity.setIncoming(false);

        ((MessageEntity) chatEntity).setContentId(UUID.randomUUID().toString());

        ContentInfo contentInfo = new ContentInfo();
        contentInfo.setDuration(10);

        String contentJson = GsonBuilder.getInstance().getContentInfoJson(contentInfo);

        TextView textView = new TextView(rule.getActivity());
        UIHelper.setDuration(textView, contentJson);

        addDelay(1000);

        ((MessageEntity) chatEntity).setContentInfo(contentJson);

        contentDataHelper.prepareContentObserver((MessageEntity) chatEntity, false);

        addDelay(2000);

        contentDataHelper.contentMessageSuccessResponse(chatEntity.getMessageId().getBytes());

        addDelay(1000);

        contentDataHelper.contentMessageSuccessResponse(((MessageEntity) chatEntity).getContentId().getBytes());


        assertTrue(true);

    }

    @Test
    public void test_prepare_content_and_send() {

        UserEntity entity = addSampleUser();
        addDelay(1000);

        String contentId1 = UUID.randomUUID().toString();
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("success", true);
            jsonObject.put("msg", contentId1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentModel contentModel = new ContentModel();
        contentModel.setMessageId(UUID.randomUUID().toString());
        contentModel.setUserId(userId);

        contentDataHelper.contentDataSend(jsonObject.toString(), contentModel);

        addDelay(1000);

        String contentId2 = UUID.randomUUID().toString();

        JSONObject jsonObject1 = new JSONObject();
        try {

            jsonObject1.put("success", false);
            jsonObject1.put("msg", contentId2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentDataHelper.contentDataSend(jsonObject1.toString(), contentModel);

        addDelay(1000);

        contentDataHelper.contentDataSend("", contentModel);

        addDelay(1000);


        String contentId = UUID.randomUUID().toString();

        ContentMetaInfo contentMetaInfo = new ContentMetaInfo();
        contentMetaInfo.setMessageId(contentId);
        contentMetaInfo.setMetaInfo("test meta");
        contentMetaInfo.setMessageType(1);
        contentMetaInfo.setThumbData(randomEntityGenerator.sampleByteImage().getBytes());

        String value = new Gson().toJson(contentMetaInfo);

        contentDataHelper.contentReceiveStart(contentId, randomEntityGenerator.getDummyImageLink(), userId, value.getBytes());

        addDelay(1000);

        contentDataHelper.contentReceiveStart(contentId, randomEntityGenerator.getDummyImageLink(), userId, value.getBytes());

        addDelay(1000);


        String pendingContentId = UUID.randomUUID().toString();

        // Insert a content information in table

        ChatEntity chatEntity = randomEntityGenerator.createOutgoingContent(userId);
        ((MessageEntity) chatEntity).setContentId(pendingContentId);

        messageSourceData.insertOrUpdateData(chatEntity);
        addDelay(1000);


        ContentPendingModel contentPendingModel = new ContentPendingModel();
        contentPendingModel.setSenderId(userId);
        contentPendingModel.setContentId(pendingContentId);
        contentPendingModel.setContentPath(randomEntityGenerator.getDummyImageLink());
        contentPendingModel.setProgress(101);
        contentPendingModel.setState(Constants.ServiceContentState.PROGRESS);
        contentPendingModel.setContentMetaInfo(contentMetaInfo);

        contentDataHelper.pendingContents(contentPendingModel);
        addDelay(1000);

        contentPendingModel.setContentMetaInfo(null);
        contentDataHelper.pendingContents(contentPendingModel);
        addDelay(1000);

        contentPendingModel.setContentMetaInfo(null);
        contentPendingModel.setContentId(contentId);
        contentDataHelper.pendingContents(contentPendingModel);
        addDelay(1000);


        contentPendingModel.setContentId(pendingContentId);

        contentPendingModel.setContentPath("");
        contentDataHelper.pendingContents(contentPendingModel);
        addDelay(1000);

        contentPendingModel.setIncoming(true);
        contentPendingModel.setContentPath(randomEntityGenerator.getDummyImageLink());
        contentPendingModel.setContentMetaInfo(contentMetaInfo);
        contentDataHelper.pendingContents(contentPendingModel);
        addDelay(1000);


        contentDataHelper.contentReceiveInProgress(contentId, 50);
        addDelay(1000);

        contentDataHelper.contentReceiveInProgress(contentId1, 101);
        addDelay(1000);

        contentDataHelper.contentReceiveDone(contentId, true, "success");
        addDelay(1000);

        // test content progress set
        contentDataHelper.setContentProgress(contentId, 105, contentId);
        addDelay(1000);

        contentDataHelper.setContentProgressByContentIdForSender(contentId, 100);
        addDelay(1000);

        // Test new received content info where message not exists
        contentModel.setMessageId(UUID.randomUUID().toString());
        contentModel.setContentPath(randomEntityGenerator.getDummyImageLink());
        contentModel.setAckStatus(Constants.ServiceContentState.FAILED);
        contentDataHelper.setContentProgressByContentIdForSender(contentId, 100);
        addDelay(1000);


        assertTrue(true);
        StatusHelper.out("Content test executed");
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private UserEntity addSampleUser() {
        UserEntity userEntity = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_MESH_ONLINE)
                .setMeshId(userId)
                .setUserName("Daniel")
                .setIsFavourite(Constants.FavouriteStatus.UNFAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());
        //userEntity.setId(0);

        userDataSource.insertOrUpdateData(userEntity);

        return userEntity;
    }
}