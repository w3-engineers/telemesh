package com.w3engineers.unicef.telemesh.ui.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.local.meshlog.MeshLogManager;
import com.w3engineers.mesh.util.NetworkMonitor;
import com.w3engineers.mesh.util.lib.mesh.DataManager;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.AppInstaller;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentSettingsNewBinding;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.ui.feedback.FeedbackActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.StorageUtil;

import java.io.ByteArrayOutputStream;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private Context mActivity;
    private SettingsViewModel settingsViewModel;
    private FragmentSettingsNewBinding mBinding;

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

        mBinding = (FragmentSettingsNewBinding) getViewDataBinding();

        mBinding.setSettingsVM(settingsViewModel);

        initView();

        showInAppUpdateButton();
    }

    @Override
    public void onClick(@NonNull View view) {

        int id = view.getId();

        switch (id) {
            case R.id.layout_view_profile:
                // Showing user profile
                SharedPref sharedPref = SharedPref.getSharedPref(mActivity);
                UserEntity userEntity = new UserEntity();
                userEntity.setUserName(sharedPref.read(Constants.preferenceKey.USER_NAME));
                userEntity.avatarIndex = sharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
                userEntity.meshId = sharedPref.read(Constants.preferenceKey.MY_USER_ID);
                Intent intent = new Intent(mActivity, UserProfileActivity.class);
                intent.putExtra(UserEntity.class.getName(), userEntity);
                intent.putExtra(SettingsFragment.class.getName(), true);
                startActivity(intent);
                break;
            case R.id.layout_choose_language:
                // Go system settings for change language
                showLanguageChangeDialog();
                break;
            case R.id.layout_share_app:
                shareAppOperation();
                break;
            case R.id.layout_about_us:
                // Show about us
                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
            case R.id.layout_data_plan:
                dataPlanAction();
                break;

            case R.id.layout_open_wallet:
                walletAction();
                break;
            case R.id.layout_show_log:
//                startActivity(new Intent(mActivity, MeshLogHistoryActivity.class));
                MeshLogManager.openActivity(getActivity());
                break;
            /*case R.id.layout_diagram_map:
//                startActivity(new Intent(mActivity, ConnectivityDiagramActiviy.class));
                break;*/
            case R.id.layout_app_update:
                appUpdateAction();
                break;
            case R.id.layout_feedback:
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                break;

            /*case R.id.layout_ssid:
                showAlertForSSID();
                break;*/

            default:
                break;

        }

        super.onClick(view);

    }

    private void appUpdateAction() {
        if (MainActivity.getInstance() != null) {
            String url = SharedPref.getSharedPref(mActivity).read(Constants.preferenceKey.UPDATE_APP_URL).replace(InAppUpdate.MAIN_APK, "");
            if (hasEnoughStorage()) {
                AppInstaller.downloadApkFile(url, MainActivity.getInstance(), NetworkMonitor.getNetwork());
            }
        }
    }

    private void walletAction() {
        if (isMeshInit()) {
            DataManager.on().openWalletActivity(getImageByteArray());
        }
    }

    private void dataPlanAction() {
        if (isMeshInit()) {
            DataManager.on().openDataPlan();
        }
    }

    private void shareAppOperation() {
        if (hasEnoughStorage()) {
            settingsViewModel.startInAppShareProcess();
        }
    }

    private byte[] getImageByteArray() {

        int imageIndex = SharedPref.getSharedPref(getActivity()).readInt(Constants.preferenceKey.IMAGE_INDEX);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), TeleMeshDataHelper.getInstance().getAvatarImage(imageIndex));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private boolean isMeshInit() {
        boolean isMeshInit = false;
        if (Constants.IsMeshInit) {
            isMeshInit = true;
        } else {
            Toaster.showShort(LanguageUtil.getString(R.string.mesh_not_initiated));
        }
        return isMeshInit;
    }

    private boolean hasEnoughStorage() {
        boolean hasEnoughStorage = false;
        if (StorageUtil.getFreeMemory() > Constants.MINIMUM_SPACE) {
            hasEnoughStorage = true;
        } else {
            Toaster.showShort(LanguageUtil.getString(R.string.phone_storage_not_enough));
        }
        return hasEnoughStorage;
    }

    private void showLanguageChangeDialog() {

        String[] languageList = mActivity.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = mActivity.getResources().getStringArray(R.array.language_code_list);
        String currentLanguage = settingsViewModel.getAppLanguage();

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
            if (currentLanguage.equals(languageList[i])) {
                radioButton.setChecked(true);
            }
        }

        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_english:

                    settingsViewModel.setLocale(languageCodeList[0], languageList[0]);
                    alertDialog.dismiss();
                    if (getActivity() != null) {
                        getActivity().finish();
                        Intent intent = getActivity().getIntent();
                        intent.putExtra(MainActivity.class.getSimpleName(), true);
                        startActivity(intent);
                    }
                    break;
                case R.id.radio_bangla:

                    settingsViewModel.setLocale(languageCodeList[1], languageList[1]);
                    alertDialog.dismiss();
                    if (getActivity() != null) {
                        getActivity().finish();
                        Intent intent = getActivity().getIntent();
                        intent.putExtra(MainActivity.class.getSimpleName(), true);
                        startActivity(intent);

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
                return (T) ServiceLocator.getInstance().getSettingsViewModel(getActivity().getApplication());
            }
        }).get(SettingsViewModel.class);
    }

    private void showInAppUpdateButton() {
        mBinding.layoutAppUpdate.setVisibility(View.GONE);
        if (NetworkMonitor.isOnline()) {
            long version = SharedPref.getSharedPref(mActivity).readLong(Constants.preferenceKey.UPDATE_APP_VERSION);
            if (version > InAppUpdate.getInstance(mActivity).getAppVersion().getVersionCode()) {
                mBinding.layoutAppUpdate.setVisibility(View.VISIBLE);
                mBinding.aboutUsBottom.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initView() {
        mBinding.layoutViewProfile.setOnClickListener(this);
        mBinding.layoutChooseLanguage.setOnClickListener(this);
        mBinding.layoutShareApp.setOnClickListener(this);
        mBinding.layoutAboutUs.setOnClickListener(this);
        mBinding.layoutDataPlan.setOnClickListener(this);
        mBinding.layoutOpenWallet.setOnClickListener(this);
        mBinding.layoutShowLog.setOnClickListener(this);
        mBinding.layoutDiagramMap.setOnClickListener(this);
        mBinding.layoutAppUpdate.setOnClickListener(this);
        mBinding.layoutFeedback.setOnClickListener(this);
        mBinding.layoutSsid.setOnClickListener(this);

        mBinding.titleViewProfile.setText(LanguageUtil.getString(R.string.activity_view_profile));
        mBinding.titleViewWallet.setText(LanguageUtil.getString(R.string.settings_open_wallet));
        mBinding.titleViewDataPlan.setText(LanguageUtil.getString(R.string.data_plan));
        mBinding.titleViewShare.setText(LanguageUtil.getString(R.string.settings_share_app));
        mBinding.titleViewLanguage.setText(LanguageUtil.getString(R.string.settings_choose_language));
        mBinding.titleViewLog.setText(LanguageUtil.getString(R.string.settings_open_log));
        mBinding.titleViewAbout.setText(LanguageUtil.getString(R.string.activity_about_us));
        mBinding.titleViewFeedback.setText(LanguageUtil.getString(R.string.send_feedback));
        mBinding.titleViewAppUpdate.setText(LanguageUtil.getString(R.string.update_app));
        mBinding.titleViewSsid.setText(LanguageUtil.getString(R.string.set_ssid));
    }

    // TODO SSID_Change
    /*public void showAlertForSSID() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_change_ssid, null);
        builder.setView(customLayout);

        String networkSSID = SharedPref.getSharedPref(mActivity).read(Constants.preferenceKey.NETWORK_PREFIX);

        if (TextUtils.isEmpty(networkSSID)) {
            networkSSID = getResources().getString(R.string.def_ssid);
        }

        TextView infoText = customLayout.findViewById(R.id.info);
        EditText ssidName = customLayout.findViewById(R.id.network_name);

        ssidName.setHint(LanguageUtil.getString(R.string.set_new_network_prefix));

        infoText.setText(String.format(LanguageUtil.getString(R.string.your_current_network_prefix), networkSSID));

        // add a button
        builder.setPositiveButton(LanguageUtil.getString(R.string.change), (dialog, which) -> {

            String ssid = ssidName.getText().toString();

            if (TextUtils.isEmpty(ssid) || ssid.length() < 3) {
                Toaster.showShort(LanguageUtil.getString(R.string.minimu_ssid_character));
            } else {
                SharedPref.getSharedPref(mActivity).write(Constants.preferenceKey.NETWORK_PREFIX, ssid);

                settingsViewModel.destroyMeshService();
            }

        });

        builder.setNegativeButton(LanguageUtil.getString(R.string.cancel), (dialog, which) -> {
            dialog.dismiss();
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }*/
}
