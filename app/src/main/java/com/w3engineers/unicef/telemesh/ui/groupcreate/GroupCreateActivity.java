package com.w3engineers.unicef.telemesh.ui.groupcreate;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityGroupCreateBinding;
import com.w3engineers.unicef.util.helper.LanguageUtil;

import java.util.List;


public class GroupCreateActivity extends TelemeshBaseActivity {

    private ActivityGroupCreateBinding mBinding;
    private GroupCreateViewModel mViewModel;
    private GroupCreateAdapter mGroupCreateAdapter;

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
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_create_group:
            case R.id.text_view_create_group:
                //Todo We have to open user selector view
                break;
            case R.id.button_go:
                List<UserEntity> userEntities = mGroupCreateAdapter.getCurrentList();
                mViewModel.createGroup(userEntities);
                //Todo we have to go create a group and goto chat page
                break;
        }
    }

    private void initView() {
        setClickListener(mBinding.imageViewCreateGroup, mBinding.textViewCreateGroup, mBinding.buttonGo);

        mBinding.recyclerViewUser.setHasFixedSize(true);
        mBinding.recyclerViewUser.setItemAnimator(null);
        mGroupCreateAdapter = new GroupCreateAdapter();
        mBinding.recyclerViewUser.setAdapter(mGroupCreateAdapter);

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
                    }
                }
            });

            mViewModel.getGetFilteredList().observe(this, userEntities -> {

                setTitle(LanguageUtil.getString(R.string.title_discoverd_fragment));
                if (userEntities != null && userEntities.size() > 0) {
                    //Todo we can hide create group title
                    mGroupCreateAdapter.submitList(userEntities);

                } else {
                    //Todo may be empty
                }
            });

            mViewModel.backUserEntity.observe(this, userEntities -> {
                userEntityList = userEntities;
            });

        }
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
}