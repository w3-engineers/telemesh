package com.w3engineers.unicef.telemesh.ui.settings;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentSettingsBinding;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.ui.mywallet.MyWalletActivity;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;

import org.apache.commons.lang3.ArrayUtils;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private FragmentSettingsBinding mBinding;
    private Context mActivity;
    private SettingsViewModel settingsViewModel;
    private ServiceLocator serviceLocator;
    private String selectedLanguage, selectedLanguageDisplay;

    public SettingsFragment() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void startUI() {

        mActivity = getContext();
        settingsViewModel = getViewModel();

        mBinding = (FragmentSettingsBinding) getViewDataBinding();

        mBinding.setSettingsVM(settingsViewModel);

        mBinding.layoutViewProfile.setOnClickListener(this);
        mBinding.layoutChooseLanguage.setOnClickListener(this);
        mBinding.layoutShareApp.setOnClickListener(this);
        mBinding.layoutAboutUs.setOnClickListener(this);
        mBinding.layoutPrivacyPolicy.setOnClickListener(this);
        mBinding.layoutOpenWallet.setOnClickListener(this);

        // Add sample mesh token 10000

        int token = SharedPref.getSharedPref(getContext()).readInt("cr_token");
        int spentToken =  SharedPref.getSharedPref(getContext()).readInt("sp_token");

        if (token == 0 && spentToken == 0)
        {
            SharedPref.getSharedPref(getContext()).write("cr_token", 10000);
            SharedPref.getSharedPref(getContext()).write("sp_token", 0);
        }
    }

    @Override
    public void onClick(View view) {

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

        // add a radio button list
        AlertDialog dialog;
        final String[] languageList = mActivity.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        final String[] languageCodeList = mActivity.getResources().getStringArray(R.array.language_code_list);//{"en", "bn"};

        String currentLanguage =  settingsViewModel.getAppLanguage();
        selectedLanguageDisplay = currentLanguage;
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.choose_language);

        int selectedIndex = ArrayUtils.indexOf(languageList, currentLanguage);
        builder.setSingleChoiceItems(languageList, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item

                if (!currentLanguage.equals(languageList[which])) {
                    settingsViewModel.setLocale(languageCodeList[which], languageList[which]);
                    if (getActivity() != null){
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                }else{
                    dialog.dismiss();
                }
            }
        });

        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    private SettingsViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                serviceLocator = ServiceLocator.getInstance();
                return (T) serviceLocator.getSettingsViewModel(getActivity().getApplication());
            }
        }).get(SettingsViewModel.class);
    }
}
