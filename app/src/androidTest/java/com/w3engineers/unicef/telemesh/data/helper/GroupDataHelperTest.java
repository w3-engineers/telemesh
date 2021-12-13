package com.w3engineers.unicef.telemesh.data.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.text.TextUtils;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.grouptable.ForwardGroupModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupCountModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.RelayGroupModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.GroupMessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.TimeUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class GroupDataHelperTest {

    private RmDataHelper rmDataHelper;
    private GroupDataHelper groupDataHelper;
    private GroupDataSource groupDataSource;
    private AppDatabase appDatabase;
    private GsonBuilder gsonBuilder;
    private String myUserId;

    private final String dainelId = "0xaa2dd785fc60eeb8151f65b3ded59ce3c2f12ca4";
    private final String simonId = "0xaa2dd785fc60eeb8151f65b3ded59ce3c2f13ca4";
    private final String mikeId = "0xaa2dd785fc60epb8151f65b3ded59ce3c2f12ca4";

    @Before
    public void setUp() throws Exception {
        rmDataHelper = RmDataHelper.getInstance();
        groupDataHelper = GroupDataHelper.getInstance();
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();
        groupDataSource = GroupDataSource.getInstance();
        gsonBuilder = GsonBuilder.getInstance();

        myUserId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);
    }

    @After
    public void tearDown() throws Exception {
        appDatabase.close();
    }

    @Test
    public void test_group_create_join_event() {
        GroupModel groupModel = prepareGroupModel();

        addDelay(500);

        GroupEntity groupEntity = new GroupEntity()
                .toGroupEntity(groupModel);

        groupDataSource.insertOrUpdateGroup(groupEntity);

        addDelay(1000);

        // Send group count to internet user
        List<GroupEntity> groupStatics = new ArrayList<>();
        groupStatics.add(groupEntity);
        RmDataHelper.getInstance().sendGroupCountToInternetUser(groupStatics);

        addDelay(1500);

        String groupCreationJson = gsonBuilder.getGroupModelJson(groupModel);

        DataModel groupCreationModel = new DataModel();
        groupCreationModel.setDataType(Constants.DataType.EVENT_GROUP_CREATION);
        groupCreationModel.setRawData(groupCreationJson.getBytes());
        groupCreationModel.setUserId(dainelId);

        rmDataHelper.dataReceive(groupCreationModel, true);

        addDelay(1500);

        // Group rename
        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_RENAME, dainelId, groupCreationJson.getBytes(), true);

        addDelay(1500);

        // group member add

        ArrayList<GroupMembersInfo> groupMemberList = gsonBuilder.getGroupMemberInfoObj(groupModel.getMemberInfo());

        GroupMembersInfo member = new GroupMembersInfo()
                .setUserName("Simon")
                .setMemberId(simonId)
                .setAvatarPicture(4);

        groupMemberList.add(member);


        groupModel.setMemberInfo(gsonBuilder.getGroupMemberInfoJson(groupMemberList));
        groupCreationJson = gsonBuilder.getGroupModelJson(groupModel);

        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_MEMBER_ADD, simonId, groupCreationJson.getBytes(), true);

        addDelay(1500);

        // Group member remove

        ArrayList<GroupMembersInfo> removedMember = new ArrayList<>();
        removedMember.add(member);

        groupModel.setMemberInfo(gsonBuilder.getGroupMemberInfoJson(removedMember));
        groupCreationJson = gsonBuilder.getGroupModelJson(groupModel);

        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_MEMBER_REMOVE, simonId, groupCreationJson.getBytes(), true);
        addDelay(1500);

        // group member leave
        member = new GroupMembersInfo()
                .setUserName("Daniel")
                .setMemberId(dainelId)
                .setAvatarPicture(4);

        removedMember = new ArrayList<>();
        removedMember.add(member);

        groupModel.setMemberInfo(gsonBuilder.getGroupMemberInfoJson(removedMember));
        groupCreationJson = gsonBuilder.getGroupModelJson(groupModel);

        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_LEAVE, dainelId, groupCreationJson.getBytes(), true);
        addDelay(1500);

        // Group data rely
        RelayGroupModel relayGroupModel = prepareRelayGroupModel();
        String relyGroupJson = gsonBuilder.getRelayGroupModelJson(relayGroupModel);

        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_DATA_RELAY, mikeId, relyGroupJson.getBytes(), true);
        addDelay(1500);

        // group data forward
        ForwardGroupModel forwardGroupModel = prepareForwardGroupModel();
        forwardGroupModel.setData(groupCreationJson);
        String forwardJson = gsonBuilder.getForwarderGroupModelJson(forwardGroupModel);
        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_DATA_FORWARD, mikeId, forwardJson.getBytes(), true);
        addDelay(1500);

        // Get specific group member from group
        GroupMembersInfo gInfo = CommonUtil.getGroupMemberInfo(groupModel.getMemberInfo(), dainelId);

        assertNotNull(gInfo);
    }

    @Test
    public void test_group_analytics_test() {

        // Receive and send group analytics to server

        String groupId = UUID.randomUUID().toString();

        GroupCountModel groupCountModel = new GroupCountModel();
        groupCountModel.setGroupId(groupId);
        groupCountModel.setGroupOwnerId(myUserId);
        groupCountModel.setMemberCount(3);
        groupCountModel.setCreatedTime(TimeUtil.getDateStringFromMillisecond(System.currentTimeMillis()));
        groupCountModel.setDirectSend(false);
        groupCountModel.setSubmittedBy(dainelId);

        ArrayList<GroupCountModel> countModelArrayList = new ArrayList<>();
        countModelArrayList.add(groupCountModel);

        String groupCountSyncRequest = gsonBuilder.getGroupCountJson(countModelArrayList);

        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_COUNT_SYNC_REQUEST,
                dainelId, groupCountSyncRequest.getBytes(), true);

        addDelay(2000);


        // receive group count sync ack

        GroupModel model = prepareGroupModel();
        GroupEntity groupEntity = new GroupEntity().toGroupEntity(model);
        groupEntity.setSynced(false);
        groupDataSource.insertOrUpdateGroup(groupEntity);

        groupCountModel.setGroupId(model.getGroupId());

        countModelArrayList = new ArrayList<>();
        countModelArrayList.add(groupCountModel);

        groupCountSyncRequest = gsonBuilder.getGroupCountJson(countModelArrayList);

        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_COUNT_SYNC_ACK,
                dainelId, groupCountSyncRequest.getBytes(), true);


        addDelay(2000);

        //Receive group id for sync

        groupDataHelper.groupDataReceive(Constants.DataType.EVENT_GROUP_COUNT_SYNCED,
                dainelId, model.getGroupId().getBytes(), true);

        addDelay(1000);

        assertTrue(true);
    }

    @Test
    public void test_group_message_send() {
        addDelay(1000);

        GroupModel model = prepareGroupModel();
        GroupEntity groupEntity = new GroupEntity().toGroupEntity(model);
        groupEntity.setSynced(false);
        groupDataSource.insertOrUpdateGroup(groupEntity);

        groupDataHelper.sendTextMessageToGroup(model.getGroupId(), "Hello friends");

        addDelay(1000);

        GroupMessageEntity groupMessageEntity = new GroupMessageEntity();
        groupMessageEntity.setGroupId(groupEntity.getGroupId());
        groupMessageEntity.setMessage("hello");

        groupDataHelper.prepareAndSendGroupContent(groupMessageEntity, true);
        addDelay(1000);
        assertTrue(true);

    }

    @Test
    public void testContent(){
        addDelay(1000);

        GroupModel model = prepareGroupModel();
        GroupEntity groupEntity = new GroupEntity().toGroupEntity(model);
        groupEntity.setSynced(false);
        groupDataSource.insertOrUpdateGroup(groupEntity);

        groupDataHelper.sendTextMessageToGroup(model.getGroupId(), "Hello friends");

        addDelay(1000);

        GroupMessageEntity groupMessageEntity = new GroupMessageEntity();
        groupMessageEntity.setGroupId(groupEntity.getGroupId());
        groupMessageEntity.setMessage("hello");

        UserEntity entity = new UserEntity();
        entity.setMeshId(dainelId);

        groupDataHelper.prepareContent(groupMessageEntity, entity);
        assertTrue(true);
    }

    private ForwardGroupModel prepareForwardGroupModel() {

        return new ForwardGroupModel()
                .setType(Constants.DataType.EVENT_GROUP_LEAVE)
                .setSender("0xaa2dd785fc60epb8151f65b3ded59ce3c2f12ca5");
    }

    private RelayGroupModel prepareRelayGroupModel() {
        RelayGroupModel relayGroupModel = new RelayGroupModel();
        relayGroupModel.setData("test data");
        relayGroupModel.setType(Constants.DataType.EVENT_GROUP_LEAVE);
        List<String> users = new ArrayList<>();
        users.add(simonId);
        relayGroupModel.setUsers(users);
        return relayGroupModel;
    }

    private GroupModel prepareGroupModel() {

        GroupModel groupModel = new GroupModel();

        GroupNameModel groupNameModel = new GroupNameModel();
        groupNameModel.setGroupName("Test Group");
        groupNameModel.setGroupNameChanged(false);

        String groupNameJson = gsonBuilder.getGroupNameModelJson(groupNameModel);

        groupModel.setGroupName(groupNameJson);
        groupModel.setGroupId(UUID.randomUUID().toString());

        myUserId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);

        String myUserName = SharedPref.read(Constants.preferenceKey.USER_NAME);
        int avatarIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);

        GroupMembersInfo myGroupMembersInfo = new GroupMembersInfo()
                .setMemberId(myUserId)
                .setUserName(myUserName)
                .setMemberStatus(Constants.GroupEvent.GROUP_JOINED)
                .setAvatarPicture(avatarIndex)
                .setIsAdmin(true);

        GroupMembersInfo member1 = new GroupMembersInfo()
                .setUserName("Daniel")
                .setMemberId(dainelId)
                .setAvatarPicture(2);

        GroupMembersInfo member2 = new GroupMembersInfo()
                .setUserName("Mike")
                .setMemberId(mikeId)
                .setAvatarPicture(4);

        groupModel.setAdminInfo(myUserId);

        ArrayList<GroupMembersInfo> memberList = new ArrayList<>();
        memberList.add(myGroupMembersInfo);
        memberList.add(member1);
        memberList.add(member2);

        String membersJson = gsonBuilder.getGroupMemberInfoJson(memberList);
        groupModel.setMemberInfo(membersJson);
        groupModel.setCreatedTime(System.currentTimeMillis());

        return groupModel;
    }


    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}