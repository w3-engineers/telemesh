package com.w3engineers.unicef.telemesh.ui.bulletindetails;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityBulletinDetailsBinding;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

public class BulletinDetails extends TelemeshBaseActivity {

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
        ActivityBulletinDetailsBinding activityBulletinDetailsBinding = (ActivityBulletinDetailsBinding) getViewDataBinding();

        FeedEntity feedEntity = getIntent().getParcelableExtra(FeedEntity.class.getName());
        activityBulletinDetailsBinding.setFeedEntity(feedEntity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String details = feedEntity.getFeedDetail();
        activityBulletinDetailsBinding.image.setImageResource(UIHelper.getBroadcastLogo(details));
        activityBulletinDetailsBinding.textViewLastName.setText(UIHelper.getBroadcastTitle(details));
        activityBulletinDetailsBinding.message.setText(UIHelper.getBroadcastMessage(details));
        activityBulletinDetailsBinding.textViewName.setText(UIHelper.getBroadcastTitle(details));
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
    }*/
}
