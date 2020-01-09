package com.w3engineers.unicef.telemesh._UiTest;


import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.uiautomator.UiDevice;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.editprofile.EditProfileActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.security.SecurityActivity;
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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.parse.Parse.getApplicationContext;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class TelemeshTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);


    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

    public String myAddress = "0x550de922bec427fc1b279944e47451a89a4f7cag";
    public String friendAddress = "0x3b52d4e229fd5396f468522e68f17cfe471b2e03";
    public String publicKey = "0x04647ba47589ace7e9636029e5355b9b71c1c66ccd3c1b7c127f3c21016dacea7d3aa12e41eca790d4c3eff8398fd523dc793c815da7bbdbf29c8744b761ad8e4c";
    public String defaultPassword = "mesh_123";

    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    private FeedDataSource feedDataSource;
    private MessageSourceData messageSourceData;
    private RandomEntityGenerator randomEntityGenerator;
    private SharedPref sharedPref;
    private Context context;

    @Before
    public void setUp() {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = UserDataSource.getInstance();
        feedDataSource = FeedDataSource.getInstance();
        messageSourceData = MessageSourceData.getInstance();
        randomEntityGenerator = new RandomEntityGenerator();

        context = InstrumentationRegistry.getTargetContext();
        sharedPref = SharedPref.getSharedPref(context);
    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
    }

    @Test
    public void uiTest_01() {

        addDelay(3200);

        ViewInteraction buttonImportAccount = onView(
                allOf(withId(R.id.button_import_account),
                        childAtPosition(
                                childAtPosition(withId(android.R.id.content), 0), 3),
                        isDisplayed()));
        buttonImportAccount.perform(click());

        addDelay(500);

        ViewInteraction importAnotherId = onView(
                allOf(withId(R.id.button_import_profile),
                        childAtPosition(allOf(withId(R.id.activity_import_profile_scroll_parent),
                                childAtPosition(withId(R.id.activity_import_profile_scroll), 0)), 5)));
        importAnotherId.perform(scrollTo(), click());

        addDelay(3000);

        mDevice.pressBack();

        addDelay(1000);

        ViewInteraction activityImportProfileBack = onView(
                allOf(withId(R.id.image_view_back),
                        childAtPosition(childAtPosition(withId(android.R.id.content), 0), 0), isDisplayed()));
        activityImportProfileBack.perform(click());

        addDelay(500);

        ViewInteraction buttonCreateAccount = onView(
                allOf(withId(R.id.button_create_account),
                        childAtPosition(allOf(withId(R.id.activity_profile_choice_parent),
                                childAtPosition(withId(android.R.id.content), 0)), 2),
                        isDisplayed()));

        /*ViewInteraction buttonCreateAccount = onView(
                allOf(withId(R.id.button_create_account),
                        childAtPosition(
                                childAtPosition(withId(R.id.activity_profile_choice_parent), 0), 2), isDisplayed()));*/
        buttonCreateAccount.perform(click());

        addDelay(500);

        ViewInteraction baseEditText = onView(
                allOf(withId(R.id.edit_text_name),
                        childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));
        addDelay(500);
        baseEditText.perform(scrollTo(), replaceText("Mimo"), closeSoftKeyboard());

        addDelay(500);

        ViewInteraction baseEditText2 = onView(
                allOf(withId(R.id.edit_text_name), withText("Mimo"),
                        childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));
        baseEditText2.perform(pressImeActionButton());

        addDelay(1000);

        ViewInteraction buttonImageChooserFirst = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout), childAtPosition(withId(R.id.scrollview), 0)), 6)));
        buttonImageChooserFirst.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction selectDoneMenuForNoSelect = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForNoSelect.perform(click());

        addDelay(500);

        ViewInteraction selectImageForTheFirst = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                        childAtPosition(withId(R.id.profile_image_layout), 1)), 0), isDisplayed()));
        selectImageForTheFirst.perform(click());

        addDelay(500);

        ViewInteraction selectDoneMenuForTheFirst = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheFirst.perform(click());

        addDelay(500);

        ViewInteraction buttonImageChooserSecond = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout), childAtPosition(withId(R.id.scrollview), 0)), 6)));
        buttonImageChooserSecond.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction selectImageForTheSecond = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.recycler_view),
                                childAtPosition(withId(R.id.profile_image_layout), 1)), 4), isDisplayed()));
        selectImageForTheSecond.perform(click());

        addDelay(300);

        ViewInteraction selectImageForTheThird = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                        childAtPosition(withId(R.id.profile_image_layout), 1)), 1), isDisplayed()));
        selectImageForTheThird.perform(click());

        addDelay(300);

        ViewInteraction selectDoneMenuForTheSecond = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheSecond.perform(click());

        addDelay(500);

        ViewInteraction buttonImageChooserThird = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout),
                                childAtPosition(withId(R.id.scrollview), 0)), 6)));
        buttonImageChooserThird.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction selectImageForTheFourth = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                        childAtPosition(withId(R.id.profile_image_layout), 1)), 4), isDisplayed()));
        selectImageForTheFourth.perform(click());

        addDelay(300);

        ViewInteraction selectDoneMenuForTheThird = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheThird.perform(click());

        addDelay(500);

        ViewInteraction ActionCreateProfileNext = onView(
                allOf(withId(R.id.button_signup),
                        childAtPosition(allOf(withId(R.id.image_layout),
                                childAtPosition(withId(R.id.scrollview), 0)), 10)));
        ActionCreateProfileNext.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction boxPassword = onView(
                allOf(withId(R.id.edit_text_box_password),
                        childAtPosition(allOf(withId(R.id.activity_security_scroll_parent),
                                childAtPosition(withId(R.id.activity_security_scroll), 0)), 1)));
        boxPassword.perform(scrollTo(), replaceText("mesh_123"), closeSoftKeyboard());

        addDelay(1000);

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.text_view_show_password),
                        childAtPosition(allOf(withId(R.id.activity_security_scroll_parent),
                                childAtPosition(withId(R.id.activity_security_scroll), 0)), 2)));
        appCompatTextView.perform(scrollTo(), click());

        addDelay(300);

        ViewInteraction showPassword = onView(
                allOf(withId(R.id.text_view_show_password),
                        childAtPosition(
                                allOf(withId(R.id.activity_security_scroll_parent),
                                        childAtPosition(
                                                withId(R.id.activity_security_scroll),
                                                0)),
                                2)));
        showPassword.perform(scrollTo(), click());

        addDelay(300);

        ViewInteraction securityButtonNext = onView(
                allOf(withId(R.id.button_next),
                        childAtPosition(allOf(withId(R.id.activity_security_scroll_parent),
                                childAtPosition(withId(R.id.activity_security_scroll), 0)), 4)));
        securityButtonNext.perform(scrollTo(), click());

        addDelay(5000);

        Activity currentActivity = getActivityInstance();

        if (currentActivity instanceof SecurityActivity) {

            SecurityActivity securityActivity = (SecurityActivity) getActivityInstance();

            securityActivity.processCompleted(myAddress, publicKey, defaultPassword);

            addDelay(300);
        }

        ViewInteraction settingsTab = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 3), isDisplayed()));
        settingsTab.perform(click());

        addDelay(1000);

        ViewInteraction profileRow = onView(
                allOf(withId(R.id.layout_view_profile),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 0)));
        profileRow.perform(scrollTo(), click());

        addDelay(1000);

        ViewInteraction editButton = onView(
                allOf(withId(R.id.text_view_edit),
                        childAtPosition(allOf(withId(R.id.view_profile_layout),
                                childAtPosition(withId(android.R.id.content), 0)), 3), isDisplayed()));
        editButton.perform(click());

        addDelay(4000);

        try {

            ViewInteraction editInputTextBox = onView(allOf(allOf(withId(R.id.edit_text_name),
                    childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)),
                    childAtPosition(allOf(withId(R.id.image_layout),
                            childAtPosition(withId(R.id.scrollview), 0)),
                            8)));

            /*ViewInteraction editInputTextBox = onView(
                    allOf(withId(R.id.edit_text_name),
                            childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));*/
            editInputTextBox.perform(scrollTo(), replaceText("Mimo Saha"), closeSoftKeyboard());
        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }

        addDelay(500);

        ViewInteraction updateProfileImageSelection = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout),
                                childAtPosition(withId(R.id.scrollview), 0)), 6)));
        updateProfileImageSelection.perform(scrollTo(), click());

        addDelay(1000);

        ViewInteraction profileImageSelect = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                        childAtPosition(withId(R.id.profile_image_layout), 1)), 3), isDisplayed()));
        profileImageSelect.perform(click());

        addDelay(1000);

        ViewInteraction profileUpdateDone = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(
                                childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        profileUpdateDone.perform(click());

        addDelay(2500);

        currentActivity = getActivityInstance();

        if (currentActivity instanceof EditProfileActivity) {
            EditProfileActivity editProfileActivity = (EditProfileActivity) currentActivity;
            new Handler(Looper.getMainLooper()).post(editProfileActivity::goNext);
//            editProfileActivity.goNext();
        }

        /*ViewInteraction updateProfile = onView(
                allOf(withId(R.id.button_update),
                        childAtPosition(allOf(withId(R.id.image_layout),
                                childAtPosition(withId(R.id.scrollview), 0)),
                                10)));
        updateProfile.perform(scrollTo(), click());*/

        addDelay(2500);

        mDevice.pressBack();

        addDelay(500);
    }

    @Test
    public void uiTest_02() {
        addDelay(3800);

        SharedPref sharedPref = SharedPref.getSharedPref(context);
        sharedPref.write(Constants.preferenceKey.USER_NAME, "Mimo");
        sharedPref.write(Constants.preferenceKey.IMAGE_INDEX, 1);
        sharedPref.write(Constants.preferenceKey.MY_USER_ID, myAddress);

        ViewInteraction favoriteTab = onView(
                allOf(withId(R.id.action_contact),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 1), isDisplayed()));
        favoriteTab.perform(click());

        addDelay(500);

        ViewInteraction broadcastMessageTab = onView(
                allOf(withId(R.id.action_message_feed),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 2), isDisplayed()));
        broadcastMessageTab.perform(click());

        addDelay(500);

        addFeedItem();

        addDelay(1500);

        // click feed item.

        onView(withId(R.id.message_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        addDelay(1500);

        mDevice.pressBack();

        addDelay(500);


        ViewInteraction settingsTab = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 3), isDisplayed()));
        settingsTab.perform(click());

        addDelay(1000);

        ViewInteraction profileRow = onView(
                allOf(withId(R.id.layout_view_profile),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 0)));
        profileRow.perform(scrollTo(), click());

        addDelay(1000);

        ViewInteraction copyUserId = onView(
                allOf(withId(R.id.image_view_id_copy),
                        childAtPosition(allOf(withId(R.id.view_profile_layout),
                                childAtPosition(withId(android.R.id.content), 0)), 10), isDisplayed()));
        copyUserId.perform(click());

        addDelay(1000);

        mDevice.pressBack();

        addDelay(1000);

        ViewInteraction openWallet = onView(
                allOf(withId(R.id.layout_open_wallet),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 1)));
        openWallet.perform(scrollTo(), click());

        addDelay(2000);

        mDevice.pressBack();

        addDelay(500);

        ViewInteraction openDataPlan = onView(
                allOf(withId(R.id.layout_data_plan),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 2)));
        openDataPlan.perform(scrollTo(), click());

        addDelay(2000);

        mDevice.pressBack();

        addDelay(500);

        ViewInteraction openShareApp = onView(
                allOf(withId(R.id.layout_share_app),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 3)));
        openShareApp.perform(scrollTo(), click());

        addDelay(1000);

        mDevice.pressBack();

        addDelay(500);

        ViewInteraction chooseLanguage = onView(
                allOf(withId(R.id.layout_choose_language),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 4)));
        chooseLanguage.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction optionBangla = onView(
                allOf(withId(R.id.radio_bangla),
                        childAtPosition(allOf(withId(R.id.radio_group_language),
                                childAtPosition(withId(R.id.alert_buy_sell_dialog_layout), 1)), 1), isDisplayed()));
        optionBangla.perform(click());

        addDelay(4000);

        ViewInteraction chooseLanguageForSecond = onView(
                allOf(withId(R.id.layout_choose_language),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 4)));
        chooseLanguageForSecond.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction optionEnglish = onView(
                allOf(withId(R.id.radio_english),
                        childAtPosition(allOf(withId(R.id.radio_group_language),
                                childAtPosition(withId(R.id.alert_buy_sell_dialog_layout), 1)), 0), isDisplayed()));
        optionEnglish.perform(click());

        addDelay(4000);

        ViewInteraction optionAboutUs = onView(
                allOf(withId(R.id.layout_about_us),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 7)));
        optionAboutUs.perform(scrollTo(), click());

        addDelay(500);

        mDevice.pressBack();

        addDelay(500);

        ViewInteraction optionFeedBack = onView(
                allOf(withId(R.id.layout_feedback),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 8)));
        optionFeedBack.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction editBoxFeedback = onView(
                allOf(withId(R.id.edit_text_feedback),
                        childAtPosition(childAtPosition(withId(android.R.id.content), 0), 1), isDisplayed()));
        editBoxFeedback.perform(replaceText("good"), closeSoftKeyboard());

        addDelay(300);

        ViewInteraction baseButton4 = onView(
                allOf(withId(R.id.button_feed_back),
                        childAtPosition(childAtPosition(withId(android.R.id.content), 0), 2), isDisplayed()));
        baseButton4.perform(click());

        addDelay(500);

        mDevice.pressBack();

        mDevice.pressBack();

        addDelay(500);

        mDevice.pressBack();

        addDelay(2000);

        try {
            //RmDataHelper.getInstance().stopRmService();
            mDevice.pressBack();
        } catch (NoActivityResumedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uiTest_03() {

        addDelay(4000);

        UserEntity userEntity = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_ONLINE)
                .setMeshId("0xaa2dd785fc60eeb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Daniel")
                .setIsFavourite(Constants.FavouriteStatus.FAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());
        userEntity.setId(0);

        userDataSource.insertOrUpdateData(userEntity);

        addDelay(1000);

        ViewInteraction userItemAction = onView(
                allOf(childAtPosition(allOf(withId(R.id.contact_recycler_view),
                        childAtPosition(withId(R.id.mesh_contact_layout), 0)), 0), isDisplayed()));
        userItemAction.perform(click());

        addDelay(1000);

        ViewInteraction messageEditBox = onView(
                allOf(withId(R.id.edit_text_message),
                        childAtPosition(allOf(withId(R.id.chat_message_bar),
                                childAtPosition(withId(R.id.chat_layout), 5)), 0), isDisplayed()));
        messageEditBox.perform(replaceText("Hi"), closeSoftKeyboard());

        addDelay(700);

        ViewInteraction messageSendAction = onView(
                allOf(withId(R.id.image_view_send),
                        childAtPosition(allOf(withId(R.id.chat_message_bar),
                                childAtPosition(withId(R.id.chat_layout), 5)), 1), isDisplayed()));
        messageSendAction.perform(click());

        addDelay(1000);

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        messageSourceData.insertOrUpdateData(chatEntity);

        addDelay(1000);

        ViewInteraction viewProfileAction = onView(
                allOf(withId(R.id.text_view_last_name),
                        childAtPosition(allOf(withId(R.id.chat_toolbar_layout),
                                childAtPosition(withId(R.id.toolbar_chat), 0)), 1), isDisplayed()));
        viewProfileAction.perform(click());

        addDelay(1000);

        mDevice.pressBack();

        addDelay(1000);

        mDevice.pressBack();

        addDelay(4000);

        try {

            ViewInteraction contactSearch = onView(
                    allOf(withId(R.id.action_search),
                            childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0), isDisplayed()));
            contactSearch.perform(click());

            addDelay(1000);

            onView(withId(R.id.edit_text_search)).perform(replaceText("dane"),closeSoftKeyboard());

        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }

        addDelay(500);

        mDevice.pressBack();

        ViewInteraction favoriteTab = onView(
                allOf(withId(R.id.action_contact),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 1), isDisplayed()));
        favoriteTab.perform(click());

        addDelay(1000);

        ViewInteraction discoverTab = onView(
                allOf(withId(R.id.action_discover),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 0), isDisplayed()));
        discoverTab.perform(click());

        addDelay(1000);

        Activity currentActivity = getActivityInstance();

        if (currentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) currentActivity;

            new Handler(Looper.getMainLooper()).post(()-> {
                mainActivity.createUserBadgeCount(100, Constants.MenuItemPosition.POSITION_FOR_DISCOVER);
                mainActivity.popupSnackbarForCompleteUpdate();
            });

            addDelay(3000);
        }

        mDevice.pressBack();
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

    private void addFeedItem() {
        FeedEntity entity = new FeedEntity();
        entity.setFeedId(UUID.randomUUID().toString());
        entity.setFeedTitle("Sample title");
        entity.setFeedDetail("Sample broadcast");
        entity.setFeedProviderName("Unicef");
        entity.setFeedTime("2019-08-02T06:05:30.000Z");
        entity.setFeedReadStatus(false);

        feedDataSource.insertOrUpdateData(entity);
    }

}
