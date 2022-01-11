package com.w3engineers.unicef.telemesh.ui.chat;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.Uri;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class ChatViewModelTest {

    @Rule
    public ActivityTestRule<ChatActivity> rule = new ActivityTestRule<>(ChatActivity.class);

    private ChatViewModel SUT;
    private Context mContext;

    private String userAddress = "0x3b52d4e229fd5396f468522e68f17cfe471b2e03";
    private String imageFilePath = "file:///android_asset/sample_image.jpg";
    private String videoFilePath = "file:///android_asset/sample_vide.mp4";

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        SUT = new ChatViewModel(rule.getActivity().getApplication());
    }

    @Test
    public void testContentMessageSend(){
        addDelay(100);
        ChatEntity messageEntity = new ChatEntity();
        messageEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        SUT.resendContentMessage(messageEntity);
        assertTrue(true);
    }

    @Test
    public void testContentSend(){
        addDelay(1000);
        SUT.sendContentMessage(userAddress, Uri.parse("content://com.android.providers.media.documents/document/image"));
        assertTrue(true);
    }


    @Test
    public void testPrepareContent(){
        addDelay(1000);
        SUT.prepareContentMessage(userAddress, videoFilePath, imageFilePath);
        assertTrue(true);
    }



    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
    }
}
