package com.w3engineers.unicef.telemesh.ui.messagefeed;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;

import java.util.List;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MessageFeedViewModel extends BaseRxViewModel {

    // FeedDataSource instance
    private FeedDataSource mFeedDataSource;
    // Selected feed entity for details
    private MutableLiveData<FeedEntity> mSelectedFeedEntityObservable = new MutableLiveData<>();

    private LiveData<List<FeedEntity>> mFeedEntitiesObservable;


    public MessageFeedViewModel(FeedDataSource feedDataSource) {
        this.mFeedDataSource = feedDataSource;
        this.mFeedEntitiesObservable = mFeedDataSource.loadFeeds();
    }

    /**
     * Expose the LiveData FeedList query so the UI can observe it.
     *
     * @return FeedEntity list
     */
    public LiveData<List<FeedEntity>> loadFeedList() {
        if (mFeedEntitiesObservable != null) {
            return mFeedEntitiesObservable;
        }
        return mFeedDataSource.loadFeeds();
    }

    /**
     * From the recycler view; the selected item will be post.
     *
     * @param feedEntity selected feed entity
     */
    public void postMessageFeedEntity(FeedEntity feedEntity) {
        mSelectedFeedEntityObservable.postValue(feedEntity);
    }

    /**
     * Get the selected feed entity.
     *
     * @return selected feed entity.
     */
    MutableLiveData<FeedEntity> getMessageFeedDetails() {
        return mSelectedFeedEntityObservable;
    }

    public void onBroadcastButtonClick(){
        Log.e("Button" , "Clicked");

        /*String message = "Broadcast Test Message";
        RmDataHelper.getInstance().broadcastMessage(message.getBytes());*/

        RmDataHelper.getInstance().requestWsMessage();
    }

}
