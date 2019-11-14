package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentMeshcontactBinding;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.lang.reflect.Field;
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
public class MeshContactsFragment extends BaseFragment {

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
        controlEmptyLayout();
        setTitle(title);

        init();

        userDataOperation();

        openUserMessage();

    }

    private void userDataOperation() {

        if (meshContactViewModel != null) {

            meshContactViewModel.allUserEntity.observe(this, userEntities -> {
                if (userEntities != null) {
                    getAdapter().resetWithList(userEntities);
                    userEntityList = userEntities;
                }
                if (mSearchItem != null)
                    searchViewControl(userEntities);
            });

            meshContactViewModel.getGetFilteredList().observe(this, userEntities -> {

                setTitle(getResources().getString(R.string.title_contacts_fragment));

                if (userEntities != null && userEntities.size() > 0) {
                    fragmentMeshcontactBinding.notFoundView.setVisibility(View.GONE);
                    getAdapter().clear();
                    getAdapter().addItem(userEntities);
                    isLoaded = false;

                } else {
                    if (!isLoaded) {
                        fragmentMeshcontactBinding.emptyLayout.setVisibility(View.VISIBLE);
                        enableLoading();

                        isLoaded = true;
                        Runnable runnable = () -> {
                            fragmentMeshcontactBinding.tvMessage.setText("No User Found");
                            enableEmpty();
                            fragmentMeshcontactBinding.loadingView.setVisibility(View.GONE);

                            if (getActivity() != null) {
                                ((MainActivity) getActivity()).disableLoading();
                            }
                        };
                        loaderHandler.postDelayed(runnable, Constants.AppConstant.LOADING_TIME_SHORT);
                    }
                }
            });

            meshContactViewModel.backUserEntity.observe(this, userEntities -> {
                userEntityList = userEntities;
            });

            meshContactViewModel.startUserObserver();
        }
    }

    private void openUserMessage() {
        if (meshContactViewModel != null) {
            meshContactViewModel.openUserMessage().observe(this, userEntity -> {

                if (mSearchItem != null) {
                    mSearchItem.getActionView().clearFocus();

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(UserEntity.class.getName(), userEntity.meshId);
                    startActivity(intent);
                }

            });
        }
    }

    private void initSearchView(SearchView searchView) {

        getCompositeDisposable().add(UIHelper.fromSearchView(searchView)
                .debounce(1, TimeUnit.SECONDS, Schedulers.computation())
                .filter((AppendOnlyLinkedArrayList.NonThrowingPredicate<String>) s -> (s.length() > 1 || s.length() == 0))
                .distinctUntilChanged()/*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/
                .subscribeWith(searchContacts()));

    }

    private DisposableObserver<String> searchContacts() {
        return new DisposableObserver<String>() {

            boolean isSearchStart = false;

            @Override
            public void onNext(String string) {

                if (!isSearchStart) {
                    searchLoading();
                    isSearchStart = true;
                }

                if (meshContactViewModel != null) {
                    Timber.d("Search query: %s", string);
                    meshContactViewModel.startSearch(string, userEntityList);
                }
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("onError: %s", e.getMessage());
            }

            @Override
            public void onComplete() {
                Timber.e("onError: Complete");
            }
        };
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        if (getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.menu_search_contact, menu);

            mSearchItem = menu.findItem(R.id.action_search);
            // Resolve search option visibility problem when contact is appeared from starting point
            searchViewControl(userEntityList);

            mSearchView = mSearchItem.getActionView().findViewById(R.id.search_view);


            // mSearchView = (SearchView) mSearchItem.getActionView();
            mSearchView.setQueryHint(getString(R.string.search));

            mSearchView.setIconified(true);

            ImageView searchClose = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            searchClose.setImageResource(R.mipmap.ic_cross_grey);

            // Getting EditText view from search view and change cursor color
            AutoCompleteTextView searchTextView = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            try {
                // Fixed value for getting cursor drawable from Edit text or search view
                Field mCursorDrawableRes = TextView.class.getDeclaredField(getString(R.string.cursordrawable));
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
            } catch (Exception e) {
                e.printStackTrace();
            }

            searchCollapseListener(mSearchItem, mSearchView);

            initSearchView(mSearchView);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            mSearchView.setBackgroundColor(getResources().getColor(R.color.white));
            Log.d("UiTest", "Search click call");
            //mSearchView.onActionViewExpanded();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchCollapseListener(MenuItem searchItem, SearchView searchView) {
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.setBackgroundColor(getResources().getColor(R.color.white));
                searchView.setMaxWidth(Integer.MAX_VALUE);
                Log.d("UiTest", "Search expand");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //searchView.setBackgroundColor(-1);
                Log.d("UiTest", "Search collapse");
                return true;
            }
        });
    }

    private void searchViewControl(List<UserEntity> userEntities) {
        boolean isSearchVisible = userEntities != null && userEntities.size() > 0;
        mSearchItem.setVisible(isSearchVisible);

        if (isSearchVisible) {
            setTitle(getResources().getString(R.string.title_contacts_fragment));
        }
    }

    private void controlEmptyLayout() {
        if (!Constants.IS_LOADING_ENABLE) {
            Handler handler = new Handler(Looper.getMainLooper());
            enableLoading();
            title = getResources().getString(R.string.discovering_users);

            Runnable runnable = () -> {
                if (fragmentMeshcontactBinding.emptyLayout.getVisibility() == View.VISIBLE) {
                    try {
                        enableEmpty();
                        setTitle(getResources().getString(R.string.title_contacts_fragment));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Constants.IS_LOADING_ENABLE = true;
            handler.postDelayed(runnable, Constants.AppConstant.LOADING_TIME);
        } else {
            enableEmpty();
            title = getResources().getString(R.string.title_contacts_fragment);
        }
    }

    private void enableLoading() {
        fragmentMeshcontactBinding.loadingText.setText(getResources().getString(R.string.this_may_take_while));
        fragmentMeshcontactBinding.notFoundView.setVisibility(View.GONE);
        fragmentMeshcontactBinding.loadingView.setVisibility(View.VISIBLE);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).enableLoading();
        }
    }

    private void enableEmpty() {
        fragmentMeshcontactBinding.notFoundView.setVisibility(View.VISIBLE);
        fragmentMeshcontactBinding.loadingView.setVisibility(View.GONE);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).disableLoading();
        }
    }

    protected void searchLoading() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                fragmentMeshcontactBinding.loadingText.setText(getResources().getString(R.string.searching));
                fragmentMeshcontactBinding.notFoundView.setVisibility(View.GONE);
                fragmentMeshcontactBinding.loadingView.setVisibility(View.VISIBLE);

                ((MainActivity) getActivity()).enableLoading();
            });
        }
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
                return (T) ServiceLocator.getInstance().getMeshContactViewModel();
            }
        }).get(MeshContactViewModel.class);
    }
}
