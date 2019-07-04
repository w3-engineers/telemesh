package com.w3engineers.unicef.telemesh.ui.main;

import android.Manifest;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3.offlinelocationtrack.OfflineLocationTracker;
import com.w3.offlinelocationtrack.listener.LocationUpdateListener;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.ui.base.rm.RmBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMainBinding;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactsFragment;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedFragment;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;

import java.util.List;

public class MainActivity extends RmBaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private MainActivityViewModel mViewModel;
    private boolean doubleBackToExitPressedOnce = false;
    private Menu bottomMenu;

    // Location element
    private OfflineLocationTracker locationTracker;
    private long updateTime = 1000 * 60; // 1 minute
    private int minimumDistance = 0; // meter basis

    private FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    public static MainActivity mainActivity;

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
    protected void startUI() {
        binding = (ActivityMainBinding) getViewDataBinding();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        Constants.IS_LOADING_ENABLE = false;
        mainActivity = this;

        BulletinTimeScheduler.getInstance().connectivityRegister();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        bottomMenu = binding.bottomNavigation.getMenu();
        initBottomBar();
        mViewModel = getViewModel();
        //when  counting need to add
        /*
        mViewModel.getMessageCount().observe(this, messageCount -> {
            //call createBadgeCount() put necessary params (count, position)
        });
        mViewModel.getSurveyCount().observe(this, surveyCount ->{
            //call createBadgeCount() put necessary params (count, position)
        });*/

//        mViewModel.makeSendingMessageAsFailed();

        // Only for location element
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initLocationTracker();
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

    private void initBottomBar() {

        boolean fromSettings = getIntent().getBooleanExtra(MainActivity.class.getSimpleName(), false);
        Fragment mFragment = null;
        if (fromSettings) {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_setting);
            menuItem.setChecked(true);
            mFragment = new SettingsFragment();
        } else {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_contact);
            menuItem.setChecked(true);
            mFragment = new MeshContactsFragment();
        }
        loadFragment(mFragment, getString(R.string.title_contacts_fragment));

//        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) binding.bottomNavigation
//                .getChildAt(Constants.MenuItemPosition.POSITION_FOR_CONTACT);

        /*binding.bottomNavigation
                .setIconSize(Constants.MenuItemPosition.MENU_ITEM_WIDTH
                        , Constants.MenuItemPosition.MENU_ITEM_HEIGHT);
        binding.bottomNavigation.enableShiftingMode(false);
        binding.bottomNavigation.enableItemShiftingMode(false);
        binding.bottomNavigation.enableAnimation(false);*/

/*
        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);
        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_SURVEY);
*/


        //its for checking. must be removed later
 /*       createBadgeCount(6, Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);
        createBadgeCount(12, Constants.MenuItemPosition.POSITION_FOR_SURVEY);*/

    }

    // Again this api will be enable when its functionality will be added
    /*private void addBadgeToBottomBar(int menuItemPosition) {
        NotificationBadgeBinding notificationBadgeBinding = NotificationBadgeBinding.inflate(getLayoutInflater());
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);
        if (itemView != null) {
            itemView.addView(notificationBadgeBinding.getRoot());
        }
    }*/


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() > Constants.DefaultValue.NEG_INTEGER_ONE) {
            setFragmentsOnPosition(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationTracker.onActivityResult(requestCode, resultCode, data);
    }

    private void setFragmentsOnPosition(@NonNull MenuItem item) {
        Fragment mFragment = null;
        String toolbarTitle = "";
        switch (item.getItemId()) {
            case R.id.action_contact:
                toolbarTitle = getString(R.string.title_contacts_fragment);
                mFragment = new MeshContactsFragment();
                break;
            case R.id.action_message_feed:
                toolbarTitle = getString(R.string.title_message_feed_fragment);
              /*  createBadgeCount(Constants.DefaultValue.INTEGER_VALUE_ZERO
                        , Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);*/
                mFragment = new MessageFeedFragment();
                break;
            case R.id.action_setting:
                toolbarTitle = getString(R.string.title_settings_fragment);
                mFragment = new SettingsFragment();
                break;
        }
        if (mFragment != null && !toolbarTitle.equals("")) {
            loadFragment(mFragment, toolbarTitle);
        }
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
        setTitle(title);
    }

    // Again this api will be enable when its functionality will be added
    /*public void createBadgeCount(int latestCount, int menuItemPosition) {

        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);

        if (itemView == null) {
            return;
        }
        ConstraintLayout constraintLayoutContainer = itemView.findViewById(R.id.constraint_layout_badge);
        TextView textViewBadgeCount = itemView.findViewById(R.id.text_view_badge_count);

        if (latestCount > Constants.DefaultValue.INTEGER_VALUE_ZERO) {
            if (latestCount <= Constants.DefaultValue.MAXIMUM_BADGE_VALUE) {
                textViewBadgeCount.setText(String.valueOf(latestCount));
            } else {
                textViewBadgeCount.setText(R.string.badge_count_more_than_9);
            }
        } else {
            constraintLayoutContainer.setVisibility(View.GONE);
        }
    }*/

    @Override
    protected void onDestroy() {
        locationTracker.removeLocationUpdate();
        super.onDestroy();
//        mViewModel.userOfflineProcess();
    }

    @NonNull
    @Override
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toaster.showShort(getString(R.string.double_press_exit));
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, Constants.DefaultValue.DOUBLE_PRESS_INTERVAL);
    }

    /*@Override
    public void sendToUi(String message) {
        Toast.makeText(this, "Message received:" + message, Toast.LENGTH_SHORT).show();
    }*/

    private void initLocationTracker() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        locationTracker = OfflineLocationTracker.getInstance();

                        locationTracker.init(MainActivity.this)
                                .setUpdateTime(updateTime)
                                .setMinimumDistance(minimumDistance)
                                .requestLocation();

                        locationTracker.getLocationListener(location -> {
                            SharedPref sharedPref = SharedPref.getSharedPref(MainActivity.this);
                            String userName = sharedPref.read(Constants.preferenceKey.USER_NAME);
                            String userId = sharedPref.read(Constants.preferenceKey.MY_USER_ID);

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, userId);
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, userName);
                            bundle.putDouble("latitude", location.getLatitude());
                            bundle.putDouble("longitude", location.getLongitude());
                            mFirebaseAnalytics.setUserId(userId);
                            mFirebaseAnalytics.setUserProperty("userName", userName);
                            mFirebaseAnalytics.logEvent("LocationTrack", bundle);
                        });
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }
}
