package com.w3engineers.unicef.telemesh.ui.bulletindetails;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;

public class BulletinViewModel extends BaseRxViewModel {

    private FeedDataSource feedDataSource;

    public BulletinViewModel(@NonNull FeedDataSource feedDataSource) {
        this.feedDataSource = feedDataSource;
    }

    LiveData<FeedEntity> getLiveFeedEntity(String feedId) {
        return feedDataSource.getLiveFeedEntity(feedId);
    }

}
