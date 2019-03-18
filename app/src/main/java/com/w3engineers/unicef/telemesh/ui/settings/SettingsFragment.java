package com.w3engineers.unicef.telemesh.ui.settings;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentSettingsBinding;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.ui.inappshare.InAppShareActivity;
import com.w3engineers.unicef.telemesh.ui.mywallet.MyWalletActivity;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;
import com.w3engineers.unicef.util.helper.InAppShareUtil;

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
        int spentToken = SharedPref.getSharedPref(getContext()).readInt("sp_token");

        if (token == 0 && spentToken == 0) {
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
                settingsViewModel.initServerProcess();
                openDialog();
//                startActivity(new Intent(getActivity(), InAppShareActivity.class));
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
        String currentLanguage = settingsViewModel.getAppLanguage();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
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
                        startActivity(getActivity().getIntent());
                    }
                    break;
                case R.id.radio_bangla:

                    settingsViewModel.setLocale(languageCodeList[1], languageList[1]);
                    alertDialog.dismiss();
                    if (getActivity() != null) {
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
                serviceLocator = ServiceLocator.getInstance();
                return (T) serviceLocator.getSettingsViewModel(getActivity().getApplication());
            }
        }).get(SettingsViewModel.class);
    }

    private void openDialog() {

        View layoutView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_wifi_share,
                null, false);

        DialogPlus dialog = DialogPlus.newDialog(getActivity())
                .setContentHolder(new AlertWifiShareViewHolderHolder(layoutView))
                .setContentBackgroundResource(android.R.color.transparent)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnClickListener((dialog1, view) -> {
                    switch (view.getId()) {
                        case R.id.share_ok:
                            startActivity(new Intent(getActivity(), InAppShareActivity.class));
                            dialog1.dismiss();
                            break;
                    }
                }).create();
        dialog.show();
    }

    private class AlertWifiShareViewHolderHolder extends ViewHolder {
        private TextView shareWifiPass;
        private ImageView imageView;

        public AlertWifiShareViewHolderHolder(View view) {
            super(view);
            shareWifiPass = view.findViewById(R.id.share_wifi_id_pass);
            imageView = view.findViewById(R.id.wifi_qr);

            String wifiName = InAppShareUtil.getInstance().getWifiName();
            String wifiPass = "m3sht3st";

            String wifiInfo = String.format(getString(R.string.hotspot_id_pass), wifiName, wifiPass);
            String QrText = "WIFI:T:WPA;P:\"" + wifiPass + "\";S:" + wifiName + ";";

            shareWifiPass.setText(wifiInfo);
            qrGenerator(QrText, imageView);
        }
    }

    private void qrGenerator(String Value, ImageView imageView) {
        try {
            Context context = imageView.getContext();

            BitMatrix bitMatrix = new MultiFormatWriter().encode(Value, BarcodeFormat.DATA_MATRIX.QR_CODE,
                    150, 150, null
            );

            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;

                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            context.getResources().getColor(R.color.colorPrimaryDark) :
                            context.getResources().getColor(R.color.white);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, 150, 0, 0, bitMatrixWidth, bitMatrixHeight);

            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
