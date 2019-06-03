package com.w3engineers.unicef.telemesh.ui.messagefeed;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        updateFeedEntity(feedEntity);
    }

    private void updateFeedEntity(FeedEntity feedEntity) {
        getCompositeDisposable().add(Single.fromCallable(() ->
                mFeedDataSource.updateFeedMessageReadStatus(feedEntity.getFeedId()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    mSelectedFeedEntityObservable.postValue(feedEntity);
                }, Throwable::printStackTrace));
    }

    /**
     * Get the selected feed entity.
     *
     * @return selected feed entity.
     */
    MutableLiveData<FeedEntity> getMessageFeedDetails() {
        return mSelectedFeedEntityObservable;
    }

}
