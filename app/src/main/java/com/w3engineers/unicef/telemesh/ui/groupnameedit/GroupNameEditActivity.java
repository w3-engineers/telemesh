package com.w3engineers.unicef.telemesh.ui.groupnameedit;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.mesh.application.ui.util.ToastUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityGroupNameEditBinding;
import com.w3engineers.unicef.telemesh.ui.editprofile.EditProfileViewModel;

public class GroupNameEditActivity extends TelemeshBaseActivity {

    private ActivityGroupNameEditBinding mBinding;
    private GroupNameEditViewModel mViewModel;
    private String mGroupId;
    private String mGroupName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_name_edit;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public void startUI() {
        mBinding = (ActivityGroupNameEditBinding) getViewDataBinding();
        mViewModel = getViewModel();

        initView();
    }

    @Override
    protected void stopUI() {
        super.stopUI();
    }

    @Override
    public BaseServiceLocator a() {
        return null;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.op_back:
                finish();
                break;
            case R.id.button_done:
                saveName();
                break;
        }
    }

    private void initView() {
        setClickListener(mBinding.opBack, mBinding.buttonDone);

        parseIntent();

        mViewModel.textChangeLiveData.observe(this, this::updateButtonControl);
        mViewModel.textEditControl(mBinding.editTextName);
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(GroupEntity.class.getName())) {
            mGroupId = intent.getStringExtra(GroupEntity.class.getName());
            mGroupName = intent.getStringExtra(GroupNameEditActivity.class.getName());

            mBinding.editTextName.setText(mGroupName);
        }
    }

    private void saveName() {
        if (mBinding.editTextName.getText() != null) {
            String updatedName = mBinding.editTextName.getText().toString().trim();
            if (!updatedName.equals(mGroupName)) {
                mViewModel.updateGroupName(updatedName, mGroupId);
                ToastUtil.showShort(this, getString(R.string.group_name_updated));
            }
            finish();
        }
    }

    private void updateButtonControl(String nameText) {
        if (!TextUtils.isEmpty(nameText) &&
                nameText.length() >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT) {

            mBinding.buttonDone.setBackgroundResource(R.drawable.ractangular_gradient);
            mBinding.buttonDone.setTextColor(getResources().getColor(R.color.white));
            mBinding.buttonDone.setEnabled(true);
        } else {

            mBinding.buttonDone.setBackgroundResource(R.drawable.ractangular_white);
            mBinding.buttonDone.setTextColor(getResources().getColor(R.color.new_user_button_color));
            mBinding.buttonDone.setEnabled(false);
        }
    }

    private GroupNameEditViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getGroupNameEditViewModel(getApplication());
            }
        }).get(GroupNameEditViewModel.class);
    }
}