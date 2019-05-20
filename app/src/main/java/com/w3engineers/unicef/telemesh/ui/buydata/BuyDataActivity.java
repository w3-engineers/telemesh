package com.w3engineers.unicef.telemesh.ui.buydata;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityBuyDataBinding;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class BuyDataActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBuyDataBinding mBinding;
    private int currentBalance;
    private int spentBalance;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_data;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected void startUI() {

        setTitle("Buy Data ");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        BuyDataViewModel viewModel = getViewModel();
        mBinding = (ActivityBuyDataBinding) getViewDataBinding();
        mBinding.buttonBuy.setOnClickListener(this);

        currentBalance = SharedPref.getSharedPref(this).readInt("cr_token");
        spentBalance = SharedPref.getSharedPref(this).readInt("sp_token");
        String cBalance = currentBalance + " " + getString(R.string.rmesh);
        mBinding.currentToken.setText(cBalance);
        String sBalance = spentBalance + " " + getString(R.string.rmesh);
        mBinding.spentToken.setText(sBalance);

        if (spentBalance > 0)
            mBinding.layoutSpentToken.setVisibility(View.VISIBLE);

    }

    private void showDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.alert_buy_sell_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        TextView alertTitle = dialogView.findViewById(R.id.confirmation_title);
        alertTitle.setText(getString(R.string.buy_data_success));

        Button alertButton = dialogView.findViewById(R.id.confirmation_ok);

        alertButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }


    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_buy:
                String buyTokenTxt = mBinding.editTextBuyToken.getText().toString();
                if (!buyTokenTxt.equals("")) {

                    int buyToken = 100;//Integer.parseInt(mBinding.editTextBuyToken.getText().toString());
                    SharedPref.getSharedPref(BuyDataActivity.this).write("sp_token", (spentBalance + buyToken));
                    SharedPref.getSharedPref(BuyDataActivity.this).write("cr_token", (currentBalance - buyToken));

                    currentBalance = currentBalance - buyToken;
                    spentBalance = spentBalance + buyToken;

                    String cBalance = currentBalance + " " + getString(R.string.rmesh);
                    String sBalance = spentBalance + " " + getString(R.string.rmesh);

                    mBinding.spentToken.setText(sBalance);
                    mBinding.currentToken.setText(cBalance);
                    mBinding.layoutSpentToken.setVisibility(View.VISIBLE);

//                    Toaster.showLong("Buy data Successfully done!");
//
                    showDialog();
                }
                break;
        }
        super.onClick(view);
    }

    private BuyDataViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getBuyDataViewModel(getApplication());
            }
        }).get(BuyDataViewModel.class);
    }
}
