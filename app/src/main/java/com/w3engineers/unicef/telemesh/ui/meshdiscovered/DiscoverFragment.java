package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.FragmentDiscoverBinding;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.groupcreate.GroupCreateActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.base.ui.BaseFragment;
import com.w3engineers.unicef.util.helper.LanguageUtil;

import java.util.List;

public class DiscoverFragment extends BaseFragment {

    private FragmentDiscoverBinding fragmentDiscoverBinding;
    @Nullable
    public DiscoverViewModel discoverViewModel;
    @Nullable
    public List<UserEntity> userEntityList;
    @Nullable
    public MenuItem mSearchItem;
    private String title;
    private boolean isLoaded = false;
    private SearchView mSearchView;
    private DiscoverAdapter meshContactAdapter;

    Handler loaderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    protected void startUI() {
        fragmentDiscoverBinding = (FragmentDiscoverBinding)
                getViewDataBinding();

        setHasOptionsMenu(true);
        controlEmptyLayout();
        ((MainActivity) getActivity()).setToolbarTitle(title);

        init();

        userDataOperation();

        openUserMessage();

        changeFavouriteStatus();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (discoverViewModel != null) {
            discoverViewModel.selectedChattedUser(null);
        }
    }

    private void userDataOperation() {

        if (discoverViewModel != null) {

            discoverViewModel.startUserObserver();

            discoverViewModel.nearbyUsers.observe(this, userEntities -> {
                if (userEntities != null) {
                    getAdapter().submitList(userEntities);
                    userEntityList = userEntities;

                    isLoaded = false;

                    if (userEntityList != null && userEntityList.size() > 0) {
                        if (fragmentDiscoverBinding.emptyLayout.getVisibility() == View.VISIBLE) {
                            fragmentDiscoverBinding.emptyLayout.setVisibility(View.GONE);
                        }
                        fragmentDiscoverBinding.fabChat.show();
                    }else{
                        fragmentDiscoverBinding.fabChat.hide();
                    }
                }
                if (mSearchItem != null)
                    searchViewControl(userEntities);
            });

            discoverViewModel.getGetFilteredList().observe(this, userEntities -> {

                ((MainActivity) getActivity()).setToolbarTitle(LanguageUtil.getString(R.string.title_discoverd_fragment));
                if (userEntities != null && userEntities.size() > 0) {
                    fragmentDiscoverBinding.notFoundView.setVisibility(View.GONE);
                    fragmentDiscoverBinding.emptyLayout.setVisibility(View.GONE);

                    fragmentDiscoverBinding.fabChat.show();

                    //  getAdapter().clear();
                    meshContactAdapter.submitList(userEntities);
                    isLoaded = false;

                } else {
                    fragmentDiscoverBinding.fabChat.hide();
                    if (!isLoaded) {
                        fragmentDiscoverBinding.emptyLayout.setVisibility(View.VISIBLE);
                        //enableLoading();

                        isLoaded = true;
                        Runnable runnable = () -> {
                            fragmentDiscoverBinding.tvMessage.setText("No User Found");
                            enableEmpty();
                            fragmentDiscoverBinding.loadingView.setVisibility(View.GONE);

                        };
                        loaderHandler.postDelayed(runnable, Constants.AppConstant.LOADING_TIME_SHORT);
                    }
                }
            });

            discoverViewModel.backUserEntity.observe(this, userEntities -> {
                userEntityList = userEntities;
            });

        }
    }

