package com.w3engineers.unicef.telemesh.ui.addnewmember;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityAddNewMemberBinding;
import com.w3engineers.unicef.telemesh.ui.groupdetails.GroupDetailsViewModel;

import java.util.List;

public class AddNewMemberActivity extends TelemeshBaseActivity implements AddNewMemberAdapter.ItemChangeListener {

    private ActivityAddNewMemberBinding mBinding;
    private AddNewMemberViewModel mViewModel;
    private AddNewMemberAdapter mMemberAdapter;

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
    public void onGetChangedItem(boolean isAdd, UserEntity userEntity) {
        //Todo add a selected view
    }

    private void initView() {

        setClickListener(mBinding.buttonGo);

        mBinding.recyclerViewUser.setHasFixedSize(true);
        mMemberAdapter = new AddNewMemberAdapter(this, this);
        mBinding.recyclerViewUser.setAdapter(mMemberAdapter);

        userDataOperation();
    }

    private void userDataOperation() {

        if (mViewModel != null) {

            mViewModel.startUserObserver();

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