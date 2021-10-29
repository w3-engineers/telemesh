package com.w3engineers.unicef.telemesh.ui.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityWelcomeBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.termofuse.TermsOfUseActivity;
import com.w3engineers.unicef.telemesh.ui.termofuse.TermsOfUseViewModel;
import com.w3engineers.unicef.util.base.ui.BaseActivity;

public class WelcomeActivity extends BaseActivity {

    private ActivityWelcomeBinding mBinding;
    private WelcomeViewModel mViewModel;
    public static WelcomeActivity instance;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void startUI() {
        instance = this;
        mBinding = (ActivityWelcomeBinding) getViewDataBinding();
        mViewModel = getViewModel();


        setClickListener(mBinding.buttonContinue);

        mViewModel.getWalletPrepareLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isOldAccount) {
                if (isOldAccount) {
                    Intent intent = new Intent(WelcomeActivity.this, CreateUserActivity.class);
                    intent.putExtra("wallet_exists", true);
                    startActivity(intent);
                }
            }
        });

        mViewModel.initWalletPreparationCallback();
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.button_continue) {
            checkPermissionAndGoToNext();
        }
    }

    private void checkPermissionAndGoToNext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                triggerServiceConnectionAction();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        } else {
            triggerServiceConnectionAction();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            triggerServiceConnectionAction();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                checkPermissionAndGoToNext();
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void triggerServiceConnectionAction() {
        ServiceLocator.getInstance().startTelemeshService();
    }

    private WelcomeViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getWelcomeViewModel();
            }
        }).get(WelcomeViewModel.class);
    }
}