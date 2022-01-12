package com.w3engineers.unicef.telemesh.ui.selectaccount;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivitySelectAccountBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedViewModel;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashActivity;
import com.w3engineers.unicef.util.base.ui.BaseActivity;
import com.w3engineers.unicef.util.base.ui.BaseServiceLocator;
import com.w3engineers.unicef.util.base.ui.TelemeshBaseActivity;
import com.w3engineers.unicef.util.helper.ViperUtil;

/**
 * Created by Azizul Islam on 10/22/21.
 */
public class SelectAccountActivity extends BaseActivity {
    private SelectAccountViewModel selectAccountViewModel;
    private ActivitySelectAccountBinding viewBinder;

    public static SelectAccountActivity instance;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_account;
    }


    @Override
    public void startUI() {
        instance = this;
        viewBinder = (ActivitySelectAccountBinding) getViewDataBinding();
        selectAccountViewModel = getViewModel();
        setClickListener(viewBinder.buttonCreateAccount, viewBinder.buttonImportAccount);
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_create_account) {
            gotoCreateAccountPage(false);
        } else if (view.getId() == R.id.button_import_account) {
            gotoCreateAccountPage(true);
        }
    }

    public void gotoCreateAccountPage(boolean isImportWallet) {

        boolean isUserAlreadyRegistered = SharedPref.readBoolean(Constants.preferenceKey.IS_USER_REGISTERED);

        if (isUserAlreadyRegistered) {

            launchWalletPage(isImportWallet);

        } else {
            if (isImportWallet) {
                launchWalletPage(true);
            } else {
                Intent intent = new Intent(this, CreateUserActivity.class);
                intent.putExtra(Constants.IntentKeys.IMPORT_WALLET, false);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean isUserAlreadyRegistered = SharedPref.readBoolean(Constants.preferenceKey.IS_USER_REGISTERED);
        if (!isUserAlreadyRegistered) {
            super.onBackPressed();

            if (SplashActivity.instance != null) {
                SplashActivity.instance.finish();
            }
        } else {
            //Todo we can show a message
        }
    }

    @Override
    protected void stopUI() {
        super.stopUI();
    }

    private SelectAccountViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getSelectAccountViewModel();
            }
        }).get(SelectAccountViewModel.class);
    }

    public void launchWalletPage(boolean isNeedToImportWallet) {
        if (isNeedToImportWallet) {
            ServiceLocator.getInstance().launchActivity(ViperUtil.WALLET_IMPORT_ACTIVITY);
        } else {
            ServiceLocator.getInstance().launchActivity(ViperUtil.WALLET_SECURITY_ACTIVITY);
        }
    }
}
