package com.w3engineers.unicef.telemesh._UiTest;


import android.app.Activity;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;

import com.w3engineers.mesh.application.data.AppDataObserver;
import com.w3engineers.mesh.application.data.model.WalletLoaded;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.MeshDataSource;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.chooseprofileimage.ProfileImageActivity;
import com.w3engineers.unicef.telemesh.ui.editprofile.EditProfileActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.util.helper.StatusHelper;

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
import java.util.Collection;
import java.util.UUID;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertTrue;

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
    //private SharedPref sharedPref;
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

        mActivityTestRule.getActivity().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        //sharedPref = SharedPref.getSharedPref(context);
    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
        mActivityTestRule.finishActivity();
    }

    @Test
    public void uiTest_01() {

        addDelay(3800);

        termsOfUsePageTest();

        addDelay(1000);


        //        ViewInteraction buttonImageChooserFirst = onView(
//                allOf(withId(R.id.image_profile),
//                        childAtPosition(allOf(withId(R.id.image_layout), childAtPosition(withId(R.id.scrollview), 0)), 6)));
//        buttonImageChooserFirst.perform(scrollTo(), click());
        onView(withId(R.id.image_profile)).perform(click());


        addDelay(1000);

        ViewInteraction selectDoneMenuForNoSelect = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForNoSelect.perform(click());

        addDelay(1000);

        ViewInteraction selectImageForTheFirst = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                        childAtPosition(withId(R.id.profile_image_layout), 1)), 0), isDisplayed()));
        selectImageForTheFirst.perform(click());

        addDelay(1000);

        ViewInteraction selectDoneMenuForTheFirst = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheFirst.perform(click());

        addDelay(1000);

        ViewInteraction buttonImageChooserSecond = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout), childAtPosition(withId(R.id.scrollview), 0)), 6)));
        buttonImageChooserSecond.perform(scrollTo(), click());

        addDelay(1000);

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

        addDelay(700);

        ViewInteraction selectDoneMenuForTheSecond = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheSecond.perform(click());

        addDelay(700);

        ViewInteraction buttonImageChooserThird = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout),
                                childAtPosition(withId(R.id.scrollview), 0)), 6)));
        buttonImageChooserThird.perform(scrollTo(), click());

        addDelay(700);

        ViewInteraction selectImageForTheFourth = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                        childAtPosition(withId(R.id.profile_image_layout), 1)), 4), isDisplayed()));
        selectImageForTheFourth.perform(click());

        addDelay(700);

        ViewInteraction selectDoneMenuForTheThird = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheThird.perform(click());

        addDelay(1000);


        ViewInteraction baseEditText = onView(
                allOf(withId(R.id.edit_text_name),
                        childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));
        addDelay(500);
        baseEditText.perform(scrollTo(), replaceText("M"), closeSoftKeyboard());

        addDelay(500);

        baseEditText.perform(pressImeActionButton());

        addDelay(1000);

        baseEditText.perform(scrollTo(), replaceText("Aladeen"), closeSoftKeyboard());

        addDelay(1000);

        /*ViewInteraction baseEditText2 = onView(
                allOf(withId(R.id.edit_text_name), withText("Mimo"),
                        childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));
        baseEditText2.perform(pressImeActionButton());*/

//        addDelay(2000);


        ViewInteraction ActionCreateProfileNext = onView(withId(R.id.button_signup));
