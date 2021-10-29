package com.w3engineers.unicef.telemesh.ui.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
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
import com.w3engineers.unicef.util.base.ui.BaseFragment;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.DexterPermissionHelper;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.StorageUtil;
import com.w3engineers.unicef.util.helper.ViperUtil;

import java.io.ByteArrayOutputStream;

public class SettingsFragment extends BaseFragment implements View.OnClickListener, DexterPermissionHelper.PermissionCallback {

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


        if (!CommonUtil.isWalletBackupDone) {
            mBinding.imageViewDot.setVisibility(View.VISIBLE);
        }

        initView();

        showInAppUpdateButton();
    }

    @Override
    public void onClick(@NonNull View view) {

        int id = view.getId();

        switch (id) {
            case R.id.layout_view_profile:
                // Showing user profile
                UserEntity userEntity = new UserEntity();
                userEntity.setUserName(SharedPref.read(Constants.preferenceKey.USER_NAME));
                userEntity.setUserLastName(SharedPref.read(Constants.preferenceKey.LAST_NAME));
                userEntity.avatarIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
                userEntity.meshId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    DexterPermissionHelper.getInstance().requestForPermission(getActivity(), this,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION);
                } else {
                    shareAppOperation();
                }

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
                meshLogAction();
//                MeshLogManager.openActivity(getActivity());
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

            case R.id.layout_backup_wallet:
                walletBackUpAction();
                break;

            default:
                break;

        }

        super.onClick(view);

    }


    public void removeBadgeIcon() {
        mBinding.imageViewDot.setVisibility(View.GONE);
    }

    private void appUpdateAction() {
        if (MainActivity.getInstance() != null) {
            String url = SharedPref.read(Constants.preferenceKey.UPDATE_APP_URL).replace(InAppUpdate.MAIN_APK, "");
            if (hasEnoughStorage()) {
                AppInstaller.downloadApkFile(url, MainActivity.getInstance(), DataManager.on().getNetwork());
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

    private void meshLogAction() {
        if (isMeshInit()) {
            DataManager.on().openMeshLogUI();
        }
    }

    private void shareAppOperation() {
        if (hasEnoughStorage()) {
            settingsViewModel.startInAppShareProcess();
        }
    }

    private void walletBackUpAction() {
        if (isMeshInit()) {
            DataManager.on().launchActivity(ViperUtil.WALLET_BACKUP_ACTIVITY);
        }
    }

    private byte[] getImageByteArray() {

        int imageIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
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
            Toast.makeText(mActivity, LanguageUtil.getString(R.string.mesh_not_initiated), Toast.LENGTH_SHORT).show();
        }
        return isMeshInit;
    }

    private boolean hasEnoughStorage() {
        boolean hasEnoughStorage = false;
        if (StorageUtil.getFreeMemory() > Constants.MINIMUM_SPACE) {
            hasEnoughStorage = true;
        } else {
            Toast.makeText(mActivity, LanguageUtil.getString(R.string.phone_storage_not_enough), Toast.LENGTH_SHORT).show();
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
//        if (NetworkMonitor.isOnline()) {
        long version = SharedPref.readLong(Constants.preferenceKey.UPDATE_APP_VERSION);
        if (version > InAppUpdate.getInstance(mActivity).getAppVersion().getVersionCode()) {
            mBinding.layoutAppUpdate.setVisibility(View.VISIBLE);
            mBinding.aboutUsBottom.setVisibility(View.VISIBLE);
        }
//        }
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
        mBinding.layoutBackupWallet.setOnClickListener(this);

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

    @Override
    public void onPermissionGranted() {
        shareAppOperation();
    }
}
