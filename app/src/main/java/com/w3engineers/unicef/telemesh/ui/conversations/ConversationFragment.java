package com.w3engineers.unicef.telemesh.ui.conversations;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentConversationBinding;
import com.w3engineers.unicef.util.helper.LanguageUtil;

public class ConversationFragment extends BaseFragment {

    private FragmentConversationBinding conversationBinding;
    private ConversationViewModel conversationViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void startUI() {
        conversationBinding = (FragmentConversationBinding) getViewDataBinding();

        setHasOptionsMenu(true);
        setTitle(LanguageUtil.getString(R.string.title_conversation_fragment));
    }

    private void initAll() {
        conversationViewModel = getViewModel();
    }

    private void initListener() {
        conversationViewModel.getGroupConversation().observe(this, groupEntities -> {

        });
    }

    private ConversationViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getConversationViewModel(getActivity().getApplication());
            }
        }).get(ConversationViewModel.class);
    }

    private void controlEmptyLayout(boolean isEmpty) {
        conversationBinding.emptyLayout.setVisibility(View.GONE);
        if (isEmpty) {
            conversationBinding.emptyLayout.setVisibility(View.VISIBLE);
        }
    }
}
