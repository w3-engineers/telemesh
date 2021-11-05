package com.w3engineers.unicef.telemesh.ui.messagefeed;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import com.w3engineers.unicef.telemesh.data.helper.BroadcastDataHelper;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.util.base.ui.BaseRxViewModel;

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


    public MessageFeedViewModel(@NonNull FeedDataSource feedDataSource) {
        this.mFeedDataSource = feedDataSource;
        this.mFeedEntitiesObservable = mFeedDataSource.loadFeeds();
    }

    /**
     * Expose the LiveData FeedList query so the UI can observe it.
     *
     * @return FeedEntity list
     */
    @NonNull
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
    public void postMessageFeedEntity(@NonNull FeedEntity feedEntity) {
        mSelectedFeedEntityObservable.postValue(feedEntity);
    }

    public void requestBroadcastMessage() {
//        if(NetworkMonitor.isOnline()) {
//            BroadcastDataHelper.getInstance().demoTextBroadcast();
          BroadcastDataHelper.getInstance().requestForBroadcast();
    //    BroadcastDataHelper.getInstance().testLocalBroadcast();
//        }
    }

/*    private void updateFeedEntity(FeedEntity feedEntity) {
        getCompositeDisposable().add(Single.fromCallable(() ->
                mFeedDataSource.updateFeedMessageReadStatus(feedEntity.getFeedId()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {

                }, Throwable::printStackTrace));
    }*/

    /**
     * Get the selected feed entity.
     *
     * @return selected feed entity.
     */
    MutableLiveData<FeedEntity> getMessageFeedDetails() {
        return mSelectedFeedEntityObservable;
    }

}
