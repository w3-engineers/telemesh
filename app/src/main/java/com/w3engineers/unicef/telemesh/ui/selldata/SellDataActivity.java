package com.w3engineers.unicef.telemesh.ui.selldata;

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
import com.w3engineers.unicef.telemesh.databinding.ActivitySellDataBinding;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class SellDataActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySellDataBinding mBinding;
    private int currentBalance;
    private int earnedBalance;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sell_data;
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

        setTitle("Sell Data ");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SellDataViewModel viewModel = getViewModel();
        mBinding = (ActivitySellDataBinding) getViewDataBinding();
        mBinding.buttonBuy.setOnClickListener(this);

        currentBalance = SharedPref.getSharedPref(this).readInt("cr_token");
        earnedBalance = SharedPref.getSharedPref(this).readInt("er_token");

        String cBalance = currentBalance + " " + getString(R.string.rmesh);
        String eBalance = earnedBalance + " " + getString(R.string.rmesh);

        mBinding.currentToken.setText(cBalance);
        mBinding.spentToken.setText(eBalance);

        if (earnedBalance > 0)
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
        alertTitle.setText(getString(R.string.sell_data_success));

        Button alertButton = dialogView.findViewById(R.id.confirmation_ok);

        alertButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }


    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_buy:

//                String buyTokenTxt = mBinding.editTextBuyToken.getText().toString();
//                if (!buyTokenTxt.equals("")) {

                int buyToken = 100;//Integer.parseInt(mBinding.editTextBuyToken.getText().toString());
                SharedPref.getSharedPref(SellDataActivity.this).write("cr_token", (currentBalance + buyToken));
                SharedPref.getSharedPref(SellDataActivity.this).write("er_token", (earnedBalance + buyToken));

                currentBalance = currentBalance + buyToken;
                earnedBalance = earnedBalance + buyToken;

                String cBalance = currentBalance + " " + getString(R.string.rmesh);
                String eBalance = earnedBalance + " " + getString(R.string.rmesh);

                mBinding.spentToken.setText(eBalance);
                mBinding.currentToken.setText(cBalance);
                mBinding.layoutSpentToken.setVisibility(View.VISIBLE);

//                Toaster.showLong("Sell data Successfully done!");
//
                showDialog();
//                }
                break;
        }
        super.onClick(view);
    }

    private SellDataViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getSellDataViewModel(getApplication());
            }
        }).get(SellDataViewModel.class);
    }
}
