package com.w3engineers.unicef.telemesh.ui.splashscreen;/*
package com.w3engineers.unicef.telemesh.ui.splashscreen;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.w3engineers.unicef.telemesh.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SplashActivityTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void splashActivityTest() {
        ViewInteraction baseButton = onView(
                allOf(withId(R.id.button_create_account), withText("I'm a New User"),
                        childAtPosition(
                                allOf(withId(R.id.activity_profile_choice_parent),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        baseButton.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button2), withText("I’m a new user"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                2)));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction baseEditText = onView(
                allOf(withId(R.id.edit_text_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.name_layout),
                                        0),
                                0)));
        baseEditText.perform(scrollTo(), replaceText("hsy"), closeSoftKeyboard());

        ViewInteraction baseButton2 = onView(
                allOf(withId(R.id.button_signup), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.image_layout),
                                        childAtPosition(
                                                withId(R.id.scrollview),
                                                0)),
                                10)));
        baseButton2.perform(scrollTo(), click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.button_skip), withText("Continue"),
                        childAtPosition(
                                allOf(withId(R.id.activity_security_scroll_parent),
                                        childAtPosition(
                                                withId(R.id.activity_security_scroll),
                                                0)),
                                5)));
        appCompatTextView.perform(scrollTo(), click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_search), withContentDescription("Search People"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_text_search),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.search_bar),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("da"), closeSoftKeyboard());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.image_view_cross), withContentDescription("Clear search text"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.search_bar),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.user_container),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.contact_recycler_view),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout.perform(click());

        pressBack();

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.action_search), withContentDescription("Search People"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.image_view_back), withContentDescription("Close search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.search_bar),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.image_view_favourite), withContentDescription("Favourite"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.contact_recycler_view),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.action_contact),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.spinner_view),
                        childAtPosition(
                                allOf(withId(R.id.spinner_holder),
                                        childAtPosition(
                                                withId(R.id.mesh_contact_layout),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.action_discover),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.image_view_favourite), withContentDescription("Favourite"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.contact_recycler_view),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView4.perform(click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.action_contact),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction appCompatImageView5 = onView(
                allOf(withId(R.id.image_view_favourite),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.contact_recycler_view),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView5.perform(click());

        ViewInteraction constraintLayout2 = onView(
                allOf(withId(R.id.user_container),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.contact_recycler_view),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout2.perform(click());

        pressBack();

        ViewInteraction actionMenuItemView3 = onView(
                allOf(withId(R.id.action_search), withContentDescription("Search People"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView3.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_text_search),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.search_bar),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("dane"), closeSoftKeyboard());

        ViewInteraction appCompatImageView6 = onView(
                allOf(withId(R.id.image_view_back), withContentDescription("Close search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.search_bar),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView6.perform(click());
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
*/
