package com.w3engineers.unicef.telemesh.ui.dataplan;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityDataPlanBinding;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactAdapter;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactViewModel;

 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class DataPlanActivity extends BaseActivity {

    private ActivityDataPlanBinding activityDataPlanBinding;
    private DataPlanViewModel dataPlanViewModel;
    private BuyerAdapter buyerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_plan;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected void startUI() {

        activityDataPlanBinding = (ActivityDataPlanBinding) getViewDataBinding();
        dataPlanViewModel = getViewModel();

        setClickListener(activityDataPlanBinding.opBack);
        init();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.op_back:
                finish();
                break;
        }
    }

    private void init() {
        setDataPlanRadio();
        dataLimitControl();
        initRecyclerView();
        setBuyerDataInfo();
    }

    private void setDataPlanRadio() {

        activityDataPlanBinding.dataPlanType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.mesh_user:
                    setMeshUserInfo();
                    break;

                case R.id.data_seller:
                    setDataSellInfo();
                    break;

                case R.id.data_buyer:
                    setDataBuyInfo();
                    break;
            }
        });

        RadioButton meshUser = activityDataPlanBinding.dataPlanType.findViewById(R.id.mesh_user);
        meshUser.setChecked(true);
    }

    private void resetAllView() {
        activityDataPlanBinding.meshUserLayout.setVisibility(View.GONE);
        activityDataPlanBinding.dataSellLayout.setVisibility(View.GONE);
        activityDataPlanBinding.dataBuyLayout.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        activityDataPlanBinding.dataBuyerList.setItemAnimator(null);
        activityDataPlanBinding.dataBuyerList.setHasFixedSize(true);
        activityDataPlanBinding.dataBuyerList.setLayoutManager(new LinearLayoutManager(this));

        buyerAdapter = new BuyerAdapter();
        activityDataPlanBinding.dataBuyerList.setAdapter(buyerAdapter);
    }

    // Mesh user part ++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void setMeshUserInfo() {
        resetAllView();

        activityDataPlanBinding.meshUserLayout.setVisibility(View.VISIBLE);
        String meshUserInfo = getResources().getString(R.string.mesh_user_info);

        Spanned text = getSpannedText(meshUserInfo);
        activityDataPlanBinding.defaultInfo.setText(text, TextView.BufferType.SPANNABLE);
    }

    // Data sell part ++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void setDataSellInfo() {
        resetAllView();
        activityDataPlanBinding.dataSellLayout.setVisibility(View.VISIBLE);

        activityDataPlanBinding.dataShareToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dataShareEnable();
            } else {
                dataShareDisable();
            }
        });
    }

    private void dataShareEnable() {
        activityDataPlanBinding.dataShareState.setText(getResources().getString(R.string.data_share_on));
        activityDataPlanBinding.enableDisableLayout.setAlpha(1.0f);
        activityDataPlanBinding.limitControl.setClickable(false);
        activityDataPlanBinding.range.setClickable(false);
        activityDataPlanBinding.fromDate.setClickable(false);
        activityDataPlanBinding.toDate.setClickable(false);
    }

    private void dataShareDisable() {
        activityDataPlanBinding.dataShareState.setText(getResources().getString(R.string.data_share_off));
        activityDataPlanBinding.enableDisableLayout.setAlpha(0.5f);
        activityDataPlanBinding.limitControl.setClickable(true);
        activityDataPlanBinding.range.setClickable(true);
        activityDataPlanBinding.fromDate.setClickable(true);
        activityDataPlanBinding.toDate.setClickable(true);
    }

    private void dataLimitControl() {
        activityDataPlanBinding.limitControl.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.unlimited:
                    unlimitedDataPlan();
                    break;

                case R.id.limit_to:
                    limitDataPlan();
                    break;
            }
        });
    }

    private void unlimitedDataPlan() {
        activityDataPlanBinding.dataLimitInfo.setVisibility(View.GONE);
        activityDataPlanBinding.range.setAlpha(0.5f);
        activityDataPlanBinding.dataUnit.setAlpha(0.5f);
        activityDataPlanBinding.range.setEnabled(false);
        activityDataPlanBinding.dataUnit.setEnabled(false);
    }

    private void limitDataPlan() {
        activityDataPlanBinding.dataLimitInfo.setVisibility(View.VISIBLE);
        activityDataPlanBinding.range.setAlpha(1.0f);
        activityDataPlanBinding.dataUnit.setAlpha(1.0f);
        activityDataPlanBinding.range.setEnabled(true);
        activityDataPlanBinding.dataUnit.setEnabled(true);

        dataLimitInfoChange(30 + "", activityDataPlanBinding.range.getText() + "");

        activityDataPlanBinding.range.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataLimitInfoChange(30 + "", s + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void dataLimitInfoChange(String consumed, String total) {
        String formatString = getResources().getString(R.string.transferred_total);
        String limitInfo = String.format(getSpannedText(formatString) + "", consumed, total);
        activityDataPlanBinding.dataLimitInfo.setText(limitInfo);
    }

    // Data buy part ++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void setDataBuyInfo() {
        resetAllView();
        activityDataPlanBinding.dataBuyLayout.setVisibility(View.VISIBLE);
    }

    private void setBuyerDataInfo() {
        dataPlanViewModel.getBuyerUsers.observe(this, buyerUsers -> {
            if (buyerAdapter != null) {
                buyerAdapter.addItem(buyerUsers);
            }
        });

        dataPlanViewModel.getBuyerList();
    }

    private Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }


    private DataPlanViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//                serviceLocator = ServiceLocator.getInstance();
                return (T) ServiceLocator.getInstance().getDataPlanViewModel();
            }
        }).get(DataPlanViewModel.class);
    }
}
