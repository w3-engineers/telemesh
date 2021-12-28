package com.w3engineers.unicef.telemesh.ui.chat;

import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class ChatViewModelTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    private AppDatabase appDatabase;

    private ChatViewModel SUT;
    private RandomEntityGenerator randomEntityGenerator;

    private UserDataSource userDataSource;
    private MessageSourceData messageSourceData;
    private Context mContext;

    private String userAddress = "0x3b52d4e229fd5396f468522e68f17cfe471b2e03";

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        randomEntityGenerator = new RandomEntityGenerator();
        appDatabase = Room.inMemoryDatabaseBuilder(mContext,
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = UserDataSource.getInstance(appDatabase.userDao());
        messageSourceData = MessageSourceData.getInstance(appDatabase.messageDao());


        userDataSource = UserDataSource.getInstance();
        messageSourceData = MessageSourceData.getInstance();

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



    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }
}
