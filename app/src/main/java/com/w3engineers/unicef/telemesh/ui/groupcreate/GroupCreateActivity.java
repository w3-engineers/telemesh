package com.w3engineers.unicef.telemesh.ui.groupcreate;

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
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityGroupCreateBinding;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;

import java.util.List;


public class GroupCreateActivity extends TelemeshBaseActivity implements GroupCreateAdapter.ItemChangeListener {

    private ActivityGroupCreateBinding mBinding;
    private GroupCreateViewModel mViewModel;
    private GroupCreateAdapter mGroupCreateAdapter;
    private SelectedUserAdapter mSelectedUserAdapter;

    @Nullable
    public List<UserEntity> userEntityList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_create;
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
        mBinding = (ActivityGroupCreateBinding) getViewDataBinding();
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
    public void onBackPressed() {
        if (mGroupCreateAdapter != null) {
            if (mGroupCreateAdapter.isSelectionEnable()) {
                showOrHideGroupCreateView(false);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_create_group:
            case R.id.text_view_create_group:
                //Todo We have to open user selector view
                if (mGroupCreateAdapter != null) {
                    showOrHideGroupCreateView(true);
                }
                //Todo check user exists or not and show message
                break;
            case R.id.button_go:
                List<UserEntity> userEntities = mGroupCreateAdapter.getSelectedUserList();
                if (userEntities.size() == 1) {
                    openUserMessage(mGroupCreateAdapter.getSelectedUserList().get(0));
                } else {
                    mViewModel.createGroup(userEntities);
                }

                break;
        }
    }

    @Override
    public void onGetChangedItem(boolean isAdd, UserEntity userEntity) {
        // May be isAdd variable not need but in future we need it


        if (mGroupCreateAdapter != null && !mGroupCreateAdapter.getSelectedUserList().isEmpty()) {
            mBinding.buttonGo.show();
            mBinding.cardViewSelectedItem.setVisibility(View.VISIBLE);
            setTitle("New Group");
        } else {
            mBinding.buttonGo.hide();
            clearSelectedUserAdapter();
            setTitle("New Chat");
        }

        if (isAdd) {
            mSelectedUserAdapter.addItem(userEntity);
        } else {
            mSelectedUserAdapter.removeItem(userEntity);
        }
    }

    private void initView() {
        setClickListener(mBinding.imageViewCreateGroup, mBinding.textViewCreateGroup, mBinding.buttonGo);

        mBinding.recyclerViewUser.setHasFixedSize(true);
        mBinding.recyclerViewUser.setItemAnimator(null);
        mGroupCreateAdapter = new GroupCreateAdapter(this, this);
        mBinding.recyclerViewUser.setAdapter(mGroupCreateAdapter);

        mSelectedUserAdapter = new SelectedUserAdapter();
        mBinding.recyclerViewSelectedUser.setHasFixedSize(true);
        mBinding.recyclerViewSelectedUser.setAdapter(mSelectedUserAdapter);

        userDataOperation();
    }


    private void userDataOperation() {

        if (mViewModel != null) {

            mViewModel.startUserObserver();

            mViewModel.nearbyUsers.observe(this, userEntities -> {
                if (userEntities != null) {
                    mGroupCreateAdapter.submitList(userEntities);
                    userEntityList = userEntities;

                    if (userEntityList != null && userEntityList.size() > 0) {
                        //Todo we can hide create group title
                    } else {
                        //Todo show create group title
                    }
                }
            });

        }
    }

    private void openUserMessage(UserEntity entity) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(UserEntity.class.getName(), entity.meshId);
        startActivity(intent);
    }

    private GroupCreateViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getGroupCreateViewModel(getApplication());
            }
        }).get(GroupCreateViewModel.class);
    }

    private void showOrHideGroupCreateView(boolean isSelectionEnable) {
        mGroupCreateAdapter.setSelectionEnable(isSelectionEnable);
        mGroupCreateAdapter.notifyDataSetChanged();

        if (isSelectionEnable) {
            mBinding.imageViewCreateGroup.setVisibility(View.GONE);
            mBinding.textViewCreateGroup.setVisibility(View.GONE);
        } else {
            mBinding.imageViewCreateGroup.setVisibility(View.VISIBLE);
            mBinding.textViewCreateGroup.setVisibility(View.VISIBLE);

            mBinding.buttonGo.hide();
            clearSelectedUserAdapter();

            //Todo we have to remove hard coded string
            setTitle("New Chat");
        }

    }

    private void clearSelectedUserAdapter() {
        mBinding.cardViewSelectedItem.setVisibility(View.GONE);
        mSelectedUserAdapter.clear();
    }


}