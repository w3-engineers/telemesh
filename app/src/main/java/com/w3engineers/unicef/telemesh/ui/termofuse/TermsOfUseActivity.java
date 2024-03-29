package com.w3engineers.unicef.telemesh.ui.termofuse;


import android.app.Activity;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.CommonUtil;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityTermsOfUseBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.selectaccount.SelectAccountActivity;
import com.w3engineers.unicef.util.base.ui.BaseActivity;

public class TermsOfUseActivity extends BaseActivity {

    private ActivityTermsOfUseBinding mBinding;
    private TermsOfUseViewModel mViewModel;
    public static TermsOfUseActivity instance;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    public static final int REQUEST_XIAOMI_PERMISSION = 109;

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_terms_of_use;
    }

    ActivityResultLauncher<Intent> permissionActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    checkPermissionAndGoToNext();
                }
            });

    @Override
    public void startUI() {
        mBinding = (ActivityTermsOfUseBinding) getViewDataBinding();
        setTitle(getResources().getString(R.string.terms_of_use_details));
        instance = this;
        mViewModel = getViewModel();

        initView();

       mViewModel.getWalletPrepareLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isOldAccount) {
                if (isOldAccount) {
                    Intent intent = new Intent(TermsOfUseActivity.this, CreateUserActivity.class);
                    intent.putExtra(Constants.IntentKeys.WALLET_EXISTS, true);
                    startActivity(intent);
                    finish();
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
        if (view.getId() == R.id.button_next) {
            if (isPermissionNeeded()) {
                showPermissionGifForXiaomi();
            } else {
                checkPermissionAndGoToNext();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_XIAOMI_PERMISSION) {
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
            // This is for test class. Because we not giving support to run service app in emulator
            if (CommonUtil.isEmulator()) {
                triggerServiceConnectionAction();
                startActivity(new Intent(this, SelectAccountActivity.class));
                finish();
            } else {
                triggerServiceConnectionAction();
            }
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
                intentAppDetailsSettings();
            }
        }
    }

    public void intentAppDetailsSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        mBinding.webViewTerms.loadUrl("file:///android_asset/terms_of_use.html");
        mBinding.buttonNext.setOnClickListener(this);

        mBinding.checkBoxTermsOfUse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mBinding.buttonNext.setEnabled(true);
                mBinding.buttonNext.setBackgroundResource(R.drawable.ractangular_gradient);
                mBinding.buttonNext.setTextColor(getResources().getColor(R.color.white));
            } else {
                mBinding.buttonNext.setEnabled(false);
                mBinding.buttonNext.setBackgroundResource(R.drawable.ractangular_white);
                mBinding.buttonNext.setTextColor(getResources().getColor(R.color.new_user_button_color));
            }
        });
    }

    private void triggerServiceConnectionAction() {
        SharedPref.write(Constants.preferenceKey.APP_POLICY_CHECKED, true);
        ServiceLocator.getInstance().startTelemeshService();
    }

    private TermsOfUseViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getTermsOfViewModel();
            }
        }).get(TermsOfUseViewModel.class);
    }

    public void showPermissionGifForXiaomi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_xiaomi_permission, null, false);

        ImageView imageView = view.findViewById(R.id.gif_image_view);
        Button buttonOk = view.findViewById(R.id.button_ok);

        Glide.with(this).asGif().load("file:///android_asset/xiaomi_permission.gif").into(imageView);
        builder.setView(view);

        AlertDialog d = builder.create();

        buttonOk.setOnClickListener(v -> {
            buttonClickListener(d);

        });


        d.show();
    }

    public void buttonClickListener(AlertDialog dialog){
        SharedPref.write(Constant.PreferenceKeys.IS_SETTINGS_PERMISSION_DONE, true);

        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", getPackageName());
        try {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            permissionActivityResultLauncher.launch(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean isPermissionNeeded() {
        String manufacturer = android.os.Build.MANUFACTURER;
        try {

            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                return !SharedPref.readBoolean(Constant.PreferenceKeys.IS_SETTINGS_PERMISSION_DONE);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
