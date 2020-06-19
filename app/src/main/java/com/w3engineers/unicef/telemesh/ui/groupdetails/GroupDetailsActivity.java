package com.w3engineers.unicef.telemesh.ui.groupdetails;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupAdminInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityGroupDetailsBinding;
import com.w3engineers.unicef.telemesh.ui.groupnameedit.GroupNameEditActivity;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.List;


public class GroupDetailsActivity extends TelemeshBaseActivity {

    private ActivityGroupDetailsBinding mBinding;
    private GroupDetailsViewModel mViewModel;
    private String groupId;
    private GroupDetailsAdapter mAdapter;

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
                //Todo open a add member page
                break;
            case R.id.image_view_pen:
                Intent intent = new Intent(this, GroupNameEditActivity.class);
                intent.putExtra(GroupEntity.class.getName(), groupId);
                intent.putExtra(GroupNameEditActivity.class.getName(), mBinding.editTextName.getText().toString());
                startActivity(intent);
                break;
            case R.id.text_view_leave_group:
                //Todo leave group operation perform
                break;
        }
    }

    private void initView() {
        setClickListener(mBinding.opBack, mBinding.textViewAddMember, mBinding.imageViewPen,
                mBinding.textViewLeaveGroup);

        mAdapter = new GroupDetailsAdapter();
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
        if (groupEntity != null) {

            GroupNameModel groupNameModel = GsonBuilder.getInstance()
                    .getGroupNameModelObj(groupEntity.getGroupName());

            List<GroupAdminInfo> adminInfo = GsonBuilder.getInstance().getGroupAdminInfoObj(groupEntity.getAdminInfo());
            mAdapter.submitAdminInfoList(adminInfo);

            mBinding.editTextName.setText(groupNameModel.getGroupName());

            initGroupMembersInfoObserver(groupEntity.getMembersInfo());
        }
    }

    private void initGroupObserver() {
        if (groupId == null) finish();

        mViewModel.getLiveGroupById(groupId).observe(this, this::populateInfoInView);
    }

    private void initGroupMembersInfoObserver(String membersInfo) {
        mViewModel.getGroupUsersById(membersInfo).observe(this, userEntities -> {
            if (userEntities != null) {
                int groupMember = userEntities.size() + 1;
                userEntities.add(getMyInfo());
                mBinding.textViewParticipantsCount.setText(String.valueOf(groupMember));
                mAdapter.clear();
                mAdapter.addItem(userEntities);
            }

        });
    }

    private UserEntity getMyInfo() {
        SharedPref sharedPref = SharedPref.getSharedPref(this);
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