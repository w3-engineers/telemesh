package com.w3engineers.unicef.telemesh._UiTest;

import android.app.Activity;

import androidx.room.Room;
import android.content.Intent;
import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.uiautomator.UiDevice;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.ui.importprofile.ImportProfileActivity;
import com.w3engineers.unicef.telemesh.ui.importwallet.ImportWalletActivity;
import com.w3engineers.unicef.telemesh.ui.profilechoice.ProfileChoiceActivity;
import com.w3engineers.walleter.wallet.Web3jWalletHelper;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Collection;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@RunWith(AndroidJUnit4.class)
public class VProfileChoiceTest {

    public String myAddress = "0x550de922bec427fc1b279944e47451a89a4f7cag";
    public String publicKey = "0x04647ba47589ace7e9636029e5355b9b71c1c66ccd3c1b7c127f3c21016dacea7d3aa12e41eca790d4c3eff8398fd523dc793c815da7bbdbf29c8744b761ad8e4c";
    public String defaultPassword = "mesh_123";

    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

    private AppDatabase appDatabase;
    private UserDataSource userDataSource;

    @Rule
    public ActivityTestRule<ProfileChoiceActivity> rule = new ActivityTestRule<>(ProfileChoiceActivity.class);

    @Before
    public void setUp() {
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = UserDataSource.getInstance();
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void vUITest_01() {
        addDelay(3200);

        ViewInteraction buttonImportAccount = onView(
                allOf(withId(R.id.button_import_account),
                        childAtPosition(
                                childAtPosition(withId(android.R.id.content), 0), 3),
                        isDisplayed()));
        buttonImportAccount.perform(click());

        addDelay(1000);

        Activity currentActivity = getActivityInstance();

        if (currentActivity instanceof ImportProfileActivity) {
            ImportProfileActivity importProfileActivity = (ImportProfileActivity) currentActivity;
            importProfileActivity.setIsEmulatorTestingMode(true);


            Activity finalCurrentActivity = currentActivity;
            currentActivity.runOnUiThread(() -> {
                ((ImportProfileActivity) finalCurrentActivity).showWarningDialog();

                addDelay(5000);
            });

        }

        onView(withText("CANCEL")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());


        addDelay(2000);

        Activity currentActivity1 = getActivityInstance();
        if (currentActivity1 instanceof ImportProfileActivity) {

            String walletSuffixDir = "wallet/" + currentActivity1.getResources().getString(com.w3engineers.mesh.R.string.app_name);

            String filePath = Web3jWalletHelper.onInstance(currentActivity1).getWalletDir(walletSuffixDir);
            File directory = new File(filePath);

            Intent intent = new Intent();
            intent.setData(Uri.fromFile(directory));

            // Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

            currentActivity1.startActivityForResult(new Intent(currentActivity1, ImportWalletActivity.class), 480);

            addDelay(2000);

            Activity walletActivity = getActivityInstance();

            if (walletActivity instanceof ImportWalletActivity) {
                walletActivity.setResult(Activity.RESULT_OK, intent);

                walletActivity.finish();
            }

            addDelay(3000);

            Activity walletActivity1 = getActivityInstance();
            if (walletActivity1 instanceof ImportWalletActivity) {
                walletActivity1.finish();
            }

            addDelay(3000);
        }


        addDelay(3000);

       /* ViewInteraction importAnotherId = onView(
                allOf(withId(R.id.button_continue),
                        childAtPosition(allOf(withId(R.id.activity_import_profile_scroll_parent),
                                childAtPosition(withId(R.id.activity_import_profile_scroll), 0)), 3)));

        importAnotherId.perform(scrollTo(), click());*/

        currentActivity = getActivityInstance();

        if (currentActivity instanceof ImportProfileActivity) {


            onView(withId(R.id.button_continue)).perform(setTextViewVisibility(true));
            addDelay(100);

            onView(withId(R.id.button_continue)).perform(click());
        }

        addDelay(2000);

        try {
            ViewInteraction importWalletBack = onView(
                    allOf(withId(R.id.image_view_back),
                            childAtPosition(allOf(withId(R.id.activity_import_wallet_scroll_parent),
                                    childAtPosition(withId(R.id.activity_import_wallet_scroll), 0)), 0), isDisplayed()));
            importWalletBack.perform(click());

            addDelay(1000);

            ViewInteraction importAnotherIdSecond = onView(
                    allOf(withId(R.id.button_continue),
                            childAtPosition(allOf(withId(R.id.activity_import_profile_scroll_parent),
                                    childAtPosition(withId(R.id.activity_import_profile_scroll), 0)), 3)));
            importAnotherIdSecond.perform(scrollTo(), click());

            addDelay(2000);

        } catch (PerformException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_text_password),
                        childAtPosition(childAtPosition(withId(R.id.password_layout), 0), 0), isDisplayed()));
        appCompatEditText.perform(replaceText("m"), closeSoftKeyboard());

        addDelay(1000);

        ViewInteraction importContinue = onView(
                allOf(withId(R.id.button_continue),
                        childAtPosition(allOf(withId(R.id.activity_import_wallet_scroll_parent),
                                childAtPosition(withId(R.id.activity_import_wallet_scroll), 0)), 7), isDisplayed()));

//        importContinue.perform(click());
        appCompatEditText.perform(pressImeActionButton());

        addDelay(500);

        appCompatEditText.perform(replaceText(defaultPassword), closeSoftKeyboard());

        addDelay(500);

        currentActivity = getActivityInstance();

        if (currentActivity instanceof ImportWalletActivity) {
            ImportWalletActivity importWalletActivity = (ImportWalletActivity) currentActivity;

            importWalletActivity.failedWalletResponse("Wallet not exist");

            addDelay(1000);
        }

        importContinue.perform(click());

        addDelay(5000);

        if (currentActivity instanceof ImportWalletActivity) {
            ImportWalletActivity importWalletActivity = (ImportWalletActivity) currentActivity;

            importWalletActivity.successWalletResponse(myAddress, publicKey, defaultPassword);
        }

        addDelay(1000);

        ViewInteraction baseEditText = onView(
                allOf(withId(R.id.edit_text_name),
                        childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));

        baseEditText.perform(scrollTo(), replaceText(""), closeSoftKeyboard());

        addDelay(1000);

        try {
            ViewInteraction ActionCreateProfileNext = onView(
                    allOf(withId(R.id.button_signup),
                            childAtPosition(allOf(withId(R.id.image_layout),
                                    childAtPosition(withId(R.id.scrollview), 0)), 10)));

            addDelay(1000);

            hideKeyboard(getActivityInstance());

            addDelay(2000);

            ActionCreateProfileNext.perform(scrollTo(), click());

            addDelay(1000);

            baseEditText.perform(scrollTo(), replaceText("M"), closeSoftKeyboard());

            addDelay(1000);

            ActionCreateProfileNext.perform(scrollTo(), click());

            addDelay(1000);

            baseEditText.perform(scrollTo(), replaceText("Mimo"), closeSoftKeyboard());

            addDelay(1000);

            ActionCreateProfileNext.perform(scrollTo(), click());

            addDelay(2000);
        } catch (Exception e) {
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

    private static ViewAction setTextViewVisibility(final boolean value) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(Button.class);
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public String getDescription() {
                return "Show / Hide View";
            }
        };
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