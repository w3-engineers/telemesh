package com.w3engineers.unicef.telemesh.ui.userprofile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_profile;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimary;
    }

    @Override
    protected void startUI() {

        setTitle(getString(R.string.activity_view_profile));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //    private ServiceLocator serviceLocator;
        UserProfileViewModel userProfileViewModel = getViewModel();
        ActivityUserProfileBinding mBinding = (ActivityUserProfileBinding) getViewDataBinding();
        UserEntity userEntity = getIntent().getParcelableExtra(UserEntity.class.getName());
        mBinding.setUserEntity(userEntity);

        //mBinding.setUserProfileModel(userProfileViewModel);
        //mBinding.imageProfile.setImageResource(userProfileViewModel.getProfileImage());

    }

    private UserProfileViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//                serviceLocator = ServiceLocator.getInstance();
                return (T) ServiceLocator.getInstance().getUserProfileViewModel(getApplication());
            }
        }).get(UserProfileViewModel.class);
    }
}
