package com.w3engineers.unicef.telemesh.ui.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentSettingsNewBinding;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.ui.mywallet.MyWalletActivity;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private Context mActivity;
    private SettingsViewModel settingsViewModel;
//    private ServiceLocator serviceLocator;
//    private String selectedLanguage, selectedLanguageDisplay;

    public SettingsFragment() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings_new;
    }

    @Override
    protected void startUI() {

        mActivity = getContext();
        settingsViewModel = getViewModel();

        //    private FragmentSettingsBinding mBinding;
        FragmentSettingsNewBinding mBinding = (FragmentSettingsNewBinding) getViewDataBinding();

        mBinding.setSettingsVM(settingsViewModel);

        mBinding.layoutViewProfile.setOnClickListener(this);
        mBinding.layoutChooseLanguage.setOnClickListener(this);
        mBinding.layoutShareApp.setOnClickListener(this);
        mBinding.layoutAboutUs.setOnClickListener(this);
        mBinding.layoutPrivacyPolicy.setOnClickListener(this);
        mBinding.layoutOpenWallet.setOnClickListener(this);

        // Add sample mesh token 10000

        if (getContext() != null) {
            int token = SharedPref.getSharedPref(getContext()).readInt("cr_token");
            int spentToken = SharedPref.getSharedPref(getContext()).readInt("sp_token");

            if (token == 0 && spentToken == 0) {
                SharedPref.getSharedPref(getContext()).write("cr_token", 10000);
                SharedPref.getSharedPref(getContext()).write("sp_token", 0);
            }
        }
    }

    @Override
    public void onClick(@NonNull View view) {

        int id = view.getId();

        switch (id) {
            case R.id.layout_view_profile:
                // Showing user profile
                SharedPref sharedPref = SharedPref.getSharedPref(mActivity);
                UserEntity userEntity = new UserEntity();
                userEntity.setUserFirstName(sharedPref.read(Constants.preferenceKey.FIRST_NAME));
                userEntity.setUserLastName(sharedPref.read(Constants.preferenceKey.LAST_NAME));
                userEntity.avatarIndex = sharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
                Intent intent = new Intent(mActivity, UserProfileActivity.class);
                intent.putExtra(UserEntity.class.getName(), userEntity);
                startActivity(intent);
                break;
            case R.id.layout_choose_language:
                // Go system settings for change language
                showLanguageChangeDialog();
                break;
            case R.id.layout_share_app:
                // Open Share app page
                break;
            case R.id.layout_about_us:
                // Show about us
                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
            case R.id.layout_privacy_policy:
                // Show privacy policy
                break;

            case R.id.layout_open_wallet:
                startActivity(new Intent(mActivity, MyWalletActivity.class));
                break;
            default:
                break;

        }

        super.onClick(view);

    }

    private void showLanguageChangeDialog() {

        String[] languageList = mActivity.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = mActivity.getResources().getStringArray(R.array.language_code_list);
        String currentLanguage =  settingsViewModel.getAppLanguage();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.alert_language_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        RadioGroup languageGroup = dialogView.findViewById(R.id.radio_group_language);

        for (int i = 0; i < languageGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) languageGroup.getChildAt(i);
            radioButton.setText(languageList[i]);
            if (currentLanguage.equals(languageList[i])){
                radioButton.setChecked(true);
            }
        }

        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_english:

                    settingsViewModel.setLocale(languageCodeList[0], languageList[0]);
                    alertDialog.dismiss();
                    if (getActivity() != null){
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                    break;
                case R.id.radio_bangla:

                    settingsViewModel.setLocale(languageCodeList[1], languageList[1]);
                    alertDialog.dismiss();
                    if (getActivity() != null){
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                    break;
            }
        });

        alertDialog.show();
    }

    private SettingsViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//                serviceLocator = ServiceLocator.getInstance();
                return (T) ServiceLocator.getInstance().getSettingsViewModel(getActivity().getApplication());
            }
        }).get(SettingsViewModel.class);
    }
}
