package com.w3engineers.unicef.telemesh.ui.addnewmember;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.ItemClickListener;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityAddNewMemberBinding;
import com.w3engineers.unicef.telemesh.ui.groupcreate.SelectedUserAdapter;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.LanguageUtil;

import java.util.ArrayList;
import java.util.List;

public class AddNewMemberActivity extends TelemeshBaseActivity implements
        AddNewMemberAdapter.ItemChangeListener, ItemClickListener<UserEntity> {

    private ActivityAddNewMemberBinding mBinding;
    private AddNewMemberViewModel mViewModel;
    private AddNewMemberAdapter mMemberAdapter;
    private SelectedUserAdapter mSelectedUserAdapter;

    private String mGroupId;

    private GroupEntity mGroupEntity;
    private List<String> mGroupMemberList;

    @Nullable
    public List<UserEntity> userEntityList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_new_member;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    public void startUI() {
        mBinding = (ActivityAddNewMemberBinding) getViewDataBinding();
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
        if (view.getId() == R.id.button_go) {
            mViewModel.addMembersInGroup(mGroupEntity, mMemberAdapter.getSelectedUserList());
        }
    }

    @Override
    public void onGetChangedItem(boolean isAdd, UserEntity userEntity) {
        if (!mMemberAdapter.getSelectedUserList().isEmpty()) {
            mBinding.buttonGo.show();
            mBinding.cardViewSelectedItem.setVisibility(View.VISIBLE);
        } else {
            mBinding.buttonGo.hide();
            clearSelectedUserAdapter();
        }

        if (isAdd) {
            mSelectedUserAdapter.addItem(userEntity);
        } else {
            mSelectedUserAdapter.removeItem(userEntity);
        }
    }

    @Override
    public void onItemClick(View view, UserEntity item) {
        if (view.getId() == R.id.button_remove) {
            mSelectedUserAdapter.removeItem(item);

            mMemberAdapter.deselectUser(item);

            if (mMemberAdapter.getSelectedUserList().isEmpty()) {
                mBinding.cardViewSelectedItem.setVisibility(View.GONE);
            }
        }
    }

    private void initView() {

        setClickListener(mBinding.buttonGo);

        setTitle(LanguageUtil.getString(R.string.add_new_member));

        mBinding.recyclerViewUser.setHasFixedSize(true);
        mMemberAdapter = new AddNewMemberAdapter(this, this);
        mBinding.recyclerViewUser.setAdapter(mMemberAdapter);

        mSelectedUserAdapter = new SelectedUserAdapter();
        mSelectedUserAdapter.setItemClickListener(this);
        mBinding.recyclerViewSelectedUser.setHasFixedSize(true);
        mBinding.recyclerViewSelectedUser.setAdapter(mSelectedUserAdapter);

        parseIntent();

        groupDataObserver();

        userDataOperation();
    }

    private void groupDataObserver() {
        if (mGroupId == null) {
            finish();
            return;
        }

        mViewModel.getLiveGroupById(mGroupId).observe(this, groupEntity -> {
            if (groupEntity != null) {
                mGroupEntity = groupEntity;
                generateMemberList(GsonBuilder.getInstance()
                        .getGroupMemberInfoObj(groupEntity.getMembersInfo()));
                mViewModel.startUserObserver(mGroupMemberList);
            }
        });

        mViewModel.groupUserList.observe(this, groupEntity -> {
            if (groupEntity != null) {
                finish();
            }
        });

    }

    private void userDataOperation() {

        mViewModel.nearbyUsers.observe(this, userEntities -> {
            if (userEntities != null) {
                mMemberAdapter.submitList(userEntities);
                userEntityList = userEntities;

                if (userEntityList != null && userEntityList.size() > 0) {
                    //Todo we can show empty page
                } else {
                    //Todo we can hide empty page
                }
            }
        });
    }

    private void generateMemberList(List<GroupMembersInfo> groupAdminInfoList) {
        mGroupMemberList = new ArrayList<>();
        for (GroupMembersInfo info : groupAdminInfoList) {
            mGroupMemberList.add(info.getMemberId());
        }
    }

    private void clearSelectedUserAdapter() {
        mBinding.cardViewSelectedItem.setVisibility(View.GONE);
        mSelectedUserAdapter.clear();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(GroupEntity.class.getName())) {
            mGroupId = intent.getStringExtra(GroupEntity.class.getName());
        }
    }

    private AddNewMemberViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getAddNewMemberViewModel(getApplication());
            }
        }).get(AddNewMemberViewModel.class);
    }

}