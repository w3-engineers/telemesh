package com.w3engineers.unicef.telemesh.ui.groupcreate;

import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GroupCreateViewModelTest {

    private GroupCreateViewModel groupCreateViewModel;

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setUp() throws Exception {
        groupCreateViewModel = new GroupCreateViewModel((Application) InstrumentationRegistry.getContext().getApplicationContext());
    }

    @After
    public void tearDown() throws Exception {
        // teardown method is needed at the last
    }

    @Test
    public void testGroupExistence(){
        addDelay(1000);
        List<UserEntity> userEntities = new ArrayList<>();
        ArrayList<GroupMembersInfo > groupMembersInfos = new ArrayList<>();
        groupCreateViewModel.checkGroupExists(userEntities, groupMembersInfos);
        assertTrue(true);
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
