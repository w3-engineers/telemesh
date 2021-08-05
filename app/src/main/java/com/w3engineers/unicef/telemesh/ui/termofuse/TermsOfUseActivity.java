package com.w3engineers.unicef.telemesh.ui.termofuse;

import android.content.Intent;
import android.view.View;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.databinding.ActivityTermsOfUseBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.util.base.ui.BaseActivity;

public class TermsOfUseActivity extends BaseActivity {

    private ActivityTermsOfUseBinding mBinding;

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_terms_of_use;
    }


    @Override
    protected void startUI() {
        mBinding = (ActivityTermsOfUseBinding) getViewDataBinding();
        setTitle(getResources().getString(R.string.terms_of_use_details));
        initView();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.button_next) {
            gotoProfileChoicePage();
        }
    }

    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        mBinding.webViewTerms.loadUrl("file:///android_asset/terms_of_use.html");
        mBinding.buttonNext.setOnClickListener(this);

        mBinding.checkBoxTermsOfUse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mBinding.buttonNext.setEnabled(true);
                mBinding.buttonNext.setBackgroundResource(R.drawable.ractangular_gradient);
                mBinding.buttonNext.setTextColor(getResources().getColor(R.color.white));
            } else {
                mBinding.buttonNext.setEnabled(false);
                mBinding.buttonNext.setBackgroundResource(R.drawable.ractangular_white);
                mBinding.buttonNext.setTextColor(getResources().getColor(R.color.new_user_button_color));
            }
        });
    }

    private void gotoProfileChoicePage() {
        startActivity(new Intent(TermsOfUseActivity.this, CreateUserActivity.class));
        finish();
    }
}
