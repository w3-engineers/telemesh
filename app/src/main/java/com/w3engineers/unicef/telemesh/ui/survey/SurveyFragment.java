package com.w3engineers.unicef.telemesh.ui.survey;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentSurveyBinding;

public class SurveyFragment extends BaseFragment {


    public SurveyFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_survey;
    }

    @Override
    protected void startUI() {

        //    private ServiceLocator serviceLocator;
        SurveyViewModel mSurveyViewModel = getViewModel();
        FragmentSurveyBinding mSurveyBinding = (FragmentSurveyBinding) getViewDataBinding();

    }

    private SurveyViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//                serviceLocator = ServiceLocator.getInstance();
                return (T) ServiceLocator.getInstance().getSurveyViewModel();
            }
        }).get(SurveyViewModel.class);
    }
}
