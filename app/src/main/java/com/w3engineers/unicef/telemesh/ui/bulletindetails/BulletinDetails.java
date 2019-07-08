package com.w3engineers.unicef.telemesh.ui.bulletindetails;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.databinding.ActivityBulletinDetailsBinding;

public class BulletinDetails extends BaseActivity {

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
    protected void startUI() {

        ActivityBulletinDetailsBinding activityBulletinDetailsBinding = (ActivityBulletinDetailsBinding) getViewDataBinding();

        FeedEntity feedEntity = getIntent().getParcelableExtra(FeedEntity.class.getName());
        activityBulletinDetailsBinding.setFeedEntity(feedEntity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
