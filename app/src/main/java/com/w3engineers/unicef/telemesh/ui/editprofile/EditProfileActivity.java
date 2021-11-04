package com.w3engineers.unicef.telemesh.ui.editprofile;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityEditProfileBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.util.base.ui.BaseServiceLocator;
import com.w3engineers.unicef.util.base.ui.TelemeshBaseActivity;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

public class EditProfileActivity extends TelemeshBaseActivity {

    private ActivityEditProfileBinding mBinding;
    private EditProfileViewModel mViewModel;

    public static int INITIAL_IMAGE_INDEX = -1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public BaseServiceLocator a() {
        return null;
    }

    @Override
    public void startUI() {
        super.startUI();
        mBinding = (ActivityEditProfileBinding) getViewDataBinding();
        mViewModel = getViewModel();

        initAllText();

        setClickListener(mBinding.imageViewBack, mBinding.buttonUpdate);

        UserEntity userEntity = getIntent().getParcelableExtra(UserEntity.class.getName());
        mBinding.setUser(userEntity);

        mViewModel.firstNameChangeLiveData.observe(this, this::nextButtonControl);
        mViewModel.firstNameEditControl(mBinding.editTextFirstName);

        mViewModel.lastNameChangeLiveData.observe(this, this::nextButtonControl);
        mViewModel.lastNameEditControl(mBinding.editTextLastName);

        mBinding.editTextFirstName.setSelection(mBinding.editTextFirstName.getText().toString().length());
        mBinding.editTextLastName.setSelection(mBinding.editTextLastName.getText().toString().length());
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);

        int id = view.getId();
        switch (id) {
            case R.id.button_update:
                goNext();
                break;
            case R.id.image_view_back:
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == Constants.RequestCodes.PROFILE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mViewModel.setImageIndex(data.getIntExtra(CreateUserActivity.IMAGE_POSITION, INITIAL_IMAGE_INDEX));


            nextButtonControl(mBinding.editTextFirstName.getText().toString());
        }
    }

    private void nextButtonControl(String nameText) {

        updateImageNameField();


        String firstName = mBinding.editTextFirstName.getText().toString();
        String lastName = mBinding.editTextLastName.getText().toString();

        if (!TextUtils.isEmpty(firstName)
                && !TextUtils.isEmpty(lastName)
                && firstName.length() >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT
                && lastName.length() >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT) {

            mBinding.buttonUpdate.setBackgroundResource(R.drawable.ractangular_gradient);
            mBinding.buttonUpdate.setTextColor(getResources().getColor(R.color.white));
        } else {

            mBinding.buttonUpdate.setBackgroundResource(R.drawable.ractangular_white);
            mBinding.buttonUpdate.setTextColor(getResources().getColor(R.color.new_user_button_color));
        }
    }

    private void updateImageNameField() {
        String firstName = mBinding.editTextFirstName.getText().toString();
        String lastName = mBinding.editTextLastName.getText().toString();

        String finalText = "";


        if (!TextUtils.isEmpty(firstName)) {

            finalText = String.valueOf(firstName.charAt(0));
        }
        if (!TextUtils.isEmpty(lastName)) {

            finalText += String.valueOf(lastName.charAt(0));
        }

        if (TextUtils.isEmpty(finalText)) {
            mBinding.textViewImageName.setVisibility(View.GONE);

            mBinding.imageProfileBackground.setVisibility(View.VISIBLE);
        } else {
            mBinding.textViewImageName.setVisibility(View.VISIBLE);

            mBinding.imageProfileBackground.setVisibility(View.INVISIBLE);
        }

        mBinding.textViewImageName.setText(finalText);
    }

    public void goNext() {
        UIHelper.hideKeyboardFrom(this, mBinding.editTextFirstName);
        UIHelper.hideKeyboardFrom(this, mBinding.editTextLastName);

        if (CommonUtil.isValidName(mBinding.editTextFirstName.getText().toString(), this, true)
                && CommonUtil.isValidName(mBinding.editTextLastName.getText().toString(), this, true)) {
            if (isNeedToUpdate()) {
                if (mViewModel.storeData(mBinding.editTextFirstName.getText() + "",
                        mBinding.editTextLastName.getText() + "")) {
                    Toast.makeText(this, LanguageUtil.getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show();
                    mViewModel.sendUserInfoToAll();
                    finish();
                }
            } else {
                finish();
            }
        }

    }

    private boolean isNeedToUpdate() {
        String oldFirstName = SharedPref.read(Constants.preferenceKey.USER_NAME);
        String oldLastName = SharedPref.read(Constants.preferenceKey.LAST_NAME);
      /*  int oldImageIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);

        int currentImageIndex = mViewModel.getImageIndex();

        if (currentImageIndex < 0) {
            currentImageIndex = oldImageIndex;
        }*/

        if (!oldFirstName.equals(mBinding.editTextFirstName.getText().toString().trim())
                || !oldLastName.equals(mBinding.editTextLastName.getText().toString().trim())) {
            return true;
        }

        return false;
    }


    private EditProfileViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getEditProfileViewModel(getApplication());
            }
        }).get(EditProfileViewModel.class);
    }

    private void initAllText() {
        mBinding.textViewCreateProfile.setText(LanguageUtil.getString(R.string.update_profile));
        mBinding.buttonUpdate.setText(LanguageUtil.getString(R.string.update));
//        mBinding.editTextName.setHint(LanguageUtil.getString(R.string.enter_first_name));
//        mBinding.nameLayout.setHint(LanguageUtil.getString(R.string.enter_first_name));
    }

}
