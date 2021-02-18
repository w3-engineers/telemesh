package com.w3engineers.unicef.telemesh.ui.splashscreen;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.MeshDataSource;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.data.provider.ViewModelProviderFactory;
import com.w3engineers.unicef.telemesh.databinding.ActivitySplashBinding;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.profilechoice.ProfileChoiceActivity;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.walleter.wallet.WalletService;

import javax.inject.Inject;

import timber.log.Timber;

public class SplashActivity extends BaseActivity {


    SplashViewModel splashViewModel;

    @Inject
    ViewModelProviderFactory providerFactory;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }


    @Override
    protected void startUI() {

        //splashViewModel = getViewModel();
        splashViewModel = ViewModelProviders.of(this, providerFactory).get(SplashViewModel.class);
        ActivitySplashBinding activitySplashBinding = (ActivitySplashBinding) getViewDataBinding();

        activitySplashBinding.setSplashViewModel(splashViewModel);

        MeshDataSource.isPrepared = false;

        splashViewModel.getUserRegistrationStatus();
        ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer_container);
        shimmerFrameLayout.startShimmer();

        if (CommonUtil.isEmulator()) {
            WalletService.getInstance(this).deleteExistsWallet();
        }


        splashViewModel.getIsUserRegistered().observe(this, aBoolean -> {
            Intent intent;
            if (aBoolean != null && aBoolean) {
                // Go to contact page
                Timber.d("User already created. Go next page");
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                Timber.e("User not created. Go User create page");
                intent = new Intent(SplashActivity.this, ProfileChoiceActivity.class);
            }
            startActivity(intent);
            finish();
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
}
