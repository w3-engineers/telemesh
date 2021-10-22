package com.w3engineers.unicef.telemesh.ui.selectaccount;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedViewModel;
import com.w3engineers.unicef.util.base.ui.BaseServiceLocator;
import com.w3engineers.unicef.util.base.ui.TelemeshBaseActivity;

/**
 * Created by Azizul Islam on 10/22/21.
 */
public class SelectAccountActivity extends TelemeshBaseActivity {
    private SelectAccountViewModel selectAccountViewModel;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_account;
    }

    @Override
    public BaseServiceLocator a() {
        return ServiceLocator.getInstance();
    }

    @Override
    public void startUI() {
        super.startUI();
        selectAccountViewModel = getViewModel();
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
