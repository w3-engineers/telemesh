package com.w3engineers.unicef.telemesh.ui.chat;

import android.app.NotificationManager;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.w3engineers.ext.strom.util.Text;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.LayoutManagerWithSmoothScroller;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityChatRevisedBinding;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;

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
    private String userId;
    //private ChatAdapter mChatAdapter;
    //private  ChatPagedAdapter mChatPagedAdapter;
    @Nullable
    public ChatPagedAdapterRevised mChatPagedAdapter;
    //private ActivityChatBinding mViewBinging;
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
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
    }

    @Override
    protected void startUI() {
        Intent intent = getIntent();
        userId = intent.getStringExtra(UserEntity.class.getName());

        if (TextUtils.isEmpty(userId)) {
            finish();
            return;
        }

        mViewBinging = (ActivityChatRevisedBinding) getViewDataBinding();
        setTitle("");

        mChatViewModel = getViewModel();

        initComponent();
        subscribeForMessages(userId);
        subscribeForUserEvent(userId);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (mViewBinging != null) {
            mViewBinging.imageProfile.setOnClickListener(this);
            mViewBinging.textViewLastName.setOnClickListener(this);

            mViewBinging.setUserEntity(mUserEntity);
            //mViewBinging.imageView.setBackgroundResource(activeStatusResource(mUserEntity.getOnlineStatus()));
        }


        if (Text.isNotEmpty(userId)) {
            mChatViewModel.updateAllMessageStatus(userId);
        }

        int myMode = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readInt(Constants.preferenceKey.MY_MODE);
        BulletinTimeScheduler.getInstance().initNoInternetCallback(isMobileDataOn -> showHideInternetWarning(myMode, isMobileDataOn));
        showHideInternetWarning(myMode, Constants.IS_DATA_ON);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Text.isNotEmpty(userId)) {
            mChatViewModel.setCurrentUser(userId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mChatViewModel.setCurrentUser(null);
    }


    /**
     * <h1>Init view model and recycler view</h1>
     * <p>Init adapter and listener</p>
     */
    private void initComponent() {


        mChatPagedAdapter = new ChatPagedAdapterRevised(this, mChatViewModel);
        mChatPagedAdapter.registerAdapterDataObserver(new AdapterDataSetObserver());


        mLinearLayoutManager = new LayoutManagerWithSmoothScroller(this);

        // to load messages from reverse order as in chat view
        mLinearLayoutManager.setStackFromEnd(true);

        if (mViewBinging != null) {
            mViewBinging.chatRv.setLayoutManager(mLinearLayoutManager);
            mViewBinging.chatRv.setAdapter(mChatPagedAdapter);
            //  mViewBinging.emptyViewId.setOnClickListener(this);
            mViewBinging.imageViewSend.setOnClickListener(this);
        }

        clearNotification();
    }

    /**
     * Remove current user notification
     */
    private void clearNotification() {
        if (Text.isNotEmpty(userId)) {
            int notificationId = Math.abs(userId.hashCode());
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
            /*mChatViewModel.getAllMessage(mUserEntity.meshId).observe(this, chatEntities -> {

                if (chatEntities == null)
                    return;

//                Collections.reverse(chatEntities);
                mChatAdapter.addItems(chatEntities);
                mViewBinging.chatRv.scrollToPosition(mChatAdapter.getItemCount() - 1);
            });*/

            if (mChatPagedAdapter != null) {
                mChatViewModel.getChatEntityWithDate().observe(ChatActivity.this, chatEntities -> {
                    controlEmptyView(chatEntities);
                    mChatPagedAdapter.submitList(chatEntities);
                });
            }

            if (Text.isNotEmpty(userId)) {
                mChatViewModel.getAllMessage(userId).observe(this, chatEntities -> {
                    mChatViewModel.prepareDateSpecificChat(chatEntities);
                });
            }

        }
    }


    private void subscribeForUserEvent(String userId) {
        if (Text.isNotEmpty(userId)) {

            mChatViewModel.getUserById(userId).observe(this, userEntity -> {
                mUserEntity = userEntity;
                if (userEntity != null && mViewBinging != null) {
                    mViewBinging.setUserEntity(userEntity);
                    mViewBinging.imageView.setBackgroundResource(activeStatusResource(userEntity.getOnlineStatus()));

                    if (mChatPagedAdapter != null) {
                        mChatPagedAdapter.addAvatarIndex(mUserEntity.getAvatarIndex());
                        Log.d("UiTest", "Avatar sending process: " + mUserEntity.getAvatarIndex());
                    }
                }
            });
        }
    }

    private int activeStatusResource(int userActiveStatus) {

        if (userActiveStatus == Constants.UserStatus.WIFI_ONLINE || userActiveStatus == Constants.UserStatus.WIFI_MESH_ONLINE) {
            return R.mipmap.ic_mesh_online;
        } else if (userActiveStatus == Constants.UserStatus.BLE_MESH_ONLINE || userActiveStatus == Constants.UserStatus.BLE_ONLINE) {
            return R.mipmap.ic_mesh_online;
        } else if (userActiveStatus == Constants.UserStatus.INTERNET_ONLINE) {
            return R.mipmap.ic_internet;
        } else {
            return R.mipmap.ic_offline;
        }

        /*if (userActiveStatus == Constants.UserStatus.WIFI_ONLINE || userActiveStatus == Constants.UserStatus.BLE_ONLINE) {
            return R.drawable.circle_online;
        } else if (userActiveStatus == Constants.UserStatus.INTERNET_ONLINE) {
            return R.drawable.circle_internet;
        } else {
            return R.drawable.circle_offline;
        }*/
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
        }

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
        int id = item.getItemId();

        if (id == android.R.id.home) {
            loadMainActivity();
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loadMainActivity() {
        // When user comes in chat screen using notification
        if (isTaskRoot()) {
            Intent newTask = new Intent(this, MainActivity.class);
            newTask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newTask);
        }
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

    private void showHideInternetWarning(int myMode, boolean isMobileDataOn) {
        if (myMode == Constants.INTERNET_ONLY || myMode == Constants.SELLER_MODE) {
            if (isMobileDataOn) {
                mViewBinging.textViewNoInternet.setVisibility(View.GONE);
            } else {
                mViewBinging.textViewNoInternet.setVisibility(View.VISIBLE);
            }
        } else {
            mViewBinging.textViewNoInternet.setVisibility(View.GONE);
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
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }
    }
}
