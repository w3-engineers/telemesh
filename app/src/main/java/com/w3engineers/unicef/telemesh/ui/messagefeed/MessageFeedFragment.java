package com.w3engineers.unicef.telemesh.ui.messagefeed;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentMessageFeedBinding;

public class MessageFeedFragment extends BaseFragment {


    public MessageFeedFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_feed;
    }

    @Override
    protected void startUI() {

        //    private ServiceLocator serviceLocator;
        MessageFeedViewModel mMessageFeedViewModel = getViewModel();
        FragmentMessageFeedBinding mMessageFeedBinding = (FragmentMessageFeedBinding) getViewDataBinding();

    }

    private MessageFeedViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//                serviceLocator = ServiceLocator.getInstance();
                return (T) ServiceLocator.getInstance().getMessageFeedViewModel();
            }
        }).get(MessageFeedViewModel.class);
    }
}
