package com.w3engineers.unicef.telemesh.ui.bulletindetails;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedContentModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityBulletinDetailsBinding;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

public class BulletinDetails extends TelemeshBaseActivity {

    private ActivityBulletinDetailsBinding activityBulletinDetailsBinding;
    private Animator currentAnimator;
    private long shortAnimationDuration = 300;
    private boolean isExpandCancel = false;
    private String contentPath;

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

        setClickListener(activityBulletinDetailsBinding.imageViewMessage);
        FeedEntity feedEntity = getIntent().getParcelableExtra(FeedEntity.class.getName());

        loadFeedEntityData(feedEntity);

        bulletinViewModel.updateFeedEntity(feedEntity.getFeedId());

        bulletinViewModel.getLiveFeedEntity(feedEntity.getFeedId()).observe(this, this::loadFeedEntityData);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_message:
                if (!TextUtils.isEmpty(contentPath)) {
                    zoomImageFromThumb(view, contentPath);
                }
                break;
        }
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

                    contentPath = feedContentModel.getContentPath();
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

    private void zoomImageFromThumb(final View thumbView, String imagePath) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        UIHelper.setImageInGlide(activityBulletinDetailsBinding.expandedImage, imagePath);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(1f);
        activityBulletinDetailsBinding.expandedImage.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        activityBulletinDetailsBinding.expandedImage.setPivotX(0f);
        activityBulletinDetailsBinding.expandedImage.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(activityBulletinDetailsBinding.expandedImage, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(activityBulletinDetailsBinding.expandedImage, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(activityBulletinDetailsBinding.expandedImage, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(activityBulletinDetailsBinding.expandedImage,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isExpandCancel) {
                    activityBulletinDetailsBinding.expandImageBack.setVisibility(View.VISIBLE);
                }
                isExpandCancel = false;
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
                isExpandCancel = true;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        activityBulletinDetailsBinding.expandedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityBulletinDetailsBinding.expandImageBack.setVisibility(View.GONE);
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(activityBulletinDetailsBinding.expandedImage, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(activityBulletinDetailsBinding.expandedImage,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(activityBulletinDetailsBinding.expandedImage,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(activityBulletinDetailsBinding.expandedImage,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        activityBulletinDetailsBinding.expandedImage.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        activityBulletinDetailsBinding.expandedImage.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}
