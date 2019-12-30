package com.w3engineers.unicef.telemesh._UiTest;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.gson.Gson;
import com.w3engineers.appshare.application.ui.InAppShareActivity;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.DataModel;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class TeleMeshTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    public UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    public ActivityTestRule<InAppShareActivity> mActivityAppShareRule = new ActivityTestRule<>(InAppShareActivity.class);


    @Rule
    public ActivityTestRule<ChatActivity> mChatTestRule = new ActivityTestRule<>(ChatActivity.class, true, false);

    private UserDataSource userDataSource;
    private FeedDataSource feedDataSource;
    private MessageSourceData messageSourceData;
    private RandomEntityGenerator randomEntityGenerator;
    private SharedPref sharedPref;
    private AppDatabase appDatabase;
    private Context context;

    @Before
    public void setUp() {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        // userDataSource = UserDataSource.getInstance(appDatabase.userDao());
        userDataSource = UserDataSource.getInstance();

        feedDataSource = FeedDataSource.getInstance();

        //messageSourceData = MessageSourceData.getInstance(appDatabase.messageDao());
        messageSourceData = MessageSourceData.getInstance();

        randomEntityGenerator = new RandomEntityGenerator();

        context = InstrumentationRegistry.getTargetContext();
        sharedPref = SharedPref.getSharedPref(context);
    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
    }

    // Registration process
    @Test
    public void uiTest_1() {
        addDelay(3200);

        // first create new user.
        onView(allOf(withId(R.id.button_create_account))).perform(click());

        addDelay(500);

        ViewInteraction baseEditText = onView(
                allOf(withId(R.id.edit_text_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.name_layout),
                                        0),
                                0)));
        addDelay(500);
        baseEditText.perform(scrollTo(), replaceText("Mimo"), closeSoftKeyboard());

        addDelay(500);

        ViewInteraction baseEditText2 = onView(
                allOf(withId(R.id.edit_text_name), withText("Mimo"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.name_layout),
                                        0),
                                0)));
        baseEditText2.perform(pressImeActionButton());

        addDelay(1000);

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.image_view_camera),
                        childAtPosition(
                                allOf(withId(R.id.image_layout),
                                        childAtPosition(
                                                withId(R.id.scrollview),
                                                0)),
                                7)));
        appCompatImageView.perform(scrollTo(), click());

        addDelay(700);

        ViewInteraction constraintLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.recycler_view),
                                childAtPosition(
                                        withId(R.id.profile_image_layout),
                                        1)),
                        3),
                        isDisplayed()));
        constraintLayout.perform(click());

        addDelay(700);

        ViewInteraction profileImageLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.recycler_view),
                                childAtPosition(
                                        withId(R.id.profile_image_layout),
                                        1)),
                        0),
                        isDisplayed()));
        profileImageLayout.perform(click());

        addDelay(700);

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_done), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        addDelay(700);

        ViewInteraction appCompatImageView1 = onView(
                allOf(withId(R.id.image_view_camera),
                        childAtPosition(
                                allOf(withId(R.id.image_layout),
                                        childAtPosition(
                                                withId(R.id.scrollview),
                                                0)),
                                7)));
        appCompatImageView1.perform(scrollTo(), click());

        addDelay(700);

        ViewInteraction reSelectImage = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.recycler_view),
                                childAtPosition(
                                        withId(R.id.profile_image_layout),
                                        1)),
                        6),
                        isDisplayed()));
        reSelectImage.perform(click());

        addDelay(700);

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.menu_done), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        addDelay(700);

        ViewInteraction baseButton = onView(
                allOf(withId(R.id.button_signup), withText("Sign Up"),
                        childAtPosition(
                                allOf(withId(R.id.image_layout),
                                        childAtPosition(
                                                withId(R.id.scrollview),
                                                0)),
                                8)));
        baseButton.perform(scrollTo(), click());

        addDelay(700);
    }

    // Fragment tab switch process
    @Test
    public void uiTest_2() {
        addDelay(3800);

        ViewInteraction bottomNavigationMessageFeed = onView(
                allOf(withId(R.id.action_message_feed),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationMessageFeed.perform(click());

        addDelay(700);

        ViewInteraction bottomNavigationSettings = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationSettings.perform(click());

        addDelay(700);

        ViewInteraction meshLogOpen = onView(allOf(withId(R.id.layout_show_log),
                childAtPosition(allOf(withId(R.id.layout_settings), childAtPosition(withId(R.id.layout_scroll), 0)), 6)));

        meshLogOpen.perform(scrollTo(), click());
        pressBack();
        addDelay(700);

       /* ViewInteraction connectivityDiagram = onView(allOf(withId(R.id.layout_diagram_map),
                childAtPosition(allOf(withId(R.id.layout_settings), childAtPosition(withId(R.id.layout_scroll), 0)), 5)));

        connectivityDiagram.perform(scrollTo(), click());
        pressBack();
        addDelay(700);*/

        mDevice.pressBack();

        addDelay(500);

        mDevice.pressBack();
    }

    // Settings properties test
    @Test
    public void uiTest_3() {
        addDelay(3800);

        UserEntity userEntity = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_ONLINE)
                .setMeshId("0xaa2dd785fc60eeb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Daniel");
        userEntity.setId(0);

        userDataSource.insertOrUpdateData(userEntity);

        addDelay(1000);

        ViewInteraction contactLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.contact_recycler_view),
                                childAtPosition(withId(R.id.mesh_contact_layout),
                                        0)),
                        0),
                        isDisplayed()));
        contactLayout.perform(click());

        addDelay(1000);

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_text_message),
                        childAtPosition(
                                allOf(withId(R.id.chat_message_bar),
                                        childAtPosition(
                                                withId(R.id.chat_layout),
                                                4)),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Hi"), closeSoftKeyboard());

        addDelay(700);

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.image_view_send),
                        childAtPosition(
                                allOf(withId(R.id.chat_message_bar),
                                        childAtPosition(
                                                withId(R.id.chat_layout),
                                                4)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        addDelay(1000);

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        messageSourceData.insertOrUpdateData(chatEntity);

        addDelay(1000);

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.text_view_last_name),
                        childAtPosition(
                                allOf(withId(R.id.chat_toolbar_layout),
                                        childAtPosition(
                                                withId(R.id.toolbar_chat),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatTextView.perform(click());

        addDelay(1000);

        pressBack();

        addDelay(700);

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        addDelay(700);

        ViewInteraction contactSearch = onView(
                allOf(withId(R.id.action_search),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        1),
                                0),
                        isDisplayed()));
        contactSearch.perform(click());

        addDelay(500);

        ViewInteraction searchAutoComplete = onView(
                allOf(withId(R.id.search_src_text),
                        childAtPosition(
                                allOf(withId(R.id.search_plate),
                                        childAtPosition(
                                                withId(R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("dane"), closeSoftKeyboard());

        addDelay(500);

        // message and contact test end

        ViewInteraction bottomNavigationSettings = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationSettings.perform(click());

        addDelay(700);

        ViewInteraction viewProfile = onView(allOf(withId(R.id.layout_view_profile),
                childAtPosition(allOf(withId(R.id.layout_settings), childAtPosition(withId(R.id.layout_scroll), 0)), 0)));
        viewProfile.perform(scrollTo(), click());

        addDelay(700);

        ViewInteraction baseButton = onView(
                allOf(withId(R.id.op_back), childAtPosition(
                        withId(R.id.view_profile_layout),
                        1)));
        baseButton.perform(click());

        addDelay(700);


        ViewInteraction openWallet = onView(allOf(withId(R.id.layout_open_wallet),
                childAtPosition(allOf(withId(R.id.layout_settings), childAtPosition(withId(R.id.layout_scroll), 0)), 1)));
        openWallet.perform(scrollTo(), click());

        addDelay(700);

        if (Constants.IsMeshInit) {
            pressBack();
            addDelay(700);
        }

        ViewInteraction constraintLayout2 = onView(allOf(withId(R.id.layout_data_plan),
                childAtPosition(allOf(withId(R.id.layout_settings), childAtPosition(withId(R.id.layout_scroll), 0)), 2)));


        constraintLayout2.perform(scrollTo(), click());

        addDelay(700);

        if (Constants.IsMeshInit) {
            pressBack();
            addDelay(700);
        }

        ViewInteraction chooseLanguage = onView(
                allOf(withId(R.id.layout_choose_language),
                        childAtPosition(
                                allOf(withId(R.id.layout_settings),
                                        childAtPosition(
                                                withId(R.id.layout_scroll),
                                                0)),
                                4)));
        chooseLanguage.perform(scrollTo(), click());


        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.radio_bangla), withText("বাংলা"),
                        childAtPosition(
                                allOf(withId(R.id.radio_group_language),
                                        childAtPosition(
                                                withId(R.id.alert_buy_sell_dialog_layout),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        addDelay(2000);

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        addDelay(700);

        ViewInteraction constraintLayoutCooseLanguage = onView(
                allOf(withId(R.id.layout_choose_language),
                        childAtPosition(
                                allOf(withId(R.id.layout_settings),
                                        childAtPosition(
                                                withId(R.id.layout_scroll),
                                                0)),
                                4)));
        constraintLayoutCooseLanguage.perform(scrollTo(), click());

        addDelay(700);

        ViewInteraction appCompatRadioButton2 = onView(
                allOf(withId(R.id.radio_english), withText("English"),
                        childAtPosition(
                                allOf(withId(R.id.radio_group_language),
                                        childAtPosition(
                                                withId(R.id.alert_buy_sell_dialog_layout),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatRadioButton2.perform(click());

        addDelay(2000);

        ViewInteraction bottomNavigationItemViewSettings = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemViewSettings.perform(click());

        addDelay(700);

        ViewInteraction constraintLayout3 = onView(
                allOf(withId(R.id.layout_about_us),
                        childAtPosition(
                                allOf(withId(R.id.layout_settings),
                                        childAtPosition(
                                                withId(R.id.layout_scroll),
                                                0)),
                                7)));
        constraintLayout3.perform(scrollTo(), click());

        addDelay(700);

        pressBack();

        addDelay(700);

        ViewInteraction againLayoutShareApp = onView(allOf(withId(R.id.layout_share_app),
                childAtPosition(allOf(withId(R.id.layout_settings), childAtPosition(withId(R.id.layout_scroll), 0)), 3)));
        againLayoutShareApp.perform(scrollTo(), click());

        addDelay(5000);

        mActivityAppShareRule.finishActivity();

        addDelay(700);


        // ----------------- unit test 5 part ----------------------

        addDelay(3800);

        // Prepare bulletin
        BulletinModel bulletin = new BulletinModel()
                .setId("testId1")
                .setMessage("Test feed details")
                .setTime("2019-06-014T06:05:50.000Z");

        String bulletinString = new Gson().toJson(bulletin);

        DataModel rmDataModel = new DataModel()
                .setUserId("0xuodnaiabd1983nd")
                .setRawData(bulletinString.getBytes())
                .setDataType(Constants.DataType.MESSAGE_FEED);

        RmDataHelper.getInstance().dataReceive(rmDataModel, true);

        addDelay(700);

        ViewInteraction bottomNavigationMessageFeed = onView(
                allOf(withId(R.id.action_message_feed),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationMessageFeed.perform(click());

        addDelay(700);

        ViewInteraction contactLayout1 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.message_recycler_view),
                                childAtPosition(withId(R.id.message_feed_layout),
                                        0)),
                        0),
                        isDisplayed()));
        contactLayout1.perform(click());

        addDelay(700);

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        // ---------------------  user company test -----------------------

        addDelay(3800);

        ViewInteraction bottomNavigationSettings1 = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationSettings1.perform(click());

        addDelay(700);

        // just set dummy company
        sharedPref.write(Constants.preferenceKey.COMPANY_ID, "ahukoip1890");
        sharedPref.write(Constants.preferenceKey.COMPANY_NAME, "Refugi Camp 1");

        ViewInteraction viewProfile1 = onView(allOf(withId(R.id.layout_view_profile),
                childAtPosition(allOf(withId(R.id.layout_settings), childAtPosition(withId(R.id.layout_scroll), 0)), 0)));
        viewProfile1.perform(scrollTo(), click());

        addDelay(700);

        // test to off mesh service. Because we cannot hit notification button
        //  RmDataHelper.getInstance().stopMeshService();

        // addDelay(4000);

        mDevice.pressBack();

        // clear dummy company
        sharedPref.write(Constants.preferenceKey.COMPANY_ID, "");
        sharedPref.write(Constants.preferenceKey.COMPANY_NAME, "");

        addDelay(2500);

        mDevice.pressBack();

        addDelay(1000);

        try {
            mDevice.pressBack();
        } catch (NoActivityResumedException e) {
            e.printStackTrace();
        }

        // for chat and user search test

        mDevice.pressBack();

        addDelay(500);

        mDevice.pressBack();

        addDelay(2000);

        try {
            RmDataHelper.getInstance().stopRmService();
            mDevice.pressBack();
        } catch (NoActivityResumedException e) {
            e.printStackTrace();
        }
    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

}