//        ViewInteraction ActionCreateProfileNext = onView(
//                allOf(withId(R.id.button_signup),
//                        childAtPosition(allOf(withId(R.id.image_layout),
//                                childAtPosition(withId(R.id.scrollview), 0)), 10)));

        ActionCreateProfileNext.perform(scrollTo(), click());

        addDelay(2000);


        WalletLoaded walletLoaded = new WalletLoaded();
        walletLoaded.walletAddress = myAddress;
        walletLoaded.success = true;
        AppDataObserver.on().sendObserverData(walletLoaded);

        addDelay(1000);


        /*if (currentActivity instanceof SecurityActivity) {

            SecurityActivity securityActivity = (SecurityActivity) getActivityInstance();

            securityActivity.processCompleted(myAddress, publicKey, defaultPassword);

            addDelay(1000);
        }*/

        //uiTest_02();

        StatusHelper.out("Test uiTest_01 executed");

        assertTrue(true);

    }

    // Settings page test
    @Test
    public void uiTest_02() {


        addDelay(3800);


        if (currentActivity instanceof MainActivity) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) currentActivity).stopAnimation();
                }
            });
        }

        addDelay(1000);

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

        ViewInteraction openDataPlan = onView(
                allOf(withId(R.id.layout_data_plan),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 2)));
        openDataPlan.perform(scrollTo(), click());

        addDelay(2000);

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

        addDelay(1500);

        ViewInteraction baseButton4 = onView(
                allOf(withId(R.id.button_feed_back),
                        childAtPosition(childAtPosition(withId(android.R.id.content), 0), 2), isDisplayed()));
        baseButton4.perform(click());

        addDelay(1000);

        ViewInteraction editBoxFeedback = onView(
                allOf(withId(R.id.edit_text_feedback),
                        childAtPosition(childAtPosition(withId(android.R.id.content), 0), 1), isDisplayed()));
        editBoxFeedback.perform(replaceText("good"), closeSoftKeyboard());

        addDelay(300);

        baseButton4.perform(click());

        addDelay(1000);

        mDevice.pressBack();

        addDelay(1000);

        ViewInteraction discoverTab = onView(
                allOf(withId(R.id.action_discover),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 0), isDisplayed()));
        discoverTab.perform(click());

        currentActivity = getActivityInstance();

        if (currentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) currentActivity;

            new Handler(Looper.getMainLooper()).post(() -> {
                mainActivity.createUserBadgeCount(100, Constants.MenuItemPosition.POSITION_FOR_DISCOVER);
                //mainActivity.popupSnackbarForCompleteUpdate();
            });

            addDelay(3000);
        }

        UserEntity userEntity = addSampleUser();
        uiTest_03(userEntity);

        uiTest_04();

        MeshDataSource.getInstance().destroyDataSource();

        Espresso.pressBackUnconditionally();
        addDelay(500);
        Espresso.pressBackUnconditionally();

        assertTrue(true);

        StatusHelper.out("Test executed");

        addDelay(4000);

    }

    public void uiTest_03(UserEntity userEntity) {

        addDelay(3000);

        currentActivity = getActivityInstance();


        addDelay(1000);

        onView(withId(R.id.contact_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        addDelay(1000);

        ViewInteraction messageEditBox = onView(withId(R.id.edit_text_message));
        messageEditBox.perform(replaceText("Hi"), closeSoftKeyboard());

        addDelay(1000);

        userEntity.setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE);

        userDataSource.insertOrUpdateData(userEntity);

        addDelay(1000);

        ViewInteraction messageSendAction = onView(withId(R.id.image_view_send));
        messageSendAction.perform(click());

        addDelay(1000);

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        messageSourceData.insertOrUpdateData(chatEntity);

        addDelay(1000);


        try {

            ViewInteraction viewProfileAction = onView(
                    allOf(withId(R.id.text_view_last_name),
                            childAtPosition(allOf(withId(R.id.chat_toolbar_layout),
                                    childAtPosition(withId(R.id.toolbar_chat), 0)), 1), isDisplayed()));
            viewProfileAction.perform(click());

            addDelay(1000);

            mDevice.pressBack();

            addDelay(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDevice.pressBack();

        addDelay(2000);


        /*onView(withId(R.id.contact_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        addDelay(2000);

        Activity currentActivity = getActivityInstance();

        if (currentActivity instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) currentActivity;
            chatActivity.chatFinishAndStartApp();
        }

        addDelay(5000);

        try {

            ViewInteraction contactSearch = onView(
                    allOf(withId(R.id.action_search),
                            childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0), isDisplayed()));
            contactSearch.perform(click());

            addDelay(1000);

            onView(withId(R.id.edit_text_search)).perform(replaceText("dane"), closeSoftKeyboard());

            currentActivity = getActivityInstance();
            hideKeyboard(currentActivity);

        } catch (NoMatchingViewException | PerformException e) {
            e.printStackTrace();
        }

        addDelay(2500);

        mDevice.pressBack();

        addDelay(1000);
        try {


            ViewInteraction favoriteTab = onView(withId(R.id.action_contact));
            favoriteTab.perform(click());

            addDelay(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewInteraction discoverTab = onView(
                allOf(withId(R.id.action_discover),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 0), isDisplayed()));
        discoverTab.perform(click());

        addDelay(1000);*/

    }


    //@Test
    public void uiTest_04() {

        addDelay(3800);

        currentActivity = getActivityInstance();

        if (currentActivity instanceof MainActivity) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) currentActivity).stopAnimation();
                }
            });
        }

        addDelay(1000);

        Activity currentActivity;

        addDelay(5000);

        ViewInteraction settingsTab = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 3), isDisplayed()));
        settingsTab.perform(click());

        addDelay(3000);

        ViewInteraction profileRow = onView(
                allOf(withId(R.id.layout_view_profile),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 0)));
        profileRow.perform(scrollTo(), click());

        addDelay(3000);

        ViewInteraction editButton = onView(
                allOf(withId(R.id.text_view_edit),
                        childAtPosition(allOf(withId(R.id.view_profile_layout),
                                childAtPosition(withId(android.R.id.content), 0)), 3), isDisplayed()));
        editButton.perform(click());

        addDelay(4000);

        try {

            /*ViewInteraction editInputTextBox = onView(allOf(allOf(withId(R.id.edit_text_name),
                    childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)),
                    childAtPosition(allOf(withId(R.id.image_layout),
                            childAtPosition(withId(R.id.scrollview), 0)),
                            8)));
*/
            ViewInteraction editInputTextBox = onView(
                    allOf(withId(R.id.edit_text_name),
                            childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));
            editInputTextBox.perform(scrollTo(), replaceText("Mimo Saha"), closeSoftKeyboard());
        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }

        addDelay(500);

        ViewInteraction updateProfileImageSelection = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout),
                                childAtPosition(withId(R.id.scrollview), 0)), 6)));

        try {
            updateProfileImageSelection.perform(scrollTo(), click());
        } catch (Exception e) {
            e.printStackTrace();
            ViewInteraction updateProfileImageViaCameraSelection = onView(
                    allOf(withId(R.id.image_view_camera),
                            childAtPosition(allOf(withId(R.id.image_layout),
                                    childAtPosition(withId(R.id.scrollview), 0)), 7)));

            try {
                updateProfileImageViaCameraSelection.perform(scrollTo(), click());
            } catch (Exception ex) {
                ex.printStackTrace();

                currentActivity = getActivityInstance();

                if (currentActivity instanceof EditProfileActivity) {
                    ((EditProfileActivity) currentActivity).openProfileImageChooser();
                }
            }
        }


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
        try {
            profileUpdateDone.perform(click());
        } catch (Exception e) {
            e.printStackTrace();
            currentActivity = getActivityInstance();

            if (currentActivity instanceof ProfileImageActivity) {
                ((ProfileImageActivity) currentActivity).actionDone();
            }
        }

        addDelay(2500);

        UserEntity userEntityOne = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE)
                .setMeshId("0xaa2dd785fc60epb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Mike")
                .setIsFavourite(Constants.FavouriteStatus.FAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        userDataSource.insertOrUpdateData(userEntityOne);

        addDelay(10000);

        currentActivity = getActivityInstance();

        if (currentActivity instanceof ProfileImageActivity) {

            addDelay(3000);

            currentActivity = getActivityInstance();
            if (currentActivity instanceof ProfileImageActivity) {
                mDevice.pressBack();

                addDelay(2000);

            }
            updateButtonClick();
        } else {
            updateButtonClick();
        }

        StatusHelper.out("Test uiTest_02 executed");

        assertTrue(true);
    }

    public void updateButtonClick() {

        hideKeyboard(currentActivity = getActivityInstance());

        addDelay(2000);

        try {
            onView(withId(R.id.button_update)).perform(scrollTo(), click());
        } catch (NoMatchingViewException e) {
            e.printStackTrace();

            Activity currentActivity = getActivityInstance();

            if (currentActivity instanceof EditProfileActivity) {
                EditProfileActivity editProfileActivity = (EditProfileActivity) currentActivity;
                new Handler(Looper.getMainLooper()).post(editProfileActivity::goNext);
            }
        }

        addDelay(2500);

        mDevice.pressBack();

        addDelay(500);

        broadcastFeedTest();


        /*currentActivity = getActivityInstance();
        if (currentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) currentActivity;
            mainActivity.finish();

            addDelay(1000);
        }*/
    }

    public void broadcastFeedTest() {

        addDelay(1000);


        ViewInteraction broadcastMessageTab = onView(
                allOf(withId(R.id.action_message_feed),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 2), isDisplayed()));
        broadcastMessageTab.perform(click());

        addDelay(1500);

        addFeedItem();

        addDelay(1500);

        // click feed item.

        onView(withId(R.id.message_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        addDelay(1500);

        mDevice.pressBack();

        addDelay(1000);

        Activity activity = getActivityInstance();

        if (activity instanceof MainActivity) {

            MainActivity mainActivity = (MainActivity) activity;
            //mainActivity.feedRefresh();

            addDelay(1000);

           /* try {
                mDevice.pressBack();
                addDelay(700);
                mDevice.pressBack();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }
    }


    // Group messaging test

    public void setUpGroupUser() {
        userDataSource = UserDataSource.getInstance();

        UserEntity userEntityOne = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_ONLINE)
                .setMeshId("0xf57d787f3ca95e2fd9cc782c85f6bcd3d6d779d9")
                .setUserName("Aladeen G1")
                .setIsFavourite(Constants.FavouriteStatus.UNFAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        UserEntity userEntityTwo = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE)
                .setMeshId("0xc1a5185c807038a32a4c6ca020826fee85d88fde")
                .setUserName("Aladeen G2")
                .setIsFavourite(Constants.FavouriteStatus.UNFAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        userDataSource.insertOrUpdateData(userEntityOne);
        userDataSource.insertOrUpdateData(userEntityTwo);


    }


    @Test
    public void uiTest_05() {


        addDelay(4000);

        setUpGroupUser();

        addDelay(2000);

        ViewInteraction floatingActionButton = onView(allOf(withId(R.id.fab_chat), childAtPosition(allOf(withId(R.id.mesh_contact_layout), childAtPosition(withId(R.id.fragment_container), 0)), 2), isDisplayed()));
        floatingActionButton.perform(click());

        addDelay(3000);

        ViewInteraction recyclerView = onView(allOf(withId(R.id.recycler_view_user), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 5)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        addDelay(1000);

        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recycler_view_user), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 5)));
        recyclerView2.perform(actionOnItemAtPosition(1, click()));

        addDelay(1000);

        ViewInteraction floatingActionButton2 = onView(allOf(withId(R.id.button_go), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 4), isDisplayed()));
        floatingActionButton2.perform(click());

        addDelay(2000);

        ViewInteraction appCompatTextView = onView(allOf(withId(R.id.text_view_last_name), childAtPosition(allOf(withId(R.id.chat_toolbar_layout), childAtPosition(withId(R.id.toolbar_chat), 0)), 1), isDisplayed()));
        appCompatTextView.perform(click());

        addDelay(2000);

        ViewInteraction appCompatImageView = onView(allOf(withId(R.id.image_view_pen), withContentDescription("Name field icon"), childAtPosition(childAtPosition(withId(R.id.nested_scroll_view), 0), 7), isDisplayed()));
        appCompatImageView.perform(click());

        addDelay(2000);

        ViewInteraction groupNametext = onView(withId(R.id.edit_text_name));
        groupNametext.perform(replaceText("MyTestGroup"));

        addDelay(1000);
        groupNametext.perform(closeSoftKeyboard());

        addDelay(1000);

        ViewInteraction appCompatButton = onView(allOf(withId(R.id.button_done), withText("Done"), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 7), isDisplayed()));
        appCompatButton.perform(click());

        addDelay(4000);

        try {
            UiScrollable appViews = new UiScrollable(
                    new UiSelector().scrollable(true));

            appViews.scrollForward();
            addDelay(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageView2 = onView(allOf(withId(R.id.image_view_remove),
                withContentDescription("Remove group member"),
                childAtPosition(childAtPosition(withId(R.id.recycler_view_group_member), 2), 2)));
        appCompatImageView2.perform(click());

        addDelay(2000);

        ViewInteraction appCompatTextView3 = onView(allOf(withId(R.id.text_view_add_member), withText("Add member"), childAtPosition(childAtPosition(withId(R.id.nested_scroll_view), 0), 13)));
        appCompatTextView3.perform(click());

        addDelay(2000);

        ViewInteraction recyclerView4 = onView(allOf(withId(R.id.recycler_view_user), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 5)));
        recyclerView4.perform(actionOnItemAtPosition(0, click()));

        addDelay(2000);

        ViewInteraction floatingActionButton3 = onView(allOf(withId(R.id.button_go), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 3), isDisplayed()));
        floatingActionButton3.perform(click());

        addDelay(2000);

        mDevice.pressBack();

        addDelay(3000);

        ViewInteraction appCompatEditText = onView(allOf(withId(R.id.edit_text_message), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 1), isDisplayed()));
        appCompatEditText.perform(click());

        addDelay(2000);

        appCompatEditText.perform(replaceText("Hello friends"));

        addDelay(1000);
        appCompatEditText.perform(closeSoftKeyboard());

        addDelay(1000);

        ViewInteraction appCompatImageButton2 = onView(allOf(withId(R.id.image_view_send), withContentDescription("Send icon."), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 2), isDisplayed()));
        appCompatImageButton2.perform(click());

        addDelay(2000);

        ViewInteraction overflowMenuButton = onView(allOf(withContentDescription("More options"), childAtPosition(childAtPosition(withId(R.id.toolbar_chat), 2), 0), isDisplayed()));
        overflowMenuButton.perform(click());

        addDelay(2000);

        ViewInteraction appCompatTextView4 = onView(allOf(withId(R.id.title), withText("Clear Chat"), childAtPosition(childAtPosition(withId(R.id.content), 0), 0), isDisplayed()));
        appCompatTextView4.perform(click());

        addDelay(2000);

        ViewInteraction appCompatEditText9 = onView(allOf(withId(R.id.edit_text_message), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 1), isDisplayed()));
        appCompatEditText9.perform(click());

        ViewInteraction appCompatEditText10 = onView(allOf(withId(R.id.edit_text_message), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 1), isDisplayed()));
        appCompatEditText10.perform(replaceText("Ok friends"));

        addDelay(1000);
        appCompatEditText10.perform(closeSoftKeyboard());

        addDelay(1000);

        ViewInteraction appCompatImageButton3 = onView(allOf(withId(R.id.image_view_send), withContentDescription("Send icon."), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 2), isDisplayed()));
        appCompatImageButton3.perform(click());

        addDelay(2000);

        ViewInteraction overflowMenuButton2 = onView(allOf(withContentDescription("More options"), childAtPosition(childAtPosition(withId(R.id.toolbar_chat), 2), 0), isDisplayed()));
        overflowMenuButton2.perform(click());

        addDelay(2000);

        ViewInteraction appCompatTextView5 = onView(allOf(withId(R.id.title), withText("Leave Group"), childAtPosition(childAtPosition(withId(R.id.content), 0), 0), isDisplayed()));
        appCompatTextView5.perform(click());

        addDelay(2000);

        addDelay(3000);
        ViewInteraction discoverTab = onView(allOf(withId(R.id.action_discover), childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 0), isDisplayed()));
        discoverTab.perform(click());

        uiTest_07();
    }

    //    @Test
    public void uiTest_07() {

        addDelay(4000);

        ViewInteraction floatingActionButton = onView(allOf(withId(R.id.fab_chat), childAtPosition(allOf(withId(R.id.mesh_contact_layout), childAtPosition(withId(R.id.fragment_container), 0)), 2), isDisplayed()));
        floatingActionButton.perform(click());

        addDelay(3000);

        ViewInteraction recyclerView = onView(allOf(withId(R.id.recycler_view_user), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 5)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        addDelay(2000);

        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recycler_view_user), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 5)));
        recyclerView2.perform(actionOnItemAtPosition(1, click()));

        addDelay(2000);

        ViewInteraction floatingActionButton2 = onView(allOf(withId(R.id.button_go), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 4), isDisplayed()));
        floatingActionButton2.perform(click());

        addDelay(3000);

        ViewInteraction appCompatTextView = onView(allOf(withId(R.id.text_view_last_name), childAtPosition(allOf(withId(R.id.chat_toolbar_layout), childAtPosition(withId(R.id.toolbar_chat), 0)), 1), isDisplayed()));
        appCompatTextView.perform(click());

        addDelay(2000);

        try {
            UiScrollable appViews = new UiScrollable(
                    new UiSelector().scrollable(true));

            appViews.scrollForward();
            addDelay(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView2 = onView(allOf(withId(R.id.text_view_leave_group), withText("Leave group"), childAtPosition(childAtPosition(withId(R.id.nested_scroll_view), 0), 16), isDisplayed()));
        appCompatTextView2.perform(click());

        addDelay(3000);
        ViewInteraction discoverTab = onView(allOf(withId(R.id.action_discover), childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 0), isDisplayed()));
        discoverTab.perform(click());

        addDelay(2000);

        assertTrue(true);
        StatusHelper.out("Group messaging test executed");
    }


/*    @Test
    public void uiTest_04() {
        addDelay(4000);

        UserEntity userEntityOne = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE)
                .setMeshId("0xaa2dd785fc60epb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Mike")
                .setIsFavourite(Constants.FavouriteStatus.FAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        userDataSource.insertOrUpdateData(userEntityOne);

        addDelay(3000);

        try {

            ViewInteraction contactSearchClick = onView(
                    allOf(withId(R.id.action_search),
                            childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0), isDisplayed()));
            contactSearchClick.perform(click());

            addDelay(1000);

            ViewInteraction contactSearchTextAdd = onView(
                    allOf(withId(R.id.edit_text_search),
                            childAtPosition(childAtPosition(withId(R.id.search_bar), 0), 1), isDisplayed()));
            contactSearchTextAdd.perform(replaceText("da"), closeSoftKeyboard());

            addDelay(2000);

            ViewInteraction contactSearchClear = onView(
                    allOf(withId(R.id.image_view_cross),
                            childAtPosition(childAtPosition(withId(R.id.search_bar), 0), 0), isDisplayed()));
            contactSearchClear.perform(click());

            addDelay(1000);

            ViewInteraction contactSearchBack = onView(
                    allOf(withId(R.id.image_view_back),
                            childAtPosition(childAtPosition(withId(R.id.search_bar), 0), 2), isDisplayed()));
            contactSearchBack.perform(click());

        } catch (Exception e) {
            e.printStackTrace();
        }

        addDelay(1000);

        ViewInteraction favoriteUserClick = onView(
                allOf(withId(R.id.image_view_favourite),
                        childAtPosition(childAtPosition(withId(R.id.contact_recycler_view), 1), 2), isDisplayed()));

        addDelay(1000);

        favoriteUserClick.perform(click());

        addDelay(1000);

        ViewInteraction bottomNavigationFavorite = onView(
                allOf(withId(R.id.action_contact),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 1), isDisplayed()));
        bottomNavigationFavorite.perform(click());

        addDelay(3000);

        ViewInteraction favoriteSpinner = onView(
                allOf(withId(R.id.spinner_view),
                        childAtPosition(allOf(withId(R.id.spinner_holder),
                                childAtPosition(withId(R.id.mesh_contact_layout), 0)), 0), isDisplayed()));
        favoriteSpinner.perform(click());

        addDelay(1000);

        DataInteraction favTypeSelect = onData(anything())
                .atPosition(1);
        favTypeSelect.perform(click());

        addDelay(1000);

        ViewInteraction favoriteClick = onView(
                allOf(withId(R.id.image_view_favourite),
                        childAtPosition(childAtPosition(withId(R.id.contact_recycler_view), 0), 2), isDisplayed()));
        favoriteClick.perform(click());

        addDelay(1000);

        try {

            ViewInteraction favSearchClick = onView(
                    allOf(withId(R.id.action_search),
                            childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0), isDisplayed()));
            favSearchClick.perform(click());

            addDelay(1000);

            ViewInteraction favSearchWrite = onView(
                    allOf(withId(R.id.edit_text_search),
                            childAtPosition(childAtPosition(withId(R.id.search_bar), 0), 1), isDisplayed()));
            favSearchWrite.perform(replaceText("dane"), closeSoftKeyboard());

            addDelay(2000);

            ViewInteraction favSearchClose = onView(
                    allOf(withId(R.id.image_view_back),
                            childAtPosition(childAtPosition(withId(R.id.search_bar), 0), 2), isDisplayed()));
            favSearchClose.perform(click());

        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }

            addDelay(1000);

            ViewInteraction favContactClick = onView(
                    allOf(withId(R.id.user_container),
                            childAtPosition(childAtPosition(withId(R.id.contact_recycler_view), 0), 0), isDisplayed()));


            favContactClick.perform(click());

            addDelay(2000);

            mDevice.pressBack();
    }*/

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

    public void termsOfUsePageTest() {
        addDelay(500);

        onView(withId(R.id.check_box_terms_of_use)).perform(click());

        addDelay(500);

        onView(withId(R.id.check_box_terms_of_use)).perform(click());

        addDelay(500);

        onView(withId(R.id.check_box_terms_of_use)).perform(click());

        addDelay(500);

        onView(withId(R.id.button_next)).perform(click());

        addDelay(1000);
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

    private UserEntity addSampleUser() {
        UserEntity userEntity = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_MESH_ONLINE)
                .setMeshId("0xaa2dd785fc60eeb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Daniel")
                .setIsFavourite(Constants.FavouriteStatus.UNFAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());
        //userEntity.setId(0);

        userDataSource.insertOrUpdateData(userEntity);

        return userEntity;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
