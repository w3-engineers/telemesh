package com.w3engineers.unicef.telemesh.ui.mywallet;

import android.app.Dialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMyDataWalletBinding;
import com.w3engineers.unicef.telemesh.ui.selldata.SellDataActivity;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MyWalletActivity extends BaseActivity implements View.OnClickListener{

    private ActivityMyDataWalletBinding mBinding;

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_data_wallet;
    }

    @Override
    protected void startUI() {

        setTitle(getString(R.string.settings_open_wallet));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MyWalletViewModel viewModel = getViewModel();
        mBinding = (ActivityMyDataWalletBinding) getViewDataBinding();

        setClickListener(mBinding.walletAddress, mBinding.opBack);
    }

    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();
        switch (id) {
            case R.id.op_back:
                finish();
                break;
            case R.id.wallet_address:
                showDialog();
                break;
        }
        super.onClick(view);
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_wallet_address);

        String myAddress = SharedPref.getSharedPref(this).read(Constants.preferenceKey.MY_USER_ID);

        TextView walletId = dialog.findViewById(R.id.my_id);
        ImageView imageView = dialog.findViewById(R.id.image_view_qr_code);

        walletId.setText(myAddress);


        Button copy = dialog.findViewById(R.id.copy_address);
        copy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private MyWalletViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                 return (T) ServiceLocator.getInstance().getMyWalletViewModel(getApplication());
            }
        }).get(MyWalletViewModel.class);
    }
}
