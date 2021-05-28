package com.w3engineers.unicef.telemesh.ui.messagefeed;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.BroadcastDataHelper;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentMessageFeedBinding;
import com.w3engineers.unicef.telemesh.ui.bulletindetails.BulletinDetails;
import com.w3engineers.unicef.util.base.ui.BaseFragment;
import com.w3engineers.unicef.util.helper.LanguageUtil;

public class MessageFeedFragment extends BaseFragment {


    private FragmentMessageFeedBinding mMessageFeedBinding;
    private ServiceLocator mServiceLocator;
    private MessageFeedViewModel mMessageFeedViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_feed;
    }

    @Override
    protected void startUI() {
        mMessageFeedBinding = (FragmentMessageFeedBinding) getViewDataBinding();
        initGui();
        subscribeForMessageFeed();
    }

    @Override
    public void onResume() {
        super.onResume();
        BroadcastDataHelper.getInstance().setIsFeedPageEnable(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        BroadcastDataHelper.getInstance().setIsFeedPageEnable(false);
    }

    /**
     * Subscribe for message feed update
     */
    private void subscribeForMessageFeed() {

        mMessageFeedViewModel.loadFeedList()
                .observe(this, feedEntities -> getAdapter()
                        .resetWithList(feedEntities));
        mMessageFeedViewModel.loadFeedList();
    }


    private MessageFeedAdapter getAdapter() {
        return (MessageFeedAdapter) mMessageFeedBinding
                .messageRecyclerView.getAdapter();
    }

    /**
     * Initialize the view
     */
    private void initGui() {
        mMessageFeedViewModel = getViewModel();
        initAllText();

        mMessageFeedBinding.setMessageFeedViewModel(mMessageFeedViewModel);
        mMessageFeedBinding.messageRecyclerView.setItemAnimator(null);
        mMessageFeedBinding.messageRecyclerView.setHasFixedSize(true);
        mMessageFeedBinding.messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MessageFeedAdapter messageFeedAdapter = new MessageFeedAdapter(getActivity(), mMessageFeedViewModel);
        mMessageFeedBinding.messageRecyclerView.setAdapter(messageFeedAdapter);

        mMessageFeedViewModel.getMessageFeedDetails().observe(this, this::openDetailsPage);

        mMessageFeedBinding.swipeRefresh.setOnRefreshListener(this::swipeRefreshOperation);
    }

    public void swipeRefreshOperation() {
        mMessageFeedViewModel.requestBroadcastMessage();

        mMessageFeedBinding.swipeRefresh.setRefreshing(false);
    }

    /**
     * Get the view model from the view model factory
     *
     * @return ViewModel
     */

    private MessageFeedViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getMessageFeedViewModel();
            }
        }).get(MessageFeedViewModel.class);
    }

    private void openDetailsPage(FeedEntity feedEntity) {
        Intent intent = new Intent(getActivity(), BulletinDetails.class);
        intent.putExtra(FeedEntity.class.getName(), feedEntity);
        startActivity(intent);
    }

    private void initAllText() {
        mMessageFeedBinding.textNoBroadcast.setText(LanguageUtil.getString(R.string.no_message_available));
    }

}
