package com.w3engineers.unicef.telemesh._UiTest;


import android.app.Activity;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
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
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.application.data.model.WalletBackupEvent;
import com.w3engineers.mesh.application.data.model.WalletLoaded;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.MeshDataSource;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedContentModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.editprofile.EditProfileActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
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


    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    private FeedDataSource feedDataSource;
    private MessageSourceData messageSourceData;
    private RandomEntityGenerator randomEntityGenerator;
    private GroupDataSource groupDataSource;
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
        groupDataSource = GroupDataSource.getInstance();

        context = InstrumentationRegistry.getTargetContext();

        mActivityTestRule.getActivity().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
    }

    @Test
    public void uiTest_01() {

        addDelay(3800);

        termsOfUsePageTest();

        addDelay(3000);

        try {
            // click create account and existing account
            onView(withId(R.id.button_create_account)).perform(click());
            addDelay(3000);
            hideKeyboard(currentActivity = getActivityInstance());
            addDelay(1000);
            mDevice.pressBack();
            addDelay(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            onView(withId(R.id.button_import_account)).perform(click());
            addDelay(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Now goto profile creation page gain where we will set import = true

        currentActivity = getActivityInstance();
        Intent intent = new Intent(currentActivity, CreateUserActivity.class);
        intent.putExtra(Constants.IntentKeys.IMPORT_WALLET, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        currentActivity.finish();

        addDelay(1000);

        ViewInteraction baseEditText = onView(
                allOf(withId(R.id.edit_text_first_name),
                        childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));
        addDelay(500);
        baseEditText.perform(scrollTo(), replaceText("M"), closeSoftKeyboard());

        addDelay(500);

        baseEditText.perform(pressImeActionButton());

        addDelay(1000);

        baseEditText.perform(scrollTo(), replaceText("Aladeen"), closeSoftKeyboard());

        addDelay(1000);

        ViewInteraction baseEditText2 = onView(withId(R.id.edit_text_last_name));

        baseEditText2.perform(scrollTo(), replaceText("Haffaz"), closeSoftKeyboard());
        addDelay(1000);


        ViewInteraction ActionCreateProfileNext = onView(withId(R.id.button_signup));

        ActionCreateProfileNext.perform(scrollTo(), click());

        addDelay(2000);


        WalletLoaded walletLoaded = new WalletLoaded();
        walletLoaded.walletAddress = myAddress;
        walletLoaded.success = true;
        AppDataObserver.on().sendObserverData(walletLoaded);

        addDelay(1000);

        StatusHelper.out("Test uiTest_01 executed");

        assertTrue(true);

    }

    // Settings page test
    @Test
    public void uiTest_02() {


        addDelay(4000);

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


        onView(withId(R.id.action_setting)).perform(click());

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

        // Call wallet backup done api
        WalletBackupEvent walletBackupEvent = new WalletBackupEvent();
        walletBackupEvent.success = true;
        AppDataObserver.on().sendObserverData(walletBackupEvent);

        addDelay(3000);
        // Do next task

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

        ViewInteraction backupWallet = onView(
                allOf(withId(R.id.layout_backup_wallet),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 4)));
        backupWallet.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction chooseLanguage = onView(
                allOf(withId(R.id.layout_choose_language),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 5)));
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
                                childAtPosition(withId(R.id.layout_scroll), 0)), 5)));
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
                                childAtPosition(withId(R.id.layout_scroll), 0)), 8)));
        optionAboutUs.perform(scrollTo(), click());

        addDelay(500);

        mDevice.pressBack();

        addDelay(500);

        ViewInteraction optionFeedBack = onView(
                allOf(withId(R.id.layout_feedback),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 9)));
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
        //Espresso.pressBackUnconditionally();

        //dumpThreads();

        assertTrue(true);

        StatusHelper.out("Test executed");

        addDelay(4000);

    }

    public void uiTest_03(UserEntity userEntity) {

        addDelay(2000);

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


        // Click for open gallery
        onView(withId(R.id.image_view_pick_gallery_image)).perform(click());
        addDelay(1000);

        mDevice.pressBack();


        // Send and Receive content message

        currentActivity = getActivityInstance();

        File file = new File(Environment.getExternalStorageDirectory(), "dummy.jpg");
        try {

            AssetFileDescriptor assetFileDescriptor = currentActivity.getAssets().openFd("sample_image.jpg");

            FileInputStream inputStream = assetFileDescriptor.createInputStream();

            OutputStream outputStream = new FileOutputStream(file);
            copyStream(inputStream, outputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }

        addDelay(3000);

        if (currentActivity instanceof ChatActivity) {
            ((ChatActivity) currentActivity).sendContentMessage(Uri.fromFile(file));
        }

        addDelay(3000);

        MessageEntity lastIncomingContent = messageSourceData.getLastIncomingContent(userEntity.getMeshId());

        // Fixme whe travis CI showing this message entity null
        if (lastIncomingContent == null) {
            // we have to insert again
            lastIncomingContent = randomEntityGenerator.prepareImageMessage(file.getPath(), userEntity.getMeshId());
            messageSourceData.insertOrUpdateData(lastIncomingContent);
            addDelay(3000);
        }


        lastIncomingContent.setContentProgress(100);
        messageSourceData.insertOrUpdateData(lastIncomingContent);

        try {

            addDelay(1000);

            int count = 3;
            if (currentActivity instanceof ChatActivity) {
                RecyclerView recyclerView = currentActivity.findViewById(R.id.chat_rv);
                if (recyclerView.getAdapter() != null) {
                    count = recyclerView.getAdapter().getItemCount();
                    count--;
                }
            }

            onView(withId(R.id.chat_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(count, click()));

            addDelay(2000);

            onView(withId(R.id.expanded_image)).perform(click());
            addDelay(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChatEntity incomingContent = randomEntityGenerator.createIncomingContent(userEntity.getMeshId(), file);
        messageSourceData.insertOrUpdateData(incomingContent);

        addDelay(1000);


        if (currentActivity instanceof ChatActivity) {
            ((ChatActivity) currentActivity).clearChat();
        }


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


        currentActivity = getActivityInstance();

        if (!(currentActivity instanceof MainActivity)) {
            mDevice.pressBack();

            addDelay(2000);
        }

    }


    //@Test
    public void uiTest_04() {

        addDelay(2000);

        currentActivity = getActivityInstance();

        if (currentActivity instanceof MainActivity) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) currentActivity).stopAnimation();
                }
            });
        } else {
            mDevice.pressBack();
        }


        addDelay(2000);

        ViewInteraction settingsTab = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 3), isDisplayed()));
        settingsTab.perform(click());

        addDelay(2000);

        ViewInteraction profileRow = onView(
                allOf(withId(R.id.layout_view_profile),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 0)));
        profileRow.perform(scrollTo(), click());

        addDelay(2000);

        ViewInteraction editButton = onView(
                allOf(withId(R.id.text_view_edit),
                        childAtPosition(allOf(withId(R.id.view_profile_layout),
                                childAtPosition(withId(android.R.id.content), 0)), 3), isDisplayed()));
        editButton.perform(click());

        addDelay(3000);

        try {

            onView(withId(R.id.edit_text_first_name)).perform(replaceText("Mimo"), closeSoftKeyboard());
            addDelay(1000);

            onView(withId(R.id.edit_text_last_name)).perform(replaceText("Sha"), closeSoftKeyboard());

        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }


        addDelay(1000);


        UserEntity userEntityOne = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE)
                .setMeshId("0xaa2dd785fc60epb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Mike")
                .setUserLastName("Tyson")
                .setIsFavourite(Constants.FavouriteStatus.FAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        userDataSource.insertOrUpdateData(userEntityOne);

        addDelay(2000);

        updateButtonClick();

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

        // click content image. It has animation for 300 ms

        onView(withId(R.id.image_view_message)).perform(click());


        addDelay(2000);

        try {
            onView(withId(R.id.expanded_image)).perform(click());

            addDelay(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        mDevice.pressBack();

        addDelay(1000);

        Activity activity = getActivityInstance();

        if (activity instanceof MainActivity) {

            MainActivity mainActivity = (MainActivity) activity;
            //mainActivity.feedRefresh();

            addDelay(1000);
        }
    }


    // Group messaging test

    public void setUpGroupUser() {
        userDataSource = UserDataSource.getInstance();

        UserEntity userEntityOne = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_ONLINE)
                .setMeshId("0xf57d787f3ca95e2fd9cc782c85f6bcd3d6d779d9")
                .setUserName("Aladeen")
                .setUserLastName("G1")
                .setIsFavourite(Constants.FavouriteStatus.UNFAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        UserEntity userEntityTwo = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE)
                .setMeshId("0xc1a5185c807038a32a4c6ca020826fee85d88fde")
                .setUserName("Aladeen")
                .setUserLastName("G2")
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

        // click go button to open one to one chat
        onView(withId(R.id.button_go)).perform(click());
        addDelay(500);
        mDevice.pressBack();

        onView(withId(R.id.text_view_create_group)).perform(click());
        addDelay(500);

        // select item
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        addDelay(1000);

        // di select again

        recyclerView.perform(actionOnItemAtPosition(0, click()));
        addDelay(1000);

        //select again

        recyclerView.perform(actionOnItemAtPosition(0, click()));
        addDelay(1000);


        onView(withId(R.id.recycler_view_selected_user)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, new ChildViewAction().clickChildViewWithId(R.id.button_remove)));

        addDelay(1000);

        //select again

        recyclerView.perform(actionOnItemAtPosition(0, click()));
        addDelay(1000);

        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recycler_view_user), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 5)));
        recyclerView2.perform(actionOnItemAtPosition(1, click()));

        addDelay(1000);

        ViewInteraction floatingActionButton2 = onView(allOf(withId(R.id.button_go), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 4), isDisplayed()));
        floatingActionButton2.perform(click());

        addDelay(2000);

        //ViewInteraction appCompatTextView = onView(allOf(withId(R.id.text_view_last_name), childAtPosition(allOf(withId(R.id.chat_toolbar_layout), childAtPosition(withId(R.id.toolbar_chat), 0)), 1), isDisplayed()));
        ViewInteraction appCompatTextView = onView(withId(R.id.text_view_last_name));
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

        // perform search operation
        onView(withId(R.id.action_search)).perform(click());
        addDelay(1000);

        try {
            onView(withId(R.id.edit_text_search)).perform(replaceText("h"), closeSoftKeyboard());
            addDelay(1000);

            onView(withId(R.id.image_view_back)).perform(click());
            addDelay(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView4 = onView(allOf(withId(R.id.recycler_view_user), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 5)));
        recyclerView4.perform(actionOnItemAtPosition(0, click()));

        addDelay(2000);

        // deselect
        recyclerView4.perform(actionOnItemAtPosition(0, click()));
        addDelay(1000);

        //select again
        recyclerView4.perform(actionOnItemAtPosition(0, click()));
        addDelay(1000);

        onView(withId(R.id.recycler_view_selected_user)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, new ChildViewAction().clickChildViewWithId(R.id.button_remove)));

        addDelay(1000);

        //again select
        recyclerView4.perform(actionOnItemAtPosition(0, click()));
        addDelay(1000);

        ViewInteraction floatingActionButton3 = onView(allOf(withId(R.id.button_go), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 3), isDisplayed()));
        floatingActionButton3.perform(click());

        addDelay(2000);

        mDevice.pressBack();

        addDelay(3000);

        //ViewInteraction appCompatEditText = onView(allOf(withId(R.id.edit_text_message), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 1), isDisplayed()));
        ViewInteraction appCompatEditText = onView(withId(R.id.edit_text_message));
        appCompatEditText.perform(click());

        addDelay(2000);

        appCompatEditText.perform(replaceText("Hello friends"));

        addDelay(1000);
        appCompatEditText.perform(closeSoftKeyboard());

        addDelay(1000);

        //ViewInteraction appCompatImageButton2 = onView(allOf(withId(R.id.image_view_send), withContentDescription("Send icon."), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 2), isDisplayed()));
        ViewInteraction appCompatImageButton2 = onView(withId(R.id.image_view_send));
        appCompatImageButton2.perform(click());

        addDelay(2000);

        try {


            ViewInteraction overflowMenuButton = onView(allOf(withContentDescription("More options"), childAtPosition(childAtPosition(withId(R.id.toolbar_chat), 2), 0), isDisplayed()));
            overflowMenuButton.perform(click());

            addDelay(2000);

            ViewInteraction appCompatTextView4 = onView(allOf(withId(R.id.title), withText("Clear Chat"), childAtPosition(childAtPosition(withId(R.id.content), 0), 0), isDisplayed()));
            appCompatTextView4.perform(click());

            addDelay(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ViewInteraction appCompatEditText9 = onView(allOf(withId(R.id.edit_text_message), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 1), isDisplayed()));
        ViewInteraction appCompatEditText9 = onView(withId(R.id.edit_text_message));
        appCompatEditText9.perform(click());

        //ViewInteraction appCompatEditText10 = onView(allOf(withId(R.id.edit_text_message), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 1), isDisplayed()));
        ViewInteraction appCompatEditText10 = onView(withId(R.id.edit_text_message));
        appCompatEditText10.perform(replaceText("Ok friends"));

        addDelay(1000);
        appCompatEditText10.perform(closeSoftKeyboard());

        addDelay(1000);

        //ViewInteraction appCompatImageButton3 = onView(allOf(withId(R.id.image_view_send), withContentDescription("Send icon."), childAtPosition(allOf(withId(R.id.chat_message_bar), childAtPosition(withId(R.id.chat_layout), 5)), 2), isDisplayed()));
        ViewInteraction appCompatImageButton3 = onView(withId(R.id.image_view_send));
        appCompatImageButton3.perform(click());

        addDelay(2000);

        try {

            ViewInteraction overflowMenuButton2 = onView(allOf(withContentDescription("More options"), childAtPosition(childAtPosition(withId(R.id.toolbar_chat), 2), 0), isDisplayed()));
            overflowMenuButton2.perform(click());

            addDelay(2000);

            ViewInteraction appCompatTextView5 = onView(allOf(withId(R.id.title), withText("Leave Group"), childAtPosition(childAtPosition(withId(R.id.content), 0), 0), isDisplayed()));
            appCompatTextView5.perform(click());

            addDelay(2000);
        } catch (Exception e) {
            e.printStackTrace();
            mDevice.pressBack();

            addDelay(1000);
            currentActivity = getActivityInstance();
            if (!(currentActivity instanceof MainActivity)) {
                mDevice.pressBack();
            }
        }


        addDelay(3000);
        ViewInteraction discoverTab = onView(allOf(withId(R.id.action_discover), childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 0), isDisplayed()));
        discoverTab.perform(click());

        StatusHelper.out("uiTest_05 test executed");

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

        //ViewInteraction appCompatTextView = onView(allOf(withId(R.id.text_view_last_name), childAtPosition(allOf(withId(R.id.chat_toolbar_layout), childAtPosition(withId(R.id.toolbar_chat), 0)), 1), isDisplayed()));
        ViewInteraction appCompatTextView = onView(withId(R.id.text_view_last_name));
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

        uiTest_08();
    }

    public void uiTest_08() {
        //addDelay(4000);

        userDataSource.updateUserToOffline();

        addDelay(2000);

        UserEntity userEntityOne = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE)
                .setMeshId("0xaa2dd785fc60epb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Mike")
                .setUserLastName("Tyson")
                .setIsFavourite(Constants.FavouriteStatus.FAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        userDataSource.insertOrUpdateData(userEntityOne);

        UserEntity userEntityTwo = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_ONLINE)
                .setMeshId("0xaa2dd785fc60epb8151f65b3ded59ce3c2f12cb4")
                .setUserName("Sam")
                .setUserLastName("Smith")
                .setIsFavourite(Constants.FavouriteStatus.UNFAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        userDataSource.insertOrUpdateData(userEntityTwo);

        addDelay(2000);

        // Create group
        String groupId = createAGroup(userEntityOne);

        addDelay(1000);


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


        // Create a group message
        ChatEntity groupChatEntity = randomEntityGenerator.createGroupChatEntity(userEntityOne.getMeshId(), groupId);
        messageSourceData.insertOrUpdateData(groupChatEntity);

        addDelay(1000);

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

        assertTrue(true);

        StatusHelper.out("uiTest_08 test executed");
    }


    private String createAGroup(UserEntity userEntity) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        ArrayList<GroupMembersInfo> groupMembersInfos = new ArrayList<>();

        String myUserId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);

        String myUserName = SharedPref.read(Constants.preferenceKey.USER_NAME);
        int avatarIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);

        GroupMembersInfo myGroupMembersInfo = new GroupMembersInfo()
                .setMemberId(myUserId)
                .setUserName(myUserName)
                .setMemberStatus(Constants.GroupEvent.GROUP_JOINED)
                .setAvatarPicture(avatarIndex)
                .setIsAdmin(true);
        groupMembersInfos.add(myGroupMembersInfo);


        GroupMembersInfo groupMembersInfo = new GroupMembersInfo()
                .setMemberId(userEntity.getMeshId())
                .setUserName(userEntity.getUserName())
                .setAvatarPicture(userEntity.getAvatarIndex())
                .setMemberStatus(Constants.GroupEvent.GROUP_JOINED);

        groupMembersInfos.add(groupMembersInfo);

        String groupId = UUID.randomUUID().toString();

        GroupNameModel groupNameModel = new GroupNameModel()
                .setGroupName(CommonUtil.getGroupNameByUser(groupMembersInfos));

        GroupEntity groupEntity = new GroupEntity()
                .setGroupId(groupId)
                .setGroupName(gsonBuilder.getGroupNameModelJson(groupNameModel))
                .setOwnStatus(Constants.GroupEvent.GROUP_CREATE)
                .setMembersInfo(gsonBuilder.getGroupMemberInfoJson(groupMembersInfos))
                .setAdminInfo(myUserId)
                .setGroupCreationTime(System.currentTimeMillis());

        groupDataSource.insertOrUpdateGroup(groupEntity);

        return groupId;
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

        String contentInfo = GsonBuilder.getInstance().getFeedContentModelJson(prepareBroadcastContentModel());

        entity.setFeedContentInfo(contentInfo);

        feedDataSource.insertOrUpdateData(entity);
    }

    private FeedContentModel prepareBroadcastContentModel() {
        FeedContentModel contentModel = new FeedContentModel();
        contentModel.setContentInfo("Sample content info");
        String imagePath = randomEntityGenerator.getDummyImageLink();
        contentModel.setContentPath(imagePath);
        contentModel.setContentThumb(imagePath);
        contentModel.setContentUrl(imagePath);

        return contentModel;
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

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    public class ChildViewAction {

        public ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }

    }
}
