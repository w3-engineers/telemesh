package com.w3engineers.unicef.telemesh.ui.selectaccount;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivitySelectAccountBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedViewModel;
import com.w3engineers.unicef.util.base.ui.BaseActivity;
import com.w3engineers.unicef.util.base.ui.BaseServiceLocator;
import com.w3engineers.unicef.util.base.ui.TelemeshBaseActivity;

/**
 * Created by Azizul Islam on 10/22/21.
 */
public class SelectAccountActivity extends BaseActivity {
    private SelectAccountViewModel selectAccountViewModel;
    private ActivitySelectAccountBinding viewBinder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_account;
    }



    @Override
    public void startUI() {
        viewBinder = (ActivitySelectAccountBinding) getViewDataBinding();
        selectAccountViewModel = getViewModel();
        setClickListener(viewBinder.buttonCreateAccount, viewBinder.buttonImportAccount);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_create_account) {
            Intent intent = new Intent(this, CreateUserActivity.class);
            intent.putExtra("import_wallet", false);
            startActivity(intent);
        } else if (view.getId() == R.id.button_import_account) {
            Intent intent = new Intent(this, CreateUserActivity.class);
            intent.putExtra("import_wallet", true);
            startActivity(intent);
        }
    }

    @Override
    protected void stopUI() {
        super.stopUI();
    }

    private SelectAccountViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getSelectAccountViewModel();
            }
        }).get(SelectAccountViewModel.class);
    }
}
