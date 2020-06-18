package com.w3engineers.unicef.telemesh.ui.groupdetails;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityGroupDetailsBinding;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.List;


public class GroupDetailsActivity extends TelemeshBaseActivity {

    private ActivityGroupDetailsBinding mBinding;
    private GroupDetailsViewModel mViewModel;
    private String groupId;

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
                //Todo open group name edit page
                break;
            case R.id.text_view_leave_group:
                //Todo leave group operation perform
                break;
        }
    }

    private void initView() {
        setClickListener(mBinding.opBack, mBinding.textViewAddMember, mBinding.imageViewPen,
                mBinding.textViewLeaveGroup);

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
                mBinding.textViewParticipantsCount.setText(String.valueOf(userEntities.size()));
            }

        });
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