package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.mesh.util.MeshLog;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentMeshcontactBinding;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.conversations.ConversationAdapter;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MeshContactsFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private FragmentMeshcontactBinding fragmentMeshcontactBinding;
    @Nullable
    public MeshContactViewModel meshContactViewModel;
    @Nullable
    public List<UserEntity> userEntityList;
    @Nullable
    public MenuItem mSearchItem;
    private String title;
    private boolean isLoaded = false;
    private SearchView mSearchView;

    private PagedList<GroupEntity> groupList;
    private PagedList<UserEntity> favouriteList;
    private PagedList<UserEntity> msgWithFavList;
    private int currentSelection = 0;


    Handler loaderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meshcontact;
    }

    @Override
    protected void startUI() {
        fragmentMeshcontactBinding = (FragmentMeshcontactBinding)
                getViewDataBinding();

        setHasOptionsMenu(true);
        // controlEmptyLayout();
        title = LanguageUtil.getString(R.string.title_personal_fragment);
        setTitle(title);

        init();
        initSpinner();

        userDataOperation();

        openUserMessage();

        changeFavouriteStatus();

    }

    private void userDataOperation() {
        if (meshContactViewModel != null) {

            initDataObserver();

            meshContactViewModel.startGroupObserver();
            meshContactViewModel.startFavouriteObserver();
            meshContactViewModel.startAllMessagedWithFavouriteObserver();

            meshContactViewModel.getGetFilteredList().observe(this, userEntities -> {

                setTitle(LanguageUtil.getString(R.string.title_personal_fragment));
                if (userEntities != null && userEntities.size() > 0) {
                    controlEmpty(false);
                    getAdapter().submitList(userEntities);

                    isLoaded = false;

                } else {
                    if (!isLoaded) {
                        controlEmpty(false);

                        isLoaded = true;
                        Runnable runnable = () -> {
                            fragmentMeshcontactBinding.tvMessage.setText("No User Found");
                            controlEmpty(true);
                        };
                        loaderHandler.postDelayed(runnable, Constants.AppConstant.LOADING_TIME_SHORT);
                    }
                }
            });

            meshContactViewModel.backUserEntity.observe(this, userEntities -> {
                userEntityList = userEntities;
            });
        }
    }

    private void openUserMessage() {
        if (meshContactViewModel != null) {
            meshContactViewModel.openUserMessage().observe(this, userEntity -> {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideSearchBar();

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(UserEntity.class.getName(), userEntity.meshId);
                    intent.putExtra(GroupEntity.class.getName(), false);
                    startActivity(intent);
                }
            });

            meshContactViewModel.openGroupMessage().observe(this, groupEntity -> {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideSearchBar();

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(UserEntity.class.getName(), groupEntity.getGroupId());
                    intent.putExtra(GroupEntity.class.getName(), true);
                    startActivity(intent);
                }
            });
        }
    }

    private void changeFavouriteStatus() {
        if (meshContactViewModel != null) {
            meshContactViewModel.changeFavourite().observe(this, userEntity -> {
                if (userEntity.getIsFavourite() == Constants.FavouriteStatus.UNFAVOURITE) {
                    meshContactViewModel.updateFavouriteStatus(userEntity.getMeshId(), Constants.FavouriteStatus.FAVOURITE);
                } else if (userEntity.getIsFavourite() == Constants.FavouriteStatus.FAVOURITE) {
                    meshContactViewModel.updateFavouriteStatus(userEntity.getMeshId(), Constants.FavouriteStatus.UNFAVOURITE);
                }
            });
        }
    }

    public void searchContacts(String query) {
        if (meshContactViewModel != null) {
            Timber.d("Search query: %s", query);
            meshContactViewModel.startSearch(query, meshContactViewModel.getCurrentUserList(currentSelection));
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        if (getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.menu_search_contact, menu);

            mSearchItem = menu.findItem(R.id.action_search);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).showSearchBar();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchViewControl(List<UserEntity> userEntities) {
        boolean isSearchVisible = userEntities != null && userEntities.size() > 0;
        mSearchItem.setVisible(isSearchVisible);

        if (isSearchVisible) {
            setTitle(LanguageUtil.getString(R.string.title_personal_fragment));
        }
    }

    private void controlList(boolean isGroup) {
        fragmentMeshcontactBinding.contactRecyclerView.setVisibility(View.GONE);
        fragmentMeshcontactBinding.groupRecyclerView.setVisibility(View.GONE);
        if (isGroup) {
            fragmentMeshcontactBinding.groupRecyclerView.setVisibility(View.VISIBLE);
        } else {
            fragmentMeshcontactBinding.contactRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void controlEmpty(boolean isShow) {
        if (isShow) {
            fragmentMeshcontactBinding.emptyLayout.setVisibility(View.VISIBLE);
        } else {
            fragmentMeshcontactBinding.emptyLayout.setVisibility(View.GONE);
        }
    }

    private void initSpinner() {
        fragmentMeshcontactBinding.spinnerView.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add("Group");
        categories.add("Favourite");
        categories.add("All");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.simple_spinner_item, categories);

        fragmentMeshcontactBinding.spinnerView.setAdapter(spinnerAdapter);
    }

    // General API's and initialization area
    private void init() {
        fragmentMeshcontactBinding.tvMessage.setText(LanguageUtil.getString(R.string.no_favorite));
        meshContactViewModel = getViewModel();

        fragmentMeshcontactBinding.contactRecyclerView.setItemAnimator(null);
        fragmentMeshcontactBinding.contactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MeshContactAdapter meshContactAdapter = new MeshContactAdapter(meshContactViewModel);
        fragmentMeshcontactBinding.contactRecyclerView.setAdapter(meshContactAdapter);

        fragmentMeshcontactBinding.groupRecyclerView.setItemAnimator(null);
        fragmentMeshcontactBinding.groupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ConversationAdapter conversationAdapter = new ConversationAdapter(meshContactViewModel);
        fragmentMeshcontactBinding.groupRecyclerView.setAdapter(conversationAdapter);
    }

    private MeshContactAdapter getAdapter() {
        return (MeshContactAdapter) fragmentMeshcontactBinding
                .contactRecyclerView.getAdapter();
    }

    private ConversationAdapter getConversationAdapter() {
        return (ConversationAdapter) fragmentMeshcontactBinding
                .groupRecyclerView.getAdapter();
    }

    private MeshContactViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getMeshContactViewModel(getActivity().getApplication());
            }
        }).get(MeshContactViewModel.class);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentSelection = position;
        if (position == Constants.SpinnerItem.GROUP) {
            controlList(true);
            updateGroupAdapterData(groupList);
        } else if (position == Constants.SpinnerItem.FAVOURITE) {
            controlList(false);
            updateAdapterByData(favouriteList);
        } else if (position == Constants.SpinnerItem.ALL) {
            controlList(false);
            updateAdapterByData(msgWithFavList);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initDataObserver() {
        if (meshContactViewModel != null) {

            meshContactViewModel.groupEntityList.observe(this, groupEntities -> {
                if (groupEntities != null) {
                    groupList = groupEntities;
                    updateGroupAdapterData(groupEntities);
                }
            });

            meshContactViewModel.favoriteEntityList.observe(this, userEntities -> {
                if (userEntities != null) {
                    isLoaded = false;
                    favouriteList = userEntities;

                    if (currentSelection == Constants.SpinnerItem.FAVOURITE) {
                        updateAdapterByData(userEntities);
                    }
                }
                if (mSearchItem != null)
                    searchViewControl(userEntities);
            });

            meshContactViewModel.allMessagedWithEntity.observe(this, userEntities -> {
                if (userEntities != null) {
                    isLoaded = false;
                    msgWithFavList = userEntities;

                    if (currentSelection == Constants.SpinnerItem.ALL) {
                        updateAdapterByData(userEntities);
                    }

                }
                if (mSearchItem != null)
                    searchViewControl(userEntities);
            });
        }
    }

    private void updateGroupAdapterData(PagedList<GroupEntity> groupEntities) {
        getConversationAdapter().submitList(groupEntities);

        if (groupEntities != null && groupEntities.size() > 0) {
            controlEmpty(false);
        } else {
            controlEmpty(true);
        }
    }

    private void updateAdapterByData(PagedList<UserEntity> userEntities) {
        getAdapter().submitList(userEntities);
        userEntityList = userEntities;

        if (userEntityList != null && userEntityList.size() > 0) {
            controlEmpty(false);
        } else {
            controlEmpty(true);
        }
    }
}
