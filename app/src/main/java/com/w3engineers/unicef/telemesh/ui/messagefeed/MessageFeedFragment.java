package com.w3engineers.unicef.telemesh.ui.messagefeed;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentMessageFeedBinding;
import com.w3engineers.unicef.telemesh.ui.bulletindetails.BulletinDetails;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.LanguageUtil;

public class MessageFeedFragment extends BaseFragment {


    private FragmentMessageFeedBinding mMessageFeedBinding;
    private ServiceLocator mServiceLocator;
    private MessageFeedViewModel mMessageFeedViewModel;
    private MessageFeedAdapter messageFeedAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_feed;
    }

    @Override
    protected void startUI() {
        mMessageFeedBinding = (FragmentMessageFeedBinding) getViewDataBinding();
        setHasOptionsMenu(true);
        initGui();
        subscribeForMessageFeed();
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
        messageFeedAdapter = new MessageFeedAdapter(getActivity(), mMessageFeedViewModel);
        mMessageFeedBinding.messageRecyclerView.setAdapter(messageFeedAdapter);

        mMessageFeedViewModel.getMessageFeedDetails().observe(this, this::openDetailsPage);
        mMessageFeedViewModel.deleteMutableLiveData.observe(this, aBoolean -> {
            deleteAll();
        });

        mMessageFeedBinding.swipeRefresh.setOnRefreshListener(this::swipeRefreshOperation);
    }

    public void swipeRefreshOperation() {
        mMessageFeedViewModel.requestBroadcastMessage();

        mMessageFeedBinding.swipeRefresh.setRefreshing(false);
    }

    private void deleteAll() {
        if(messageFeedAdapter != null) {
            messageFeedAdapter.clear();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.menu_delete, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            if (getActivity() != null) {
                mMessageFeedViewModel.deleteAll();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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
