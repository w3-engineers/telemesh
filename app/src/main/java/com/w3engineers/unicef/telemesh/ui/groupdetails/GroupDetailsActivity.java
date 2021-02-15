package com.w3engineers.unicef.telemesh.ui.groupdetails;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.View;

import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.ItemClickListener;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityGroupDetailsBinding;
import com.w3engineers.unicef.telemesh.ui.addnewmember.AddNewMemberActivity;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.groupnameedit.GroupNameEditActivity;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.LanguageUtil;

import java.util.ArrayList;
import java.util.List;


public class GroupDetailsActivity extends TelemeshBaseActivity implements ItemClickListener<UserEntity> {

    private ActivityGroupDetailsBinding mBinding;
    private GroupDetailsViewModel mViewModel;
    private String groupId;
    private GroupDetailsAdapter mAdapter;
    private SharedPref sharedPref;
    private GroupEntity mGroupEntity;
    private String myUserId;
    private List<GroupMembersInfo> adminInfoList = new ArrayList<>();
    private boolean amIAdmin, amICreator;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_details;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public void startUI() {
        mBinding = (ActivityGroupDetailsBinding) getViewDataBinding();
        mViewModel = getViewModel();

        sharedPref = SharedPref.getSharedPref(this);

        initView();
    }

    @Override
    protected void stopUI() {

    }

    @Override
    public BaseServiceLocator a() {
        return null;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.op_back:
                finish();
                break;

            case R.id.image_view_add_member:
            case R.id.text_view_add_member:
                if (amIAdmin) {
                    Intent addMemberIntent = new Intent(this, AddNewMemberActivity.class);
                    addMemberIntent.putExtra(GroupEntity.class.getName(), groupId);
                    startActivity(addMemberIntent);
                } else {
                    Toaster.showShort(LanguageUtil.getString(R.string.only_admin_can_add_member));
                }
                break;
            case R.id.image_view_pen:
                if(amIAdmin) {
                    Intent intent = new Intent(this, GroupNameEditActivity.class);
                    intent.putExtra(GroupEntity.class.getName(), groupId);
                    intent.putExtra(GroupNameEditActivity.class.getName(), mBinding.editTextName.getText().toString());
                    startActivity(intent);
                }else{
                    Toaster.showShort(LanguageUtil.getString(R.string.only_admin_can_change_group_name));
                }
                break;
            case R.id.text_view_leave_group:
                mViewModel.groupLeaveAction(mGroupEntity);
                break;
        }
    }

    @Override
    public void onItemClick(View view, UserEntity item) {
        int id = view.getId();
        if (id == R.id.image_view_remove) {
            if (amIAdmin) {
                mViewModel.memberRemoveAction(mGroupEntity, item);
            } else {
                Toaster.showShort(LanguageUtil.getString(R.string.only_admin_can_remove_member));
            }
        } else {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra(UserEntity.class.getName(), item);
            if (myUserId.equals(item.meshId)) {
                intent.putExtra(SettingsFragment.class.getName(), true);
            }
            startActivity(intent);
        }
    }

    private void initView() {
        setClickListener(mBinding.opBack, mBinding.textViewAddMember, mBinding.imageViewPen,
                mBinding.textViewLeaveGroup, mBinding.imageViewAddMember);

        myUserId = sharedPref.read(Constants.preferenceKey.MY_USER_ID);
        mAdapter = new GroupDetailsAdapter(myUserId);
        mAdapter.setItemClickListener(this);
        mBinding.recyclerViewGroupMember.setHasFixedSize(true);
        mBinding.recyclerViewGroupMember.setAdapter(mAdapter);

        mBinding.nestedScrollView.setNestedScrollingEnabled(true);

        parseIntent();

        initGroupMembersInfoObserver();
        initGroupObserver();
    }

    private void viewControl() {
        mBinding.imageViewAddMember.setVisibility(View.GONE);
        mBinding.textViewAddMember.setVisibility(View.GONE);
        mBinding.separator3.setVisibility(View.GONE);
        if (amICreator) {
            mBinding.imageViewAddMember.setVisibility(View.VISIBLE);
            mBinding.textViewAddMember.setVisibility(View.VISIBLE);
            mBinding.separator3.setVisibility(View.VISIBLE);
        }

        mBinding.imageViewPen.setVisibility(View.GONE);
        if (amIAdmin) {
            mBinding.imageViewPen.setVisibility(View.VISIBLE);
        }
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(GroupEntity.class.getName())) {
            groupId = intent.getStringExtra(GroupEntity.class.getName());
        }
    }

    private void populateInfoInView(GroupEntity groupEntity) {
        this.mGroupEntity = groupEntity;
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();
        if (groupEntity != null) {

            GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupEntity.getGroupName());

            List<GroupMembersInfo> groupMembersInfos = gsonBuilder.getGroupMemberInfoObj(groupEntity.getMembersInfo());

            adminInfoList.clear();
            for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
                if (groupMembersInfo.isAdmin()) {
                    adminInfoList.add(groupMembersInfo);
                    if (myUserId.equals(groupMembersInfo.getMemberId())) {
                        amIAdmin = true;
                    }
                }
            }

            if (myUserId.equals(groupEntity.getAdminInfo())) {
                amICreator = true;
            }

            viewControl();
            mAdapter.submitAdminInfoList(adminInfoList, amIAdmin);

            mBinding.editTextName.setText(groupNameModel.getGroupName());
            mViewModel.startMemberObserver(groupEntity.getMembersInfo());
        } else {
            finish();
            if (ChatActivity.sInstance != null) {
                ChatActivity.sInstance.finish();
            }
        }
    }

    private void initGroupObserver() {
        if (groupId == null) finish();

        mViewModel.getLiveGroupById(groupId).observe(this, this::populateInfoInView);

        mViewModel.finishForGroupLeave.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                finish();
                if (ChatActivity.sInstance != null) {
                    ChatActivity.sInstance.finish();
                }
            }
        });
    }

    private void initGroupMembersInfoObserver() {
        mViewModel.userListsData.observe(this, userEntities -> {
            if (userEntities != null) {
                int groupMember = userEntities.size() + 1;
                userEntities.add(getMyInfo());
                mBinding.textViewParticipantsCount.setText(String.valueOf(groupMember));
                mAdapter.clear();
                mAdapter.addItem(sortMemberList(userEntities));
            }
        });
    }

    /**
     * Sort member list. Admin users will be the top of the list
     *
     * @param memberList list of {@link UserEntity}
     * @return
     */
    private List<UserEntity> sortMemberList(List<UserEntity> memberList) {
        List<UserEntity> sortedMemberList = new ArrayList<>();
        for (UserEntity entity : memberList) {
            for (GroupMembersInfo groupMembersInfo : mAdapter.getAdminInfoList()) {
                if (groupMembersInfo.getMemberId().equals(entity.getMeshId())) {
                    sortedMemberList.add(0, entity);
                } else {
                    sortedMemberList.add(entity);
                }
            }
        }
        return sortedMemberList;
    }

    private UserEntity getMyInfo() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(sharedPref.read(Constants.preferenceKey.USER_NAME));
        userEntity.avatarIndex = sharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
        userEntity.meshId = sharedPref.read(Constants.preferenceKey.MY_USER_ID);
        return userEntity;
    }

    private GroupDetailsViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getGroupDetailsViewModel(getApplication());
            }
        }).get(GroupDetailsViewModel.class);
    }

}