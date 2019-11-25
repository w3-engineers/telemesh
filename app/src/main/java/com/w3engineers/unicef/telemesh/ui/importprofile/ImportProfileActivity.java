package com.w3engineers.unicef.telemesh.ui.importprofile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.mesh.application.data.local.wallet.WalletService;
import com.w3engineers.mesh.application.data.local.wallet.Web3jWalletHelper;
import com.w3engineers.mesh.util.DialogUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityImportProfileBinding;
import com.w3engineers.unicef.telemesh.ui.importwallet.ImportWalletActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImportProfileActivity extends BaseActivity {

    private ActivityImportProfileBinding mBinding;
    private ImportProfileViewModel mViewModel;
    private int PICKFILE_REQUEST_CODE = 480;
    private String walletSuffixDir;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_profile;
    }


    @Override
    protected void startUI() {
        mBinding = (ActivityImportProfileBinding) getViewDataBinding();
        mViewModel = getViewModel();
        walletSuffixDir = "wallet/" + getResources().getString(com.w3engineers.mesh.R.string.app_name);
        initView();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.button_import_profile:
                if (WalletService.getInstance(this).isWalletExists()) {
                    showWarningDialog();
                } else {
                    openFileChooser();
                }
                break;
            case R.id.button_continue:
                // Scan from qr.
                // Now it is off
                startActivity(new Intent(ImportProfileActivity.this, ImportWalletActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICKFILE_REQUEST_CODE) {
            String filePath;
            Constants.WALLET_URI = data.getData();
            filePath = data.getData().getPath();

           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filePath = data.getData().getPath();
            } else {
                filePath = data.getData().getPath();
            }*/
            Log.d("UiTest", "File path: " + data.getData());

            Intent intent = new Intent(ImportProfileActivity.this, ImportWalletActivity.class);
            intent.putExtra(Constants.IntentKeys.WALLET_PATH, filePath);
            startActivity(intent);
        }
    }

    private void initView() {
        setClickListener(mBinding.imageViewBack, mBinding.buttonImportProfile, mBinding.buttonContinue);

        if (WalletService.getInstance(this).isWalletExists()) {
            mBinding.buttonContinue.setVisibility(View.VISIBLE);
            mBinding.textViewWelcome.setText(getWalletSpannableString());
            mBinding.textViewWelcome.setTypeface(mBinding.textViewWelcome.getTypeface(), Typeface.NORMAL);
            mBinding.textViewWelcome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        } else {
            mBinding.buttonContinue.setVisibility(View.INVISIBLE);
            mBinding.textViewOr.setVisibility(View.INVISIBLE);
            mBinding.textViewWelcome.setTypeface(mBinding.textViewWelcome.getTypeface(), Typeface.BOLD);
        }
    }

    private SpannableString getWalletSpannableString() {
        String walletText = getResources().getString(R.string.file_exit);
        String address = readWalletAddress();

        String walletExistMessage = String.format(walletText, address);
        SpannableString spannableString = new SpannableString(walletExistMessage);


        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.wallet_highlight_color)),
                1, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int startIndex = walletExistMessage.length() - address.length();

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.wallet_highlight_color)),
                startIndex, walletExistMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    private String readWalletAddress() {
        String filePath = Web3jWalletHelper.onInstance(this).getWalletDir(walletSuffixDir);
        File directory = new File(filePath);
        StringBuilder result = new StringBuilder();
        File[] list = directory.listFiles();
        if (list != null) {
            File walletFile = list[0];
            if (walletFile.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(walletFile);
                    char current;
                    while (fis.available() > 0) {
                        current = (char) fis.read();
                        result.append(current);
                    }
                } catch (Exception e) {
                    Log.e("WalletRead", "Error: " + e.getMessage());
                } finally {
                    if (fis != null)
                        try {
                            fis.close();
                        } catch (IOException ignored) {
                        }
                }
            }
        }

        if (TextUtils.isEmpty(result.toString())) {
            return result.toString();
        }

        try {
            JSONObject walletObj = new JSONObject(result.toString());
            result = new StringBuilder("0x" + walletObj.optString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private ImportProfileViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getImportProfileViewModel(getApplication());
            }
        }).get(ImportProfileViewModel.class);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(Constants.FILE_TYPE);
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);

    }

    private void showWarningDialog() {
        DialogUtil.showConfirmationDialog(this,
                getResources().getString(R.string.warning),
                getResources().getString(R.string.wallet_lost_warning),
                getResources().getString(R.string.cancel),
                getResources().getString(R.string.ok), new DialogUtil.DialogButtonListener() {
                    @Override
                    public void onClickPositive() {
                        openFileChooser();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onClickNegative() {

                    }
                });
    }
}
