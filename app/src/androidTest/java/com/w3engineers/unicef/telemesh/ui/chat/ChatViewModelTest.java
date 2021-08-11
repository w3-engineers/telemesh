package com.w3engineers.unicef.telemesh.ui.chat;

import androidx.paging.PagedList;
import androidx.room.Room;
import androidx.test.rule.ActivityTestRule;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

/*@RunWith(AndroidJUnit4.class)
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

        if (SharedPref.getSharedPref(mContext).read(Constants.preferenceKey.MY_USER_ID).isEmpty()) {
            SharedPref.getSharedPref(mContext).write(Constants.preferenceKey.MY_USER_ID, userAddress);
        }
        SUT = new ChatViewModel(rule.getActivity().getApplication());
    }

    @Test
    public void testMessageSetAndGet_getMessageObject_setMessage() {
        addDelay(5000);
        try {
            UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
            userDataSource.insertOrUpdateData(userEntity);

            String message = "Hi";

            if (userEntity.getMeshId() != null) {
                SUT.sendMessage(userEntity.getMeshId(), message, true);
            }

            addDelay(700);

            TestObserver<List<ChatEntity>> listTestObserver = LiveDataTestUtil.testObserve(SUT.getAllMessage(userEntity.getMeshId()));

            addDelay(700);

            SUT.prepareDateSpecificChat(listTestObserver.observedvalues.get(0));

            TestObserver<PagedList<ChatEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getChatEntityWithDate());

            addDelay(700);

            assertThat(testObserver.observedvalues.get(0).size(), greaterThan(listTestObserver.observedvalues.get(0).size()));

            addDelay(700);

            assertThat(((MessageEntity) listTestObserver.observedvalues.get(0).get(0)).getMessage(), is(message));

            ChatEntity receiverChat = randomEntityGenerator.createReceiverChatEntity(userEntity.getMeshId());
            messageSourceData.insertOrUpdateData(receiverChat);

            addDelay(700);

            SUT.updateAllMessageStatus(userEntity.getMeshId());

            addDelay(700);

            ChatEntity retrieveReceiverChat = messageSourceData.getMessageEntityById(receiverChat.getMessageId());
            assertThat(retrieveReceiverChat.getStatus(), is(Constants.MessageStatus.STATUS_READ));

            userEntity.setOnlineStatus(Constants.UserStatus.OFFLINE);
            userDataSource.insertOrUpdateData(userEntity);

            //TestObserver<UserEntity> entityTestObserver = LiveDataTestUtil.testObserve(SUT.getUserById(userEntity.getMeshId()));

            addDelay(2000);

            assertFalse(userEntity.getOnlineStatus() > Constants.UserStatus.OFFLINE);

            addDelay(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
}*/
