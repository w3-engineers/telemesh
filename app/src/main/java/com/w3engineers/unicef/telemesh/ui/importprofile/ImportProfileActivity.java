package com.w3engineers.unicef.telemesh.ui.importprofile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityImportProfileBinding;
import com.w3engineers.unicef.telemesh.ui.importwallet.ImportWalletActivity;

public class ImportProfileActivity extends BaseActivity {

    private ActivityImportProfileBinding mBinding;
    private ImportProfileViewModel mViewModel;
    private int PICKFILE_REQUEST_CODE = 480;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_profile;
    }


    @Override
    protected void startUI() {
        mBinding = (ActivityImportProfileBinding) getViewDataBinding();
        mViewModel = getViewModel();

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
                openFileChooser();
                break;
            case R.id.button_scan_profile:
                // Scan from qr.
                // Now it is off
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICKFILE_REQUEST_CODE) {
            String filePath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filePath = data.getData().getPath();
            } else {
                filePath = data.getData().getPath();
            }
            Log.d("UiTest", "File path: " + data.getData());

            Intent intent = new Intent(ImportProfileActivity.this, ImportWalletActivity.class);
            intent.putExtra(Constants.IntentKeys.WALLET_PATH, filePath);
            startActivity(intent);
        }
    }

    private void initView() {
        setClickListener(mBinding.imageViewBack, mBinding.buttonImportProfile, mBinding.buttonScanProfile);
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
}
