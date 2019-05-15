package com.w3engineers.unicef.telemesh.ui.setorganization;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityCreateUserBinding;
import com.w3engineers.unicef.telemesh.databinding.ActivitySetOrganisationBinding;
import com.w3engineers.unicef.telemesh.ui.chooseprofileimage.ProfileImageActivity;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserViewModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class SetOrganizationActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySetOrganisationBinding mBinding;
    private SetOrganizationViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_organisation;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void startUI() {

        mBinding = (ActivitySetOrganisationBinding) getViewDataBinding();
        mViewModel = getViewModel();

        initUI();

        final Flowable<Boolean> organizationObservable = RxTextView.afterTextChangeEvents(mBinding.editTextOrganization)
                .map(textViewAfterTextChangeEvent -> mViewModel.isInfoValid(textViewAfterTextChangeEvent.view()
                        .getText().toString())).toFlowable(BackpressureStrategy.LATEST);

        final Flowable<Boolean> idObservable = RxTextView.afterTextChangeEvents(mBinding.editTextId)
                .map(textViewAfterTextChangeEvent -> mViewModel.isInfoValid(textViewAfterTextChangeEvent.view()
                        .getText().toString())).toFlowable(BackpressureStrategy.LATEST);


        final Flowable<Boolean> combineResult = Flowable.combineLatest(
                organizationObservable,
                idObservable,
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2
        );

        getCompositeDisposable().add(combineResult.subscribe(aBoolean -> {
                    mBinding.buttonSignup.setEnabled(aBoolean);
                    if (aBoolean) {
                        mBinding.buttonSignup.setBackgroundResource(R.drawable.ractangular_gradient);
                        mBinding.buttonSignup.setTextColor(getResources().getColor(R.color.white));
                        mBinding.buttonSignup.setClickable(true);
                    } else {
                        mBinding.buttonSignup.setBackgroundResource(R.drawable.ractangular_white);
                        mBinding.buttonSignup.setTextColor(getResources().getColor(R.color.deep_grey));
                        mBinding.buttonSignup.setClickable(false);
                    }
                }, Throwable::printStackTrace
        ));
    }

    private void initUI() {
        UIHelper.setImageResource(mBinding.imageProfile, mViewModel.getImageIndex());
        mBinding.userName.setText(mViewModel.getName());
        mBinding.opBack.setOnClickListener(this);

        setClickListener(mBinding.buttonSignup, mBinding.buttonSkip);

        UIHelper.hideKeyboardFrom(this, mBinding.editTextId);
        UIHelper.hideKeyboardFrom(this, mBinding.editTextOrganization);
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);

        int id = view.getId();

        switch (id) {
            case R.id.button_signup:
                goNext(mBinding.editTextOrganization.getText() + "", mBinding.editTextId.getText() + "");
                break;

            case R.id.button_skip:
                goNext("", "");
                break;

            case R.id.op_back:
                finish();
                break;
        }
    }

    private void goNext(String companyName, String companyId) {
        if (mViewModel.storeData(companyName, companyId)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private SetOrganizationViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getSetOrganizationViewModel(getApplication());
            }
        }).get(SetOrganizationViewModel.class);
    }
}
