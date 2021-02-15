package com.w3engineers.unicef.telemesh.ui.addnewmember;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.ItemClickListener;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityAddNewMemberBinding;
import com.w3engineers.unicef.telemesh.ui.groupcreate.SelectedUserAdapter;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class AddNewMemberActivity extends TelemeshBaseActivity implements
        AddNewMemberAdapter.ItemChangeListener, ItemClickListener<UserEntity> {

    private ActivityAddNewMemberBinding mBinding;
    private AddNewMemberViewModel mViewModel;
    private AddNewMemberAdapter mMemberAdapter;
    private SelectedUserAdapter mSelectedUserAdapter;

    private String mGroupId;

    private GroupEntity mGroupEntity;
    private List<String> mGroupMemberList;

    public MenuItem mSearchItem;

    @Nullable
    public List<UserEntity> userEntityList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_new_member;
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
    protected int getMenuId() {
        return R.menu.menu_search_contact;
    }

    @Override
    public void startUI() {
        mBinding = (ActivityAddNewMemberBinding) getViewDataBinding();
        mViewModel = getViewModel();
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
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.button_go:
                mViewModel.addMembersInGroup(mGroupEntity, mMemberAdapter.getSelectedUserList());
                break;
            case R.id.image_view_cross:
                if (TextUtils.isEmpty(mBinding.searchBar.editTextSearch.getText())) {
                    hideSearchBar();
                } else {
                    mBinding.searchBar.editTextSearch.setText("");
                }
                break;
            case R.id.image_view_back:
                mBinding.searchBar.editTextSearch.setText("");
                hideSearchBar();
                break;
        }
    }

    @Override
    public void onGetChangedItem(boolean isAdd, UserEntity userEntity) {
        if (!mMemberAdapter.getSelectedUserList().isEmpty()) {
            mBinding.buttonGo.show();
            mBinding.cardViewSelectedItem.setVisibility(View.VISIBLE);
        } else {
            mBinding.buttonGo.hide();
            clearSelectedUserAdapter();
        }

        if (isAdd) {
            mSelectedUserAdapter.addItem(userEntity);
        } else {
            mSelectedUserAdapter.removeItem(userEntity);
        }
    }

    @Override
    public void onItemClick(View view, UserEntity item) {
        if (view.getId() == R.id.button_remove) {
            mSelectedUserAdapter.removeItem(item);

            mMemberAdapter.deselectUser(item);

            if (mMemberAdapter.getSelectedUserList().isEmpty()) {
                mBinding.cardViewSelectedItem.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchItem = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            showSearchBar();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mBinding.searchBar.getRoot().getVisibility() == View.VISIBLE) {
            mBinding.searchBar.editTextSearch.setText("");
            hideSearchBar();
            return;
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {

        setClickListener(mBinding.buttonGo,
                mBinding.searchBar.imageViewBack,
                mBinding.searchBar.imageViewCross);

        setTitle(LanguageUtil.getString(R.string.add_new_member));

        mBinding.recyclerViewUser.setHasFixedSize(true);
        mMemberAdapter = new AddNewMemberAdapter(this, this);
        mBinding.recyclerViewUser.setAdapter(mMemberAdapter);

        mSelectedUserAdapter = new SelectedUserAdapter();
        mSelectedUserAdapter.setItemClickListener(this);
        mBinding.recyclerViewSelectedUser.setHasFixedSize(true);
        mBinding.recyclerViewSelectedUser.setAdapter(mSelectedUserAdapter);

        parseIntent();

        initSearchListener();

        groupDataObserver();

        userDataOperation();
    }

    private void groupDataObserver() {
        if (mGroupId == null) {
            finish();
            return;
        }

        mViewModel.getLiveGroupById(mGroupId).observe(this, groupEntity -> {
            if (groupEntity != null) {
                mGroupEntity = groupEntity;
                generateMemberList(GsonBuilder.getInstance()
                        .getGroupMemberInfoObj(groupEntity.getMembersInfo()));
                mViewModel.startUserObserver(mGroupMemberList);
            }
        });

        mViewModel.groupUserList.observe(this, groupEntity -> {
            if (groupEntity != null) {
                finish();
            }
        });

    }

    private void userDataOperation() {

        mViewModel.nearbyUsers.observe(this, userEntities -> {
            if (userEntities != null) {
                mMemberAdapter.submitList(userEntities);
                userEntityList = userEntities;

                updateSelectedAdapterItem(userEntityList);

                if (userEntityList != null && userEntityList.size() > 0) {
                    mBinding.emptyLayout.setVisibility(View.GONE);
                    if (mSearchItem != null) {
                        mSearchItem.setVisible(true);
                    }
                } else {
                    mBinding.emptyLayout.setVisibility(View.VISIBLE);
                    if (mSearchItem != null) {
                        mSearchItem.setVisible(false);
                    }
                }
            }
        });

        mViewModel.filterUserList.observe(this, userEntities -> {
            mMemberAdapter.submitList(userEntities);

            updateSelectedAdapterItem(userEntities);

            if (userEntities != null && userEntities.size() > 0) {
                mBinding.emptyLayout.setVisibility(View.GONE);
            } else {
                mBinding.emptyLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateSelectedAdapterItem(List<UserEntity> totalUserList) {
        if (mSelectedUserAdapter.getItemCount() > 0
                && userEntityList != null
                && userEntityList.size() > 0) {

            for (UserEntity userEntity : mSelectedUserAdapter.getItems()) {
                UserEntity updatedUser = getSelectedUser(userEntity.getMeshId(), totalUserList);
                if (updatedUser != null) {
                    userEntity.setUserName(updatedUser.getUserName());
                    userEntity.setAvatarIndex(updatedUser.getAvatarIndex());
                }
            }
            mSelectedUserAdapter.notifyDataSetChanged();
        }
    }

    private UserEntity getSelectedUser(String userId, List<UserEntity> totalUserList) {
        if (totalUserList != null) {
            for (UserEntity userEntity : totalUserList) {
                if (userEntity.getMeshId().equals(userId)) {
                    return userEntity;
                }
            }
        }
        return null;
    }

    private void generateMemberList(List<GroupMembersInfo> groupAdminInfoList) {
        mGroupMemberList = new ArrayList<>();
        for (GroupMembersInfo groupMembersInfo : groupAdminInfoList) {
            if (groupMembersInfo.getMemberStatus() == Constants.GroupEvent.GROUP_JOINED) {
                mGroupMemberList.add(groupMembersInfo.getMemberId());
            }
        }
    }

    private void clearSelectedUserAdapter() {
        mBinding.cardViewSelectedItem.setVisibility(View.GONE);
        mSelectedUserAdapter.clear();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(GroupEntity.class.getName())) {
            mGroupId = intent.getStringExtra(GroupEntity.class.getName());
        }
    }

    public void showSearchBar() {
        mBinding.mainToolbar.setVisibility(View.INVISIBLE);
        mBinding.searchBar.getRoot().setVisibility(View.VISIBLE);

        mBinding.searchBar.editTextSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mBinding.searchBar.editTextSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideSearchBar() {
        mBinding.mainToolbar.setVisibility(View.VISIBLE);
        mBinding.searchBar.getRoot().setVisibility(View.INVISIBLE);
        UIHelper.hideKeyboardFrom(this, mBinding.searchBar.editTextSearch);
    }

    private void initSearchListener() {

        mBinding.searchBar.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.startSearch(s.toString(), mViewModel.getCurrentUserList());
            }
        });
    }

    private AddNewMemberViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getAddNewMemberViewModel(getApplication());
            }
        }).get(AddNewMemberViewModel.class);
    }

}