package com.w3engineers.unicef.telemesh.ui.splashscreen;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.MeshDataSource;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivitySplashBinding;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.termofuse.TermsOfUseActivity;
import com.w3engineers.unicef.telemesh.ui.welcome.WelcomeActivity;
import com.w3engineers.unicef.util.base.ui.BaseActivity;
import com.w3engineers.unicef.util.helper.CommonUtil;

public class SplashActivity extends BaseActivity {
    public static SplashActivity instance;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }


    @Override
    protected void startUI() {
        instance = this;
        SplashViewModel splashViewModel = getViewModel();
        ActivitySplashBinding activitySplashBinding = (ActivitySplashBinding) getViewDataBinding();

        activitySplashBinding.setSplashViewModel(splashViewModel);

        if (MeshDataSource.getInstance() != null && MeshDataSource.isPrepared) {
            RmDataHelper.getInstance().prepareDataObserver();
        } else {
            MeshDataSource.isPrepared = false;
        }

        splashViewModel.getUserRegistrationStatus();

       /* ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer_container);
        shimmerFrameLayout.startShimmer();*/

        splashViewModel.getIsUserRegistered().observe(this, aBoolean -> {
            Intent intent;
            if (aBoolean != null && aBoolean) {
                // Go to contact page
                intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {

                boolean isTermsAccepted = SharedPref.readBoolean(Constants.preferenceKey.APP_POLICY_CHECKED, false);
                if (isTermsAccepted) {
                    //intent = new Intent(SplashActivity.this, ProfileChoiceActivity.class);
                    //intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    ServiceLocator.getInstance().startTelemeshService();
                } else {
                    //intent = new Intent(SplashActivity.this, ProfileChoiceActivity.class);
                    intent = new Intent(SplashActivity.this, TermsOfUseActivity.class);
                    startActivity(intent);
                    finish();
                }
            }


        });

    }

    private SplashViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getSplashViewModel(getApplication());
            }
        }).get(SplashViewModel.class);
    }

    @Override
    protected void stopUI() {
        super.stopUI();
        instance = null;
    }
}
