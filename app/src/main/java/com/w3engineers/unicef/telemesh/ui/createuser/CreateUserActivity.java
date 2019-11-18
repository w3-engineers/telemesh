package com.w3engineers.unicef.telemesh.ui.createuser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityCreateUserBinding;
import com.w3engineers.unicef.telemesh.ui.chooseprofileimage.ProfileImageActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.security.SecurityActivity;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.util.List;

public class CreateUserActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCreateUserBinding mBinding;
    private int PROFILE_IMAGE_REQUEST = 1;
    public static int INITIAL_IMAGE_INDEX = -1;
    private CreateUserViewModel mViewModel;

    public static CreateUserActivity sInstance;
    private boolean isLoadAccount;

    private String mPassword;
    private String mWalletPath;

    @NonNull
    public static String IMAGE_POSITION = "image_position";


    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_user;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void startUI() {

        mBinding = (ActivityCreateUserBinding) getViewDataBinding();
        setTitle(getString(R.string.create_user));
        mViewModel = getViewModel();

        setClickListener(mBinding.imageViewBack);

        parseIntent();

        UIHelper.hideKeyboardFrom(this, mBinding.editTextName);

        mBinding.imageViewCamera.setOnClickListener(this);
        mBinding.buttonSignup.setOnClickListener(this);
        mBinding.imageProfile.setOnClickListener(this);

        mBinding.editTextName.setMaxCharacters(Constants.DefaultValue.MAXIMUM_TEXT_LIMIT);
        mBinding.editTextName.setMinCharacters(Constants.DefaultValue.MINIMUM_TEXT_LIMIT);

        mViewModel.textChangeLiveData.observe(this, this::nextButtonControl);
        mViewModel.textEditControl(mBinding.editTextName);

        sInstance = this;
    }

    private void nextButtonControl(String nameText) {
        if (mViewModel.getImageIndex() >= 0 &&
                !TextUtils.isEmpty(nameText) &&
                nameText.length() >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT) {

            mBinding.buttonSignup.setBackgroundResource(R.drawable.ractangular_gradient);
            mBinding.buttonSignup.setTextColor(getResources().getColor(R.color.white));
            mBinding.buttonSignup.setClickable(true);
        } else {

            mBinding.buttonSignup.setBackgroundResource(R.drawable.ractangular_white);
            mBinding.buttonSignup.setTextColor(getResources().getColor(R.color.new_user_button_color));
            mBinding.buttonSignup.setClickable(false);
        }
    }

    private CreateUserViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getCreateUserViewModel(getApplication());
            }
        }).get(CreateUserViewModel.class);
    }

    protected void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            goNext();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestMultiplePermissions();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> requestMultiplePermissions()).onSameThread().check();
    }


    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);

        int id = view.getId();

        switch (id) {
            case R.id.button_signup:
                if (isLoadAccount) {
                    saveData();
                } else {
                    goToPasswordPage();
                }
                break;
            case R.id.image_profile:
            case R.id.image_view_camera:
                Intent intent = new Intent(this, ProfileImageActivity.class);
                intent.putExtra(CreateUserActivity.IMAGE_POSITION, mViewModel.getImageIndex());
                startActivityForResult(intent, PROFILE_IMAGE_REQUEST);
                break;
            case R.id.image_view_back:
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == PROFILE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mViewModel.setImageIndex(data.getIntExtra(IMAGE_POSITION, INITIAL_IMAGE_INDEX));

            int id = getResources().getIdentifier(Constants.drawables.AVATAR_IMAGE + mViewModel.getImageIndex(), Constants.drawables.AVATAR_DRAWABLE_DIRECTORY, getPackageName());
            mBinding.imageProfile.setImageResource(id);

            nextButtonControl(mBinding.editTextName.getText().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sInstance = null;
    }

    private void saveData() {
        if (mViewModel.getImageIndex() != INITIAL_IMAGE_INDEX) {
            requestMultiplePermissions();
        } else {
            Toaster.showLong(getString(R.string.select_avatar));
        }
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IntentKeys.PASSWORD)) {
            isLoadAccount = true;
            mPassword = intent.getStringExtra(Constants.IntentKeys.PASSWORD);
            mWalletPath = intent.getStringExtra(Constants.IntentKeys.WALLET_PATH);
        }
    }

    protected void goNext() {
        if (mViewModel.storeData(mBinding.editTextName.getText() + "", mPassword, mWalletPath)) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void goToPasswordPage() {
        Intent intent = new Intent(CreateUserActivity.this, SecurityActivity.class);
        intent.putExtra(Constants.IntentKeys.USER_NAME, mBinding.editTextName.getText() + "");
        intent.putExtra(Constants.IntentKeys.AVATAR_INDEX, mViewModel.getImageIndex());
        startActivity(intent);
    }
}
