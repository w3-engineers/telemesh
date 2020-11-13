/*
package com.w3engineers.unicef.telemesh.ui.main;

import android.Manifest;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMainBinding;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactsFragment;
import com.w3engineers.unicef.telemesh.ui.meshdiscovered.DiscoverFragment;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedFragment;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;
import com.w3engineers.unicef.util.helper.DexterPermissionHelper;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.StorageUtil;

public class MainActivityNew extends TelemeshBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private static MainActivityNew sInstance;
    private Menu bottomMenu;
    private MainActivityViewModel mViewModel;
    private Fragment mCurrentFragment;
    private BulletinTimeScheduler sheduler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
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
        binding = (ActivityMainBinding) getViewDataBinding();
        sInstance = this;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        requestMultiplePermissions();

        Constants.IS_LOADING_ENABLE = false;

//        sheduler = BulletinTimeScheduler.getInstance().connectivityRegister();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        bottomMenu = binding.bottomNavigation.getMenu();
        boolean fromSettings = getIntent().getBooleanExtra(MainActivity.class.getSimpleName(), false);
        initBottomBar(fromSettings);
        binding.searchBar.editTextSearch.setHint(LanguageUtil.getString(R.string.search));
        mViewModel = getViewModel();

        mViewModel.setUserCountWorkRequest();
        mViewModel.setServerAppShareCountWorkerRequest();
        mViewModel.setLocalAppShareCountWorkerRequest();

        setClickListener(binding.textViewBackground, binding.searchBar.imageViewBack, binding.searchBar.imageViewCross);

        InAppUpdate.getInstance(MainActivityNew.this).setAppUpdateProcess(false);

        StorageUtil.getFreeMemory();
    }

    protected void requestMultiplePermissions() {
        DexterPermissionHelper.getInstance().requestForPermission(this, () -> { },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void initBottomBar(boolean fromSettings) {

        Fragment mFragment = null;
        String title;
        if (fromSettings) {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_setting);
            menuItem.setChecked(true);
            mFragment = new SettingsFragment();
            title = LanguageUtil.getString(R.string.title_settings_fragment);
        } else {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_discover);
            menuItem.setChecked(true);
            mFragment = new DiscoverFragment();
            title = LanguageUtil.getString(R.string.title_discoverd_fragment);
        }
        mCurrentFragment = mFragment;
        loadFragment(mFragment, title);

    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
        setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() > Constants.DefaultValue.NEG_INTEGER_ONE) {
            setFragmentsOnPosition(menuItem);
        }
        return true;
    }

    private void setFragmentsOnPosition(@NonNull MenuItem item) {
        Fragment mFragment = null;
        String toolbarTitle = "";
        switch (item.getItemId()) {
            case R.id.action_discover:
                toolbarTitle = LanguageUtil.getString(R.string.title_discoverd_fragment);
                mFragment = new DiscoverFragment();
                //hideUserBadge();
                break;
            case R.id.action_contact:
                toolbarTitle = LanguageUtil.getString(R.string.title_personal_fragment);
                mFragment = new MeshContactsFragment();
//                hideUserBadge();
                break;
            case R.id.action_message_feed:
                toolbarTitle = LanguageUtil.getString(R.string.title_message_feed_fragment);
              */
/*  createBadgeCount(Constants.DefaultValue.INTEGER_VALUE_ZERO
                        , Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);*//*

                mFragment = new MessageFeedFragment();
                break;
            case R.id.action_setting:
                toolbarTitle = LanguageUtil.getString(R.string.title_settings_fragment);
                mFragment = new SettingsFragment();
                break;
        }
        if (mFragment != null && !toolbarTitle.equals("")) {
            mCurrentFragment = mFragment;

            binding.searchBar.editTextSearch.setText("");

            loadFragment(mFragment, toolbarTitle);
        }
    }

    private MainActivityViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getMainActivityViewModel();
            }
        }).get(MainActivityViewModel.class);
    }

    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onResume() {
        super.onResume();
//        int myMode = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readInt(Constants.preferenceKey.MY_MODE);
//        initNoNetworkCallback(myMode);
//        showHideInternetWarning(myMode, Constants.IS_DATA_ON);
    }

    private void showHideInternetWarning(int myMode, boolean isMobileDataOn) {
        if (myMode == Constants.INTERNET_ONLY || myMode == Constants.SELLER_MODE) {
            if (isMobileDataOn) {
                binding.textViewNoInternet.setVisibility(View.GONE);
            } else {
                binding.textViewNoInternet.setVisibility(View.VISIBLE);
            }
        } else {
            binding.textViewNoInternet.setVisibility(View.GONE);
        }
    }

    private void initNoNetworkCallback(int myMode) {
        sheduler.initNoInternetCallback(isMobileDataOn -> {
            showHideInternetWarning(myMode, isMobileDataOn);
        });
    }
}
*/
