package com.w3engineers.unicef.telemesh.ui.userprofile;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.ui.base.rm.RmBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityUserProfileBinding;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;

public class UserProfileActivity extends RmBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_profile;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @SuppressLint("SetTextI18n")
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
        boolean isMyProfile = getIntent().getBooleanExtra(SettingsFragment.class.getName(), false);
        mBinding.setUserEntity(userEntity);

        setClickListener(mBinding.opBack);

        if (isMyProfile) {
            SharedPref sharedPref = SharedPref.getSharedPref(this);
            String companyId = sharedPref.read(Constants.preferenceKey.COMPANY_ID);
            String companyName = sharedPref.read(Constants.preferenceKey.COMPANY_NAME);

            if (!TextUtils.isEmpty(companyId)) {
                mBinding.userId.setText(getResources().getString(R.string.id) + ": " + companyId);
                mBinding.userCompany.setText(getCompanyName(companyName));
                mBinding.icValid.setVisibility(View.VISIBLE);
            } else {
                mBinding.icValid.setVisibility(View.GONE);
            }
        }
    }

    private SpannableString getCompanyName(String name) {
        String companyName = String.format(getResources().getString(R.string.company_org), name);

        SpannableString spannableString = new SpannableString(companyName);

        int startIndex = companyName.length() - name.length();

        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                startIndex, companyName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        int id = view.getId();
        switch (id) {
            case R.id.op_back:
                finish();
                break;
        }
    }

    private UserProfileViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getUserProfileViewModel(getApplication());
            }
        }).get(UserProfileViewModel.class);
    }

    @Override
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
    }
}
