package com.w3engineers.unicef.util.helper;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getInstrumentation;

import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class CommonUtilTest {
    private Context mContext;
    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    @UiThreadTest
    public void permissionPopupTest() {
        addDelay(500);
        CommonUtil commonUtil = new CommonUtil();
        commonUtil.showPermissionPopUp(rule.getActivity());

        addDelay(5000);

        CommonUtil.dismissDialog();

    }

    @Test
    @UiThreadTest
    public void showLocationPermissionPopupTest() {

        addDelay(500);

        GroupEntity entity = new GroupEntity().setGroupId("gId");
        entity.lastPersonId = "myId";
        String name = CommonUtil.getUserName(entity, "myId");
        entity.lastPersonId = "otherId";
        entity.lastPersonName = "John";

        name = CommonUtil.getUserName(entity, "myId");

        GroupMembersInfo membersInfo = new GroupMembersInfo();
        membersInfo.setMemberId("otherId");
        membersInfo.setUserName("Alice");

        ArrayList<GroupMembersInfo> groupMemberList = new ArrayList<>();
        groupMemberList.add(membersInfo);

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();
        String memberIno = gsonBuilder.getGroupMemberInfoJson(groupMemberList);
        entity.setMembersInfo(memberIno);
        entity.lastPersonName = "";
        name = CommonUtil.getUserName(entity, "myId");


        addDelay(1000);
        CommonUtil.showGpsOrLocationOffPopup(rule.getActivity());

        addDelay(2000);

        CommonUtil.dismissDialog();
    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}