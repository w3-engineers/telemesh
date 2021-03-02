package com.w3engineers.unicef.telemesh.ui.aboutus;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityAboutUsBinding;
import com.w3engineers.unicef.util.helper.LanguageUtil;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class AboutUsActivity extends TelemeshBaseActivity {

    private ActivityAboutUsBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
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
    public BaseServiceLocator a() {
        return ServiceLocator.getInstance();
    }


    @Override
    public void startUI() {
        super.startUI();
        AboutUsViewModel aboutUsViewModel = getViewModel();
        mBinding = (ActivityAboutUsBinding) getViewDataBinding();
        setTitle(LanguageUtil.getString(R.string.activity_about_us));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mBinding.setAboutViewModel(aboutUsViewModel);

        initView();
    }

    private void initView() {
        mBinding.textViewPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.textViewTerms.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.textViewTelemeshWeb.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.textViewW3Web.setMovementMethod(LinkMovementMethod.getInstance());

        stripUnderlines(mBinding.textViewPrivacy);
        stripUnderlines(mBinding.textViewTerms);
        stripUnderlines(mBinding.textViewTelemeshWeb);
        stripUnderlines(mBinding.textViewW3Web);
    }


    private AboutUsViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getAboutUsViewModel(getApplication());
            }
        }).get(AboutUsViewModel.class);
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
