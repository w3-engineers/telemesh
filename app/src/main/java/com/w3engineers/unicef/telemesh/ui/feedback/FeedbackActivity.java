package com.w3engineers.unicef.telemesh.ui.feedback;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityFeedbackBinding;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class FeedbackActivity extends TelemeshBaseActivity {

    private ActivityFeedbackBinding mBinding;
    private FeedbackViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }


    @Override
    public BaseServiceLocator a() {
        return null;
    }

    @Override
    public void startUI() {
        super.startUI();
        mBinding = (ActivityFeedbackBinding) getViewDataBinding();
        mViewModel = getViewModel();
        setTitle(LanguageUtil.getString(R.string.feedback));
        initView();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.button_feed_back) {
            sendFeedBack();
        }
    }

    private void initView() {
        setClickListener(mBinding.buttonFeedBack);
        initAllText();
        mViewModel.feedbackResponse().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toaster.showShort(LanguageUtil.getString(R.string.feedback_submitted_successfully));
                mBinding.editTextFeedback.setText("");
            }
        });
    }

    private void sendFeedBack() {
        String feedBackText = mBinding.editTextFeedback.getText().toString();
        UIHelper.hideKeyboardFrom(this, mBinding.editTextFeedback);
        if (!TextUtils.isEmpty(feedBackText)) {
            mViewModel.sendFeedback(feedBackText);
        } else {
            Toaster.showShort(LanguageUtil.getString(R.string.please_write_your_feedback));
        }
    }

    private FeedbackViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getFeedbackViewModel(getApplication());
            }
        }).get(FeedbackViewModel.class);
    }

    private void initAllText() {
        mBinding.buttonFeedBack.setText(LanguageUtil.getString(R.string.send_feedback));
        mBinding.editTextFeedback.setHint(LanguageUtil.getString(R.string.add_your_feedback));
    }
}
