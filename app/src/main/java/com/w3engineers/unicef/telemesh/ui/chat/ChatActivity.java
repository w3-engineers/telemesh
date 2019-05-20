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

import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.ui.base.rm.RmBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityChatRevisedBinding;
import com.w3engineers.unicef.telemesh.pager.LayoutManagerWithSmoothScroller;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;

import java.util.List;

import timber.log.Timber;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class ChatActivity extends RmBaseActivity implements ItemClickListener<ChatEntity> {
    /**
     * <h1>Instance variable scope</h1>
     */
    private ChatViewModel mChatViewModel;
    private UserEntity mUserEntity;
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
    protected void startUI() {
        Intent intent = getIntent();
        mUserEntity = intent.getParcelableExtra(UserEntity.class.getName());



        mViewBinging = (ActivityChatRevisedBinding) getViewDataBinding();
        setTitle("");
        initComponent();
        subscribeForMessages();
        subscribeForUserEvent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (mViewBinging != null) {
            mViewBinging.imageProfile.setOnClickListener(this);
            mViewBinging.textViewLastName.setOnClickListener(this);

            mViewBinging.setUserEntity(mUserEntity);
        }


        if (mUserEntity != null && mUserEntity.meshId != null) {
            mChatViewModel.updateAllMessageStatus(mUserEntity.meshId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserEntity != null) {
            mChatViewModel.setCurrentUser(mUserEntity.meshId);
        }

    }

    @NonNull
    @Override
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
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


        mChatPagedAdapter = new ChatPagedAdapterRevised(this);
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

        mChatViewModel = getViewModel();

        clearNotification();
    }

    /**
     * Remove current user notification
     */
    private void clearNotification() {
        if (mUserEntity.meshId != null) {
            int notificationId = Math.abs(mUserEntity.meshId.hashCode());
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
    }

    /**
     * Using LiveData observer here we will observe the messages
     * from local db.
     * if any new message get inserted into db with the help of LifeData
     * here we can listen that message
     */
    private void subscribeForMessages() {
        if (mUserEntity != null) {
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

            if (mUserEntity.meshId != null) {
                mChatViewModel.getAllMessage(mUserEntity.meshId).observe(this, chatEntities -> {
                        mChatViewModel.prepareDateSpecificChat(chatEntities);
                });
            }

        }
    }


    private void subscribeForUserEvent() {
        if (mUserEntity != null && mUserEntity.meshId != null) {

            mChatViewModel.getUserById(mUserEntity.meshId).observe(this, userEntity -> {
                mUserEntity = userEntity;
                if (userEntity != null && mViewBinging != null)
                    mViewBinging.setUserEntity(userEntity);
            });
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_send:
                if (mViewBinging != null) {
                    String value = mViewBinging.editTextMessage.getText().toString().trim();
                    if (!TextUtils.isEmpty(value) && mUserEntity.meshId != null) {
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

    @Override
    public void onItemClick(@NonNull View view, @Nullable ChatEntity item) {
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