    private void openUserMessage() {
        if (discoverViewModel != null) {
            discoverViewModel.openUserMessage().observe(this, userEntity -> {

                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideSearchBar();

                    discoverViewModel.selectedChattedUser(userEntity.meshId);

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(UserEntity.class.getName(), userEntity.meshId);
                    startActivity(intent);
                }

            });
        }
    }


    private void changeFavouriteStatus() {
        if (discoverViewModel != null) {
            discoverViewModel.changeFavourite().observe(this, userEntity -> {
                if (userEntity.getIsFavourite() == Constants.FavouriteStatus.UNFAVOURITE) {
                    discoverViewModel.updateFavouriteStatus(userEntity.getMeshId(), Constants.FavouriteStatus.FAVOURITE);
                } else if (userEntity.getIsFavourite() == Constants.FavouriteStatus.FAVOURITE) {
                    discoverViewModel.updateFavouriteStatus(userEntity.getMeshId(), Constants.FavouriteStatus.UNFAVOURITE);
                }

            });
        }
    }

    public void searchContacts(String query) {
        if (discoverViewModel != null) {
            discoverViewModel.startSearch(query, discoverViewModel.getCurrentUserList());
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.fab_chat) {
            startActivity(new Intent(getActivity(), GroupCreateActivity.class));
        }
    }

    /*private void searchCollapseListener(MenuItem searchItem, SearchView searchView) {
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.setBackgroundColor(getResources().getColor(R.color.white));
                searchView.setMaxWidth(Integer.MAX_VALUE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //searchView.setBackgroundColor(-1);
                return true;
            }
        });
    }*/

    private void searchViewControl(List<UserEntity> userEntities) {
        boolean isSearchVisible = userEntities != null && userEntities.size() > 0;
        mSearchItem.setVisible(isSearchVisible);

        if (isSearchVisible) {
            ((MainActivity) getActivity()).setToolbarTitle(LanguageUtil.getString(R.string.title_discoverd_fragment));
        }
    }

    private void controlEmptyLayout() {
        if (!Constants.IS_LOADING_ENABLE) {
            Handler handler = new Handler(Looper.getMainLooper());
            enableLoading();
            title = LanguageUtil.getString(R.string.discovering_users);

            Runnable runnable = () -> {
                if (fragmentDiscoverBinding.emptyLayout.getVisibility() == View.VISIBLE) {
                    try {
                        enableEmpty();
                        ((MainActivity) getActivity()).setToolbarTitle(LanguageUtil.getString(R.string.title_discoverd_fragment));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Constants.IS_LOADING_ENABLE = true;
            handler.postDelayed(runnable, Constants.AppConstant.LOADING_TIME);
        } else {
            enableEmpty();
            title = LanguageUtil.getString(R.string.title_discoverd_fragment);
        }
    }

    private void enableLoading() {
        //fragmentMeshcontactBinding.loadingText.setText(getResources().getString(R.string.this_may_take_while));
        fragmentDiscoverBinding.notFoundView.setVisibility(View.GONE);
        fragmentDiscoverBinding.loadingView.setVisibility(View.VISIBLE);
        fragmentDiscoverBinding.rippleBackground.startRippleAnimation();
    }

    public void enableEmpty() {
        fragmentDiscoverBinding.notFoundView.setVisibility(View.VISIBLE);
        fragmentDiscoverBinding.loadingView.setVisibility(View.GONE);
        fragmentDiscoverBinding.rippleBackground.stopRippleAnimation();


//        fragmentDiscoverBinding.fabChat.hide();


        /*if (getActivity() != null) {
            ((MainActivity) getActivity()).disableLoading();
        }*/
    }

    // General API's and initialization area
    private void init() {

        setClickListener(fragmentDiscoverBinding.fabChat);
        initAllText();
        discoverViewModel = getViewModel();

        fragmentDiscoverBinding.contactRecyclerView.setItemAnimator(null);
        //   fragmentDiscoverBinding.contactRecyclerView.setHasFixedSize(true);
        fragmentDiscoverBinding.contactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //    ((SimpleItemAnimator)fragmentDiscoverBinding.contactRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        //    fragmentDiscoverBinding.contactRecyclerView.setItemAnimator(null);

        meshContactAdapter = new DiscoverAdapter(discoverViewModel);
        fragmentDiscoverBinding.contactRecyclerView.setAdapter(meshContactAdapter);
    }

    private DiscoverAdapter getAdapter() {
        return (DiscoverAdapter) fragmentDiscoverBinding
                .contactRecyclerView.getAdapter();
    }

    private DiscoverViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getDiscoveViewModel(getActivity().getApplication());
            }
        }).get(DiscoverViewModel.class);
    }

    private void initAllText() {
        fragmentDiscoverBinding.tvMessage.setText(LanguageUtil.getString(R.string.no_contact_available));
        fragmentDiscoverBinding.textViewSearching.setText(LanguageUtil.getString(R.string.searching));
    }
}
