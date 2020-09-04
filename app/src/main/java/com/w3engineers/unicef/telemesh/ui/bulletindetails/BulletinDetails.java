package com.w3engineers.unicef.telemesh.ui.bulletindetails;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedContentModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityBulletinDetailsBinding;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedViewModel;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

public class BulletinDetails extends TelemeshBaseActivity {

    ActivityBulletinDetailsBinding activityBulletinDetailsBinding;

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bulletin_details;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public BaseServiceLocator a() {
        return ServiceLocator.getInstance();
    }

    @Override
    public void startUI() {
        super.startUI();
        activityBulletinDetailsBinding = (ActivityBulletinDetailsBinding) getViewDataBinding();
        BulletinViewModel bulletinViewModel = getViewModel();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FeedEntity feedEntity = getIntent().getParcelableExtra(FeedEntity.class.getName());

        loadFeedEntityData(feedEntity);

        bulletinViewModel.getLiveFeedEntity(feedEntity.getFeedId()).observe(this, this::loadFeedEntityData);
    }

    private void loadFeedEntityData(FeedEntity feedEntity) {
        activityBulletinDetailsBinding.setFeedEntity(feedEntity);

        String messageBody = feedEntity.getFeedDetail();
        if (TextUtils.isEmpty(messageBody) || messageBody.equals(Constants.BroadcastMessage.IMAGE_BROADCAST)) {
            activityBulletinDetailsBinding.message.setVisibility(View.GONE);
        } else {
            activityBulletinDetailsBinding.message.setVisibility(View.VISIBLE);
            activityBulletinDetailsBinding.message.setText(feedEntity.getFeedDetail());
        }

        try {
            activityBulletinDetailsBinding.imageViewMessage.setVisibility(View.GONE);
            String contentInfo = feedEntity.getFeedContentInfo();
            if (!TextUtils.isEmpty(contentInfo)) {

                FeedContentModel feedContentModel = GsonBuilder.getInstance().getFeedContentModelObj(contentInfo);
                if (feedContentModel != null) {

                    String contentPath = feedContentModel.getContentPath();
                    if (!TextUtils.isEmpty(contentPath)) {
                        activityBulletinDetailsBinding.imageViewMessage.setVisibility(View.VISIBLE);
                        UIHelper.setImageInGlide(activityBulletinDetailsBinding.imageViewMessage, contentPath);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BulletinViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getBulletinViewModel();
            }
        }).get(BulletinViewModel.class);
    }
}
