package com.w3engineers.unicef.telemesh.ui.userprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityUserProfileBinding;
import com.w3engineers.unicef.telemesh.ui.editprofile.EditProfileActivity;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.util.base.ui.BaseServiceLocator;
import com.w3engineers.unicef.util.base.ui.TelemeshBaseActivity;
import com.w3engineers.unicef.util.helper.LanguageUtil;

public class UserProfileActivity extends TelemeshBaseActivity {

    private final String LABEL = "Address";

    private ActivityUserProfileBinding mBinding;
    private boolean isMyProfile;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_profile;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public BaseServiceLocator a() {
        return ServiceLocator.getInstance();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void startUI() {
        super.startUI();
        setTitle(LanguageUtil.getString(R.string.activity_view_profile));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //    private ServiceLocator serviceLocator;

        UserProfileViewModel userProfileViewModel = getViewModel();
        mBinding = (ActivityUserProfileBinding) getViewDataBinding();
        initAllText();
        UserEntity userEntity = getIntent().getParcelableExtra(UserEntity.class.getName());
        isMyProfile = getIntent().getBooleanExtra(SettingsFragment.class.getName(), false);
        mBinding.setUserEntity(userEntity);

        updateImageNameField(userEntity.getUserName(), userEntity.getUserLastName());

        setClickListener(mBinding.opBack, mBinding.imageViewIdCopy, mBinding.buttonExportProfile, mBinding.textViewEdit);

        if (isMyProfile) {

            mBinding.buttonExportProfile.setVisibility(View.INVISIBLE);
            mBinding.textViewEdit.setVisibility(View.VISIBLE);
        } else {
            mBinding.buttonExportProfile.setVisibility(View.GONE);
            mBinding.textViewEdit.setVisibility(View.GONE);

            String title = String.format(LanguageUtil.getString(R.string.other_profile), userEntity.userName);
            mBinding.textViewTitle.setText(title);
        }

        Bitmap qrImage = getWalletQr();
        if (qrImage != null) {
            mBinding.imageViewQr.setImageBitmap(qrImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isMyProfile) {
            UserEntity userEntity = new UserEntity();
            userEntity.userName = SharedPref.read(Constants.preferenceKey.USER_NAME);
            userEntity.setUserLastName(SharedPref.read(Constants.preferenceKey.LAST_NAME));
            userEntity.avatarIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
            userEntity.meshId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);
            mBinding.setUserEntity(userEntity);

            updateImageNameField(userEntity.getUserName(), userEntity.getUserLastName());
        }
    }

  /*  private SpannableString getCompanyName(String name) {
        String companyName = String.format(getResources().getString(R.string.company_org), name);

        SpannableString spannableString = new SpannableString(companyName);

        int startIndex = companyName.length() - name.length();

        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                startIndex, companyName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }*/

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        int id = view.getId();
        switch (id) {
            case R.id.op_back:
                finish();
                break;
            case R.id.image_view_id_copy:
                copyEthAddress();
                break;
            /*case R.id.button_export_profile:
                // Todo Export profile option
                break;*/
            case R.id.text_view_edit:
                gotoEditPage();
                break;
        }
    }

    private void copyEthAddress() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(LABEL, mBinding.userId.getText().toString());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, LanguageUtil.getString(R.string.copied), Toast.LENGTH_SHORT).show();
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

    private Bitmap getWalletQr() {
        String bitmapString = SharedPref.read(Constants.preferenceKey.MY_WALLET_IMAGE);
        byte[] encodeByte = Base64.decode(bitmapString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

    private void gotoEditPage() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(SharedPref.read(Constants.preferenceKey.USER_NAME));
        userEntity.setUserLastName(SharedPref.read(Constants.preferenceKey.LAST_NAME));
        userEntity.avatarIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
        userEntity.meshId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra(UserEntity.class.getName(), userEntity);
        startActivity(intent);
    }

    private void initAllText() {
        mBinding.textViewEdit.setText(LanguageUtil.getString(R.string.edit));
        mBinding.textViewTitle.setText(LanguageUtil.getString(R.string.activity_view_profile));
    }

    private void updateImageNameField(String firstName, String lastName) {

        String finalText = "";


        if (!TextUtils.isEmpty(firstName)) {

            finalText = String.valueOf(firstName.charAt(0));
        }
        if (!TextUtils.isEmpty(lastName)) {

            finalText += String.valueOf(lastName.charAt(0));
        }

        mBinding.textViewImageName.setText(finalText);
    }

    /*@Override
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
    }*/
}
