package com.w3engineers.unicef.telemesh.ui.groupdetails;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.ItemClickListener;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.mesh.application.ui.util.ToastUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupAdminInfo;
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
import java.util.Comparator;
import java.util.List;


public class GroupDetailsActivity extends TelemeshBaseActivity implements ItemClickListener<UserEntity> {

    private ActivityGroupDetailsBinding mBinding;
    private GroupDetailsViewModel mViewModel;
    private String groupId;
    private GroupDetailsAdapter mAdapter;
    private SharedPref sharedPref;
    private GroupEntity mGroupEntity;
    private String myUserId;
    private List<GroupAdminInfo> adminInfoList;
    private boolean amIAdmin;

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
                Intent intent = new Intent(this, GroupNameEditActivity.class);
                intent.putExtra(GroupEntity.class.getName(), groupId);
                intent.putExtra(GroupNameEditActivity.class.getName(), mBinding.editTextName.getText().toString());
                startActivity(intent);
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
            //Todo remove user form group
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
                mBinding.textViewLeaveGroup);

        myUserId = sharedPref.read(Constants.preferenceKey.MY_USER_ID);
        mAdapter = new GroupDetailsAdapter();
        mAdapter.setItemClickListener(this);
        mBinding.recyclerViewGroupMember.setHasFixedSize(true);
        mBinding.recyclerViewGroupMember.setAdapter(mAdapter);

        mBinding.nestedScrollView.setNestedScrollingEnabled(true);

        parseIntent();

        initGroupObserver();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(GroupEntity.class.getName())) {
            groupId = intent.getStringExtra(GroupEntity.class.getName());
        }
    }

    private void populateInfoInView(GroupEntity groupEntity) {
        this.mGroupEntity = groupEntity;
        if (groupEntity != null) {

            GroupNameModel groupNameModel = GsonBuilder.getInstance()
                    .getGroupNameModelObj(groupEntity.getGroupName());

            adminInfoList = GsonBuilder.getInstance().getGroupAdminInfoObj(groupEntity.getAdminInfo());
            mAdapter.submitAdminInfoList(adminInfoList);

            mBinding.editTextName.setText(groupNameModel.getGroupName());

            updateMyAdminStatus();

            initGroupMembersInfoObserver(groupEntity.getMembersInfo());
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

    private void initGroupMembersInfoObserver(String membersInfo) {
        mViewModel.getGroupUsersById(membersInfo).observe(this, userEntities -> {
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
            for (GroupAdminInfo adminInfo : mAdapter.getAdminInfoList()) {
                if (adminInfo.getAdminStatus()
                        && adminInfo.getAdminId().equals(entity.getMeshId())) {
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

    private void updateMyAdminStatus() {
        for (GroupAdminInfo adminInfo : adminInfoList) {
            if (myUserId.equals(adminInfo.getAdminId())) {
                amIAdmin = true;
                break;
            }
        }
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