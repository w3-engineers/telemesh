package com.w3engineers.unicef.telemesh.ui.groupcreate;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityGroupCreateBinding;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.util.base.ui.BaseServiceLocator;
import com.w3engineers.unicef.util.base.ui.ItemClickListener;
import com.w3engineers.unicef.util.base.ui.TelemeshBaseActivity;
import com.w3engineers.unicef.util.helper.LanguageUtil;

import java.util.List;


public class GroupCreateActivity extends TelemeshBaseActivity implements
        GroupCreateAdapter.ItemChangeListener, ItemClickListener<UserEntity> {

    private ActivityGroupCreateBinding mBinding;
    private GroupCreateViewModel mViewModel;
    private GroupCreateAdapter mGroupCreateAdapter;
    private SelectedUserAdapter mSelectedUserAdapter;

    public static boolean IS_NEW_GROUP_CREATED;

    @Nullable
    public List<UserEntity> userEntityList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    public void startUI() {
        mBinding = (ActivityGroupCreateBinding) getViewDataBinding();
        mViewModel = getViewModel();

        IS_NEW_GROUP_CREATED = false;

        initView();
    }

    @Override
    protected void stopUI() {

    }

    @Override
    public BaseServiceLocator a() {
        return null;
    }

    @Override
    public void onBackPressed() {
      /*  if (mGroupCreateAdapter != null) {
            if (mGroupCreateAdapter.isSelectionEnable()) {
                showOrHideGroupCreateView(false);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }*/

        super.onBackPressed();

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_create_group:
            case R.id.text_view_create_group:
                if (mGroupCreateAdapter != null) {
                    showOrHideGroupCreateView(true);
                }
                break;
            case R.id.button_go:
                List<UserEntity> userEntities = mGroupCreateAdapter.getSelectedUserList();
                if (userEntities.size() == 1) {
                    openUserMessage(mGroupCreateAdapter.getSelectedUserList().get(0));
                    showOrHideGroupCreateView(false);
                } else {
                    HandlerUtil.postBackground(()->{
                        GroupEntity groupEntity= mViewModel.createGroup(userEntities);
                        HandlerUtil.postForeground(()->{
                            showOrHideGroupCreateView(false);
                            if (groupEntity != null) {
                                Intent intent = new Intent(GroupCreateActivity.this, ChatActivity.class);
                                intent.putExtra(UserEntity.class.getName(), groupEntity.getGroupId());
                                intent.putExtra(GroupEntity.class.getName(), true);
                                startActivity(intent);
                                IS_NEW_GROUP_CREATED = true;
                                finish();
                            }
                        });
                    });
                }
                break;
        }
    }

    @Override
    public void onGetChangedItem(boolean isAdd, UserEntity userEntity) {
        // May be isAdd variable not need but in future we need it


        if (mGroupCreateAdapter.isSelectionEnable()) {

            if (!mGroupCreateAdapter.getSelectedUserList().isEmpty()) {
                mBinding.buttonGo.show();
                mBinding.cardViewSelectedItem.setVisibility(View.VISIBLE);
                setTitle(LanguageUtil.getString(R.string.new_group));
            } else {
                mBinding.buttonGo.hide();
                clearSelectedUserAdapter();
                setTitle(LanguageUtil.getString(R.string.new_chat));
            }

            if (isAdd) {
                mSelectedUserAdapter.addItem(userEntity);
            } else {
                mSelectedUserAdapter.removeItem(userEntity);
            }
        } else {
            openUserMessage(userEntity);
        }
    }

    /**
     * This section is used only for selection adapter
     *
     * @param view The view that was clicked.
     * @param item The T type object that was clicked.
     */
    @Override
    public void onItemClick(View view, UserEntity item) {
        if (view.getId() == R.id.button_remove) {
            mSelectedUserAdapter.removeItem(item);

            mGroupCreateAdapter.deselectUser(item);

            if (mGroupCreateAdapter.getSelectedUserList().isEmpty()) {
                mBinding.cardViewSelectedItem.setVisibility(View.GONE);
            }
        }
    }

    private void initView() {
        setClickListener(mBinding.imageViewCreateGroup, mBinding.textViewCreateGroup, mBinding.buttonGo);

        mBinding.recyclerViewUser.setHasFixedSize(true);
        mBinding.recyclerViewUser.setItemAnimator(null);
        mGroupCreateAdapter = new GroupCreateAdapter(this, this);
        mBinding.recyclerViewUser.setAdapter(mGroupCreateAdapter);

        mSelectedUserAdapter = new SelectedUserAdapter();
        mSelectedUserAdapter.setItemClickListener(this);
        mBinding.recyclerViewSelectedUser.setHasFixedSize(true);
        mBinding.recyclerViewSelectedUser.setAdapter(mSelectedUserAdapter);

        setTitle(LanguageUtil.getString(R.string.new_chat));

        showOrHideGroupCreateView(true);

        userDataOperation();

        observeGroupCreation();
    }


    private void userDataOperation() {

        if (mViewModel != null) {

            mViewModel.startUserObserver();

            mViewModel.nearbyUsers.observe(this, userEntities -> {
                if (userEntities != null) {
                    mGroupCreateAdapter.submitList(userEntities);
                    userEntityList = userEntities;

                    updateSelectedAdapterItem();

                    if (userEntityList != null && userEntityList.size() > 0) {
                        //Todo we can hide create group title
                    } else {
                        //Todo show create group title
                    }
                }
            });

        }
    }

    private void updateSelectedAdapterItem() {
        if (mSelectedUserAdapter.getItemCount() > 0
                && userEntityList != null
                && userEntityList.size() > 0) {

            for (UserEntity userEntity : mSelectedUserAdapter.getItems()) {
                UserEntity updatedUser = getSelectedUser(userEntity.getMeshId());
                if (updatedUser != null) {
                    userEntity.setUserName(updatedUser.getUserName());
                    userEntity.setUserLastName(updatedUser.getUserLastName());
                    userEntity.setAvatarIndex(updatedUser.getAvatarIndex());
                }
            }
            mSelectedUserAdapter.notifyDataSetChanged();
        }
    }

    private UserEntity getSelectedUser(String userId) {
        if (userEntityList != null) {
            for (UserEntity userEntity : userEntityList) {
                if (userEntity.getMeshId().equals(userId)) {
                    return userEntity;
                }
            }
        }
        return null;
    }

    private void observeGroupCreation() {
        if (mViewModel != null) {
            mViewModel.groupUserList.observe(this, entity -> {
                if (entity != null) {
                    Intent intent = new Intent(GroupCreateActivity.this, ChatActivity.class);
                    intent.putExtra(UserEntity.class.getName(), entity.getGroupId());
                    intent.putExtra(GroupEntity.class.getName(), true);
                    startActivity(intent);

                    IS_NEW_GROUP_CREATED = true;

                    finish();
                }
            });
        }
    }

    private void openUserMessage(UserEntity entity) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(UserEntity.class.getName(), entity.meshId);
        startActivity(intent);
    }

    private GroupCreateViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getGroupCreateViewModel(getApplication());
            }
        }).get(GroupCreateViewModel.class);
    }

    private void showOrHideGroupCreateView(boolean isSelectionEnable) {
        mGroupCreateAdapter.setSelectionEnable(isSelectionEnable);
        mGroupCreateAdapter.notifyDataSetChanged();

        if (isSelectionEnable) {
            mBinding.imageViewCreateGroup.setVisibility(View.GONE);
            mBinding.textViewCreateGroup.setVisibility(View.GONE);
        } else {
            mBinding.imageViewCreateGroup.setVisibility(View.VISIBLE);
            mBinding.textViewCreateGroup.setVisibility(View.VISIBLE);

            mBinding.buttonGo.hide();
            clearSelectedUserAdapter();

            setTitle(LanguageUtil.getString(R.string.new_chat));
        }

    }

    private void clearSelectedUserAdapter() {
        mBinding.cardViewSelectedItem.setVisibility(View.GONE);
        mSelectedUserAdapter.clear();
    }

}