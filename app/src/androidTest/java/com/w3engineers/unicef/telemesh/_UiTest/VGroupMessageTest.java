package com.w3engineers.unicef.telemesh._UiTest;



import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashActivity;

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

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class VGroupMessageTest {

    private UserDataSource userDataSource;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);
    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

    @Before
    public void setUp() {
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

    @After
    public void closeDb() throws IOException { }

    @Test
    public void uiTest_05() {

        addDelay(4000);

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

        addDelay(2000);

        ViewInteraction appCompatImageView2 = onView(allOf(withId(R.id.image_view_remove), withContentDescription("Remove group member"), childAtPosition(childAtPosition(withId(R.id.recycler_view_group_member), 2), 2), isDisplayed()));
        appCompatImageView2.perform(click());

        addDelay(2000);

        ViewInteraction appCompatTextView3 = onView(allOf(withId(R.id.text_view_add_member), withText("Add member"), childAtPosition(childAtPosition(withId(R.id.nested_scroll_view), 0), 13), isDisplayed()));
        appCompatTextView3.perform(click());

        addDelay(2000);

        ViewInteraction recyclerView4 = onView(allOf(withId(R.id.recycler_view_user), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 5)));
        recyclerView4.perform(actionOnItemAtPosition(0, click()));

        addDelay(2000);

        ViewInteraction floatingActionButton3 = onView(allOf(withId(R.id.button_go), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 3), isDisplayed()));
        floatingActionButton3.perform(click());

        addDelay(2000);

        mDevice.pressBack();

        addDelay(2000);

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

//        uiTest_07();
    }

    @Test
    public void uiTest_07() {

        addDelay(3000);

        ViewInteraction floatingActionButton = onView(allOf(withId(R.id.fab_chat), childAtPosition(allOf(withId(R.id.mesh_contact_layout), childAtPosition(withId(R.id.fragment_container), 0)), 2), isDisplayed()));
        floatingActionButton.perform(click());

        addDelay(2000);

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

        ViewInteraction appCompatTextView2 = onView(allOf(withId(R.id.text_view_leave_group), withText("Leave group"), childAtPosition(childAtPosition(withId(R.id.nested_scroll_view), 0), 16), isDisplayed()));
        appCompatTextView2.perform(click());

        addDelay(3000);
        ViewInteraction discoverTab = onView(allOf(withId(R.id.action_discover), childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 0), isDisplayed()));
        discoverTab.perform(click());

    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

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
