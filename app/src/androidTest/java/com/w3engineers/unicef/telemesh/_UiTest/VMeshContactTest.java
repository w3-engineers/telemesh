package com.w3engineers.unicef.telemesh._UiTest;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class VMeshContactTest {
    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);
    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());


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

        //sharedPref = SharedPref.getSharedPref(context);
    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
    }

    /**
     * Mesh contact fragment test
     * User contact and group list included
     */

    @Test
    public void uiTest_08() {
        addDelay(4000);

        UserEntity userEntityOne = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE)
                .setMeshId("0xaa2dd785fc60epb8151f65b3ded59ce3c2f12ca4")
                .setUserName("Mike")
                .setIsFavourite(Constants.FavouriteStatus.FAVOURITE)
                .setRegistrationTime(System.currentTimeMillis());

        userDataSource.insertOrUpdateData(userEntityOne);

        UserEntity userEntityTwo = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.WIFI_ONLINE)
                .setMeshId("0xaa2dd785fc60epb8151f65b3ded59ce3c2f12cb4")
                .setUserName("Sam")
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

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
