package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentMeshcontactBinding;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [04-Oct-2018 at 4:07 PM].
 * * Email: mimosaha@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [04-Oct-2018 at 4:07 PM].
 * * --> <Second Editor> on [04-Oct-2018 at 4:07 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [04-Oct-2018 at 4:07 PM].
 * * --> <Second Reviewer> on [04-Oct-2018 at 4:07 PM].
 * * ============================================================================
 **/
public class MeshContactsFragment extends BaseFragment {

    private FragmentMeshcontactBinding fragmentMeshcontactBinding;
    private ServiceLocator serviceLocator;
    private MeshContactViewModel meshContactViewModel;
    private List<UserEntity> userEntityList;
    private MenuItem mSearchItem;
    private List<UserEntity> prevUserList = null;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meshcontact;
    }

    @Override
    protected void startUI() {
        fragmentMeshcontactBinding = (FragmentMeshcontactBinding)
                getViewDataBinding();

        setHasOptionsMenu(true);

        init();

        userDataOperation();
        openUserMessage();
    }

    private void userDataOperation() {


        meshContactViewModel.getAllUsers().observe(this, userEntities -> {
            getAdapter().resetWithList(userEntities);
            userEntityList = userEntities;
            if (mSearchItem != null)
                mSearchItem.setVisible(userEntities != null && userEntities.size() > 0);
        });

        meshContactViewModel.getGetFilteredList().observe(this, userEntities -> {
            getAdapter().clear();
            getAdapter().addItem(userEntities);
        });


    }

    private void openUserMessage() {
        meshContactViewModel.openUserMessage().observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(@Nullable UserEntity userEntity) {

                mSearchItem.getActionView().clearFocus();

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(UserEntity.class.getName(), userEntity);
                startActivity(intent);
            }
        });
    }

    private void initSearchView(SearchView searchView) {

        getCompositeDisposable().add(UIHelper.fromSearchView(searchView)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .filter((AppendOnlyLinkedArrayList.NonThrowingPredicate<String>) s -> (s.length() > 1 || s.length() == 0))
                .distinctUntilChanged().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchContacts()));

    }

    private DisposableObserver<String> searchContacts() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String string) {
                Timber.d("Search query: " + string);
                meshContactViewModel.startSearch(string, userEntityList);

            }

            @Override
            public void onError(Throwable e) {
                Timber.e("onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_search_contact, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchItem.setVisible(false);

        SearchView mSearchView = (SearchView) mSearchItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search));

        ImageView searchClose = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.mipmap.ic_cross_grey);

        // Getting EditText view from searchview and change cursor color
        AutoCompleteTextView searchTextView = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            // Fixed value for getting cursor drawable from Edit text or search view
            Field mCursorDrawableRes = TextView.class.getDeclaredField(getString(R.string.cursordrawable));
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }

        initSearchView(mSearchView);

        super.onCreateOptionsMenu(menu, inflater);
    }

    // General API's and initialization area
    private void init() {

        meshContactViewModel = getViewModel();

        fragmentMeshcontactBinding.contactRecyclerView.setItemAnimator(null);
        fragmentMeshcontactBinding.contactRecyclerView.setHasFixedSize(true);
        fragmentMeshcontactBinding.contactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        MeshContactAdapter meshContactAdapter = new MeshContactAdapter(meshContactViewModel);
        fragmentMeshcontactBinding.contactRecyclerView.setAdapter(meshContactAdapter);
    }

    private MeshContactAdapter getAdapter() {
        return (MeshContactAdapter) fragmentMeshcontactBinding
                .contactRecyclerView.getAdapter();
    }

    private MeshContactViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                serviceLocator = ServiceLocator.getInstance();
                return (T) serviceLocator.getMeshContactViewModel();
            }
        }).get(MeshContactViewModel.class);
    }
}
