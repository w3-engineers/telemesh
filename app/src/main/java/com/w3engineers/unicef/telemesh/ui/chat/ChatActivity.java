package com.w3engineers.unicef.telemesh.ui.chat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3engineers.ext.strom.util.Text;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.LayoutManagerWithSmoothScroller;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityChatRevisedBinding;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.MyGlideEngineUtil;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.List;

import timber.log.Timber;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class ChatActivity extends TelemeshBaseActivity {
    /**
     * <h1>Instance variable scope</h1>
     */
    private ChatViewModel mChatViewModel;
    private UserEntity mUserEntity;
    private GroupEntity mGroupEntity;
    private String threadId;
    private boolean isGroup;
    @Nullable
    public ChatPagedAdapterRevised mChatPagedAdapter;
    @Nullable
    public ActivityChatRevisedBinding mViewBinging;
    @Nullable
    public LayoutManagerWithSmoothScroller mLinearLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_revised;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar_chat;
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
        Intent intent = getIntent();
        threadId = intent.getStringExtra(UserEntity.class.getName());
        isGroup = intent.getBooleanExtra(GroupEntity.class.getName(), false);

        if (TextUtils.isEmpty(threadId)) { finish(); return; }

        mViewBinging = (ActivityChatRevisedBinding) getViewDataBinding();
        setTitle("");

        mChatViewModel = getViewModel();
        initComponent();

        subscribeForThreadEvent(threadId);
        subscribeForMessages(threadId);
        subscribeForFinishEvent();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Text.isNotEmpty(threadId)) {
            mChatViewModel.updateAllMessageStatus(threadId);
        }
    }

    private void setUiComponent() {
        if (mViewBinging == null)
            return;

        if (isGroup) {
            if (mGroupEntity != null) {
                mViewBinging.imageProfile.setBackgroundResource(R.mipmap.group_white_circle);
                UIHelper.setGroupName(mViewBinging.textViewLastName, mGroupEntity.groupName);
                mViewBinging.imageView.setVisibility(View.GONE);

                mViewBinging.groupBlock.setVisibility(View.GONE);
                mViewBinging.chatMessageBar.setVisibility(View.GONE);

                if (mGroupEntity.getOwnStatus() != Constants.GroupUserOwnState.GROUP_JOINED) {
                    mViewBinging.groupBlock.setVisibility(View.VISIBLE);
                } else {
                    mViewBinging.chatMessageBar.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (mUserEntity != null) {

                UIHelper.setImageResource(mViewBinging.imageProfile, mUserEntity.avatarIndex);
                mViewBinging.textViewLastName.setText(mUserEntity.userName);
                String subTitle = mUserEntity.isOnline > 0 ? getString(R.string.active_online) : getString(R.string.active_offline);
                mViewBinging.textViewActiveNow.setText(subTitle);
                mViewBinging.imageView.setBackgroundResource(activeStatusResource(mUserEntity.getOnlineStatus()));

                mViewBinging.groupBlock.setVisibility(View.GONE);
                mViewBinging.chatMessageBar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void processGroupUsersComponent() {
        if (isGroup && mGroupEntity != null) {
            mChatViewModel.getGroupUsersById(mGroupEntity.membersInfo)
                    .observe(this, userEntities -> {
                        if (mChatPagedAdapter != null) {
                            mChatPagedAdapter.addAvatarIndex(userEntities);
                        }
                        String subTitle = CommonUtil.getGroupUsersName(userEntities);
                        mViewBinging.textViewActiveNow.setText(subTitle);
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Text.isNotEmpty(threadId)) {
            mChatViewModel.setCurrentUser(threadId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mChatViewModel.setCurrentUser(null);
    }


    /**
     * <h1>Init view model and recycler view</h1>
     * <p>Init adapter and listener</p>
     */
    private void initComponent() {
        mChatPagedAdapter = new ChatPagedAdapterRevised(this, mChatViewModel, this);
        mChatPagedAdapter.registerAdapterDataObserver(new AdapterDataSetObserver());

        mLinearLayoutManager = new LayoutManagerWithSmoothScroller(this);

        // to load messages from reverse order as in chat view
        mLinearLayoutManager.setStackFromEnd(true);

        if (mViewBinging != null) {

            RecyclerView.ItemAnimator itemAnimator = mViewBinging.chatRv.getItemAnimator();
            if (itemAnimator instanceof SimpleItemAnimator) {
                SimpleItemAnimator simpleItemAnimator = (SimpleItemAnimator) itemAnimator;
                simpleItemAnimator.setSupportsChangeAnimations(false);
            }

            mViewBinging.chatRv.setLayoutManager(mLinearLayoutManager);
            mViewBinging.chatRv.setAdapter(mChatPagedAdapter);

            setClickListener(mViewBinging.imageViewSend, mViewBinging.imageViewPickGalleryImage,
                    mViewBinging.imageProfile, mViewBinging.textViewLastName, mViewBinging.groupDeny,
                    mViewBinging.groupJoin);
        }

        clearNotification();
    }

    /**
     * Remove current user notification
     */
    private void clearNotification() {
        if (Text.isNotEmpty(threadId)) {
            int notificationId = Math.abs(threadId.hashCode());
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
    }

    /**
     * Using LiveData observer here we will observe the messages
     * from local db.
     * if any new message get inserted into db with the help of LifeData
     * here we can listen that message
     *
     * @param userId
     */
    private void subscribeForMessages(String userId) {
        if (Text.isNotEmpty(userId)) {

            if (mChatPagedAdapter != null) {
                mChatViewModel.getChatEntityWithDate().observe(ChatActivity.this, chatEntities -> {
                    controlEmptyView(chatEntities);
                    mChatPagedAdapter.submitList(chatEntities);
                });
            }

            if (Text.isNotEmpty(userId)) {
                if (isGroup) {
                    mChatViewModel.getAllGroupMessage(userId).observe(this, chatEntities -> {
                        mChatViewModel.prepareDateSpecificChat(chatEntities);
                    });
                } else {
                    mChatViewModel.getAllMessage(userId).observe(this, chatEntities -> {
                        mChatViewModel.prepareDateSpecificChat(chatEntities);
                    });
                }
            }
        }
    }

    private void subscribeForFinishEvent() {
        mChatViewModel.getFinishForGroupLeave().observe(this, aBoolean -> {
            finish();
        });
    }


    private void subscribeForThreadEvent(String threadId) {
        if (Text.isNotEmpty(threadId)) {

            if (isGroup) {
                mChatViewModel.getLiveGroupById(threadId).observe(this, groupEntity -> {
                    mGroupEntity = groupEntity;
                    if (groupEntity != null && mViewBinging != null) {

                        setUiComponent();
                        processGroupUsersComponent();
                    }
                });
            } else {
                mChatViewModel.getUserById(threadId).observe(this, userEntity -> {
                    mUserEntity = userEntity;
                    if (userEntity != null && mViewBinging != null) {

                        setUiComponent();

                        if (mChatPagedAdapter != null) {
                            mChatPagedAdapter.addAvatarIndex(userEntity);
                        }
                    }
                });
            }
        }
    }

    private int activeStatusResource(int userActiveStatus) {
        if (userActiveStatus == Constants.UserStatus.WIFI_ONLINE || userActiveStatus == Constants.UserStatus.WIFI_MESH_ONLINE || userActiveStatus == Constants.UserStatus.BLE_MESH_ONLINE || userActiveStatus == Constants.UserStatus.BLE_ONLINE) {
            return R.mipmap.ic_mesh_online;
        } else if (userActiveStatus == Constants.UserStatus.HB_ONLINE || userActiveStatus == Constants.UserStatus.HB_MESH_ONLINE) {
            return R.mipmap.ic_hb_online;
        } else if (userActiveStatus == Constants.UserStatus.INTERNET_ONLINE) {
            return R.mipmap.ic_internet;
        } else {
            return R.mipmap.ic_offline;
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_send:
                if (mViewBinging != null) {
                    String value = mViewBinging.editTextMessage.getText().toString().trim();
                    if (!TextUtils.isEmpty(value) && mUserEntity != null && mUserEntity.meshId != null) {
                        mChatViewModel.sendMessage(mUserEntity.meshId, value, true);
                        mViewBinging.editTextMessage.setText("");
                    }
                }
                break;
            case R.id.image_profile:
            case R.id.text_view_last_name:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(UserEntity.class.getName(), mUserEntity);
                startActivity(intent);
                break;

            case R.id.image_view_pick_gallery_image:
                if(mUserEntity != null && mUserEntity.getOnlineStatus() != Constants.UserStatus.INTERNET_ONLINE) {
                    requestToOpenGallery();
                }
                break;

            case R.id.image_view_message:
            case R.id.shimmer_incoming_loading:
            case R.id.hover:
            case R.id.hover_view:
//            case R.id.shimmerUploadingImage:
                MessageEntity messageEntity = (MessageEntity) view.getTag(R.id.image_view_message);
                if (messageEntity != null) {
                    viewContent(view, messageEntity);
                }
                break;

            case R.id.view_failed:
                MessageEntity failedMessage = (MessageEntity) view.getTag(R.id.image_view_message);
                if (failedMessage != null &&
                        (failedMessage.getStatus() == Constants.MessageStatus.STATUS_FAILED
                        || failedMessage.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED)) {
                    resendFailedMessage(failedMessage);
                }
                break;

            case R.id.group_join:
                mChatViewModel.groupJoinAction(mGroupEntity);
                break;

            case R.id.group_deny:
                mChatViewModel.groupLeaveAction(mGroupEntity);
                break;
        }
    }

    private void resendFailedMessage(MessageEntity failedMessage) {
        if (mUserEntity.getOnlineStatus() == Constants.UserStatus.OFFLINE) {
            Toaster.showShort(mUserEntity.getUserName() + " is in offline.");
            return;
        } else if (mUserEntity.getOnlineStatus() == Constants.UserStatus.INTERNET_ONLINE) {
            Toaster.showShort(mUserEntity.getUserName() + " locally not connected.");
            return;
        }
        mChatViewModel.resendContentMessage(failedMessage);
    }

    private void controlEmptyView(List<ChatEntity> chatEntities) {
        if (chatEntities != null && chatEntities.size() > 0) {
            if (mViewBinging != null) {
                mViewBinging.emptyLayout.setVisibility(View.GONE);
            }
        } else {
            if (mViewBinging != null) {
                mViewBinging.emptyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home && isTaskRoot()) {
            chatFinishAndStartApp();
        }
        return super.onOptionsItemSelected(item);
    }

    public void chatFinishAndStartApp() {
        Intent newTask = new Intent(this, MainActivity.class);
        newTask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newTask);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private ChatViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

                return (T) ServiceLocator.getInstance().getChatViewModel(getApplication());
            }
        }).get(ChatViewModel.class);
    }

    protected void requestToOpenGallery() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            openGallery();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestToOpenGallery();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> requestToOpenGallery()).onSameThread().check();
    }

    private void openGallery() {
        int selectImageCount = 1;
        Matisse.from(this)
                .choose(MimeType.ofAll(), false)
                .theme(R.style.Matisse_Dracula)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.height_120))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .maxSelectable(selectImageCount)
                .imageEngine(new MyGlideEngineUtil())
                .forResult(Constants.RequestCodes.GALLERY_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.RequestCodes.GALLERY_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    List<Uri> images = Matisse.obtainResult(data);
                    mChatViewModel.sendContentMessage(mUserEntity.meshId, images.get(0));
                }
                break;
        }
    }

    class AdapterDataSetObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            Timber.e("onChanged");
        }

        // Scroll to bottom on new messages
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            Timber.e("onItemRangeInserted");
            //mViewBinging.chatRv.smoothScrollToPosition(mChatPagedAdapter.getItemCount()-1 );
            if (mLinearLayoutManager != null && mViewBinging != null && mChatPagedAdapter != null) {
                mLinearLayoutManager.smoothScrollToPosition(mViewBinging.chatRv,
                        null, mChatPagedAdapter.getItemCount());
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) { }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) { }
    }

    private Animator currentAnimator;
    private long shortAnimationDuration = 300;
    private boolean isExpandCancel = false;

    private void viewContent(View messageImage, MessageEntity messageEntity) {
        if (messageEntity.isIncoming()) {
            if (messageEntity.getContentStatus() != Constants.ContentStatus.CONTENT_STATUS_RECEIVED
                    && messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED) {
                Toaster.showShort("Message was failed");
                return;
            }

            if (messageEntity.getContentStatus() == Constants.ContentStatus.CONTENT_STATUS_RECEIVING) {
                Toaster.showShort("Message is receiving");
                return;
            }
        }

        String contentPath = messageEntity.getContentPath();
        if (TextUtils.isEmpty(contentPath)) {
            Toaster.showShort("Error message");
            return;
        }

        if (!(new File(contentPath).exists())) {
            Toaster.showShort("File not found");
            return;
        }

        if (ContentUtil.getInstance().isTypeImage(contentPath)) {
            zoomImageFromThumb(messageImage, contentPath);
        } else if (ContentUtil.getInstance().isTypeVideo(contentPath)) {
            openVideo(contentPath);
        }
    }

    private void openVideo(String videoPath) {
        try {

            File destinationFile = new File(videoPath);

            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String packageName = getPackageName() + ".provider";
                Uri apkUri = FileProvider.getUriForFile(this, packageName, destinationFile);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "video/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                Uri apkUri = Uri.fromFile(destinationFile);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "video/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zoomImageFromThumb(final View thumbView, String imagePath) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        UIHelper.setImageInGlide(mViewBinging.expandedImage, imagePath);

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
        mViewBinging.expandedImage.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        mViewBinging.expandedImage.setPivotX(0f);
        mViewBinging.expandedImage.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(mViewBinging.expandedImage, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(mViewBinging.expandedImage, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(mViewBinging.expandedImage, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(mViewBinging.expandedImage,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isExpandCancel) {
                    mViewBinging.expandImageBack.setVisibility(View.VISIBLE);
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
        mViewBinging.expandedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewBinging.expandImageBack.setVisibility(View.GONE);
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(mViewBinging.expandedImage, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(mViewBinging.expandedImage,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(mViewBinging.expandedImage,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(mViewBinging.expandedImage,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        mViewBinging.expandedImage.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        mViewBinging.expandedImage.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}
