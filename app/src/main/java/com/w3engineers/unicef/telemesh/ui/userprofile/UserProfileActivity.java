package com.w3engineers.unicef.telemesh.ui.userprofile;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityUserProfileBinding;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;

public class UserProfileActivity extends TelemeshBaseActivity {

    private final String LABEL = "Address";

    private ActivityUserProfileBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_profile;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected BaseServiceLocator a() {
        return ServiceLocator.getInstance();
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
        mBinding = (ActivityUserProfileBinding) getViewDataBinding();
        UserEntity userEntity = getIntent().getParcelableExtra(UserEntity.class.getName());
        boolean isMyProfile = getIntent().getBooleanExtra(SettingsFragment.class.getName(), false);
        mBinding.setUserEntity(userEntity);

        setClickListener(mBinding.opBack, mBinding.imageViewIdCopy, mBinding.buttonExportProfile);

        if (isMyProfile) {
            SharedPref sharedPref = SharedPref.getSharedPref(this);
            String companyId = sharedPref.read(Constants.preferenceKey.COMPANY_ID);
            String companyName = sharedPref.read(Constants.preferenceKey.COMPANY_NAME);

           /* if (!TextUtils.isEmpty(companyId)) {
                mBinding.userId.setText(getResources().getString(R.string.id) + ": " + companyId);
                mBinding.userCompany.setText(getCompanyName(companyName));
                mBinding.icValid.setVisibility(View.VISIBLE);
            } else {
                mBinding.icValid.setVisibility(View.GONE);
            }*/

            mBinding.buttonExportProfile.setVisibility(View.INVISIBLE);
        } else {
            mBinding.buttonExportProfile.setVisibility(View.GONE);
        }

        Bitmap qrImage = getWalletQr();
        if (qrImage != null) {
            mBinding.imageViewQr.setImageBitmap(qrImage);
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
            case R.id.image_view_id_copy:
                copyEthAddress();
                break;
            case R.id.button_export_profile:
                // Todo Export profile option
                break;
        }
    }

    private void copyEthAddress() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(LABEL, mBinding.userId.getText().toString());
        clipboard.setPrimaryClip(clip);

        Toaster.showShort(getResources().getString(R.string.copied));
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
        SharedPref sharedPref = SharedPref.getSharedPref(this);
        String bitmapString = sharedPref.read(Constants.preferenceKey.MY_WALLET_IMAGE);
        byte[] encodeByte = Base64.decode(bitmapString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

    /*@Override
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
    }*/
}
