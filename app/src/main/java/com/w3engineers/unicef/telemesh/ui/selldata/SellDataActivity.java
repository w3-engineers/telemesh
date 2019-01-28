package com.w3engineers.unicef.telemesh.ui.selldata;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RightMeshDataSource;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivitySellDataBinding;


/**
 * * ============================================================================
 * * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [10-Jan-2019 at 10:37 AM].
 * * Email: sikderfaysal@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [10-Jan-2019 at 10:37 AM].
 * * --> <Second Editor> on [10-Jan-2019 at 10:37 AM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [10-Jan-2019 at 10:37 AM].
 * * --> <Second Reviewer> on [10-Jan-2019 at 10:37 AM].
 * * ============================================================================
 **/
public class SellDataActivity extends BaseActivity implements View.OnClickListener {

    private SellDataViewModel viewModel;
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
    protected void startUI() {

        setTitle("Sell Data ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = getViewModel();
        mBinding = (ActivitySellDataBinding) getViewDataBinding();
        mBinding.buttonBuy.setOnClickListener(this);

        currentBalance = SharedPref.getSharedPref(this).readInt("cr_token");
        earnedBalance = SharedPref.getSharedPref(this).readInt("er_token");
        mBinding.currentToken.setText(currentBalance + " RMESH");
        mBinding.spentToken.setText(earnedBalance + " RMESH");

        if (earnedBalance > 0)
            mBinding.layoutSpentToken.setVisibility(View.VISIBLE);

    }

    void showDialog(String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .show();
    }


    @Override
    public void onClick(View view) {
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

                mBinding.spentToken.setText(earnedBalance + " RMESH");
                mBinding.currentToken.setText(currentBalance + " RMESH");
                mBinding.layoutSpentToken.setVisibility(View.VISIBLE);

//                Toaster.showLong("Sell data Successfully done!");
//
                showDialog("Sell data Successfully done!");
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
