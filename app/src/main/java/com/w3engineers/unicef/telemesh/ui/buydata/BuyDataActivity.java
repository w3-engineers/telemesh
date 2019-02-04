package com.w3engineers.unicef.telemesh.ui.buydata;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RightMeshDataSource;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityBuyDataBinding;
import com.w3engineers.unicef.telemesh.databinding.ActivityMyWalletBinding;


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
public class BuyDataActivity extends BaseActivity implements View.OnClickListener {

    private BuyDataViewModel viewModel;
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
    protected void startUI() {

        setTitle("Buy Data ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = getViewModel();
        mBinding = (ActivityBuyDataBinding) getViewDataBinding();
        mBinding.buttonBuy.setOnClickListener(this);

        currentBalance = SharedPref.getSharedPref(this).readInt("cr_token");
        spentBalance = SharedPref.getSharedPref(this).readInt("sp_token");
        mBinding.currentToken.setText(currentBalance + " RMESH");
        mBinding.spentToken.setText(spentBalance + " RMESH");

        if (spentBalance > 0)
            mBinding.layoutSpentToken.setVisibility(View.VISIBLE);

    }

    void showDialog(String message) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_buy_sell_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        TextView alertTitle = dialogView.findViewById(R.id.confirmation_title);
        alertTitle.setText(message);

        Button alertButton = dialogView.findViewById(R.id.confirmation_ok);

        alertButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }


    @Override
    public void onClick(View view) {
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

                    mBinding.spentToken.setText(spentBalance + " RMESH");
                    mBinding.currentToken.setText(currentBalance + " RMESH");
                    mBinding.layoutSpentToken.setVisibility(View.VISIBLE);

//                    Toaster.showLong("Buy data Successfully done!");
//
                    showDialog("Buy data Successfully done!");
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
