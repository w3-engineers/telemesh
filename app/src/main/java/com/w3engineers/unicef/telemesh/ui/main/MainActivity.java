package com.w3engineers.unicef.telemesh.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.WorkInfo;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.BroadcastDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.LocationTracker;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMainBinding;
import com.w3engineers.unicef.telemesh.databinding.NotificationBadgeBinding;
import com.w3engineers.unicef.telemesh.databinding.WalletBadgeBinding;
import com.w3engineers.unicef.telemesh.ui.groupcreate.GroupCreateActivity;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactsFragment;
import com.w3engineers.unicef.telemesh.ui.meshdiscovered.DiscoverFragment;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedFragment;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.util.base.ui.BaseServiceLocator;
import com.w3engineers.unicef.util.base.ui.TelemeshBaseActivity;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.DexterPermissionHelper;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.StorageUtil;
import com.w3engineers.unicef.util.helper.uiutil.AppBlockerUtil;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;


public class MainActivity extends TelemeshBaseActivity implements NavigationView.OnNavigationItemSelectedListener, DexterPermissionHelper.PermissionCallback {
    private ActivityMainBinding binding;
    private MainActivityViewModel mViewModel;
    private boolean doubleBackToExitPressedOnce = false;
    private Menu bottomMenu;
    private BottomNavigationMenuView bottomNavigationMenuView;
    NotificationBadgeBinding notificationBadgeBinding;
    WalletBadgeBinding walletBadgeBinding;
    @SuppressLint("StaticFieldLeak")
    private static MainActivity sInstance;

    private BulletinTimeScheduler sheduler;
    private Fragment mCurrentFragment;

    Context mContext;
    private int latestMessageCount;

    private int RC_APP_UPDATE = 620;
    private static AppUpdateManager mAppUpdateManager;

    @SuppressLint("StaticFieldLeak")
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
    public BaseServiceLocator a() {
        return ServiceLocator.getInstance();
    }


    @Override
    public void startUI() {
        binding = (ActivityMainBinding) getViewDataBinding();
        sInstance = this;
        mContext = this;

        super.startUI();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        requestMultiplePermissions();

        Constants.IS_LOADING_ENABLE = false;
        Constants.IS_APP_BLOCKER_ON = false;
        mainActivity = this;


        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        bottomMenu = binding.bottomNavigation.getMenu();
        initBottomBar(getIntent());
        initAllText();
        mViewModel = getViewModel();

        // set new user count analytics so that the work manager will trigger
        mViewModel.getMeshInitiatedCall();

        setClickListener(binding.textViewBackground, binding.searchBar.imageViewBack, binding.searchBar.imageViewCross);

        mViewModel.getNewUserWorkInfo().observe(this, workInfos -> {

            // If there are no matching work info, do nothing
            if (workInfos != null && !workInfos.isEmpty()) {
                // We only care about the first output status.
                WorkInfo workInfo = workInfos.get(0);

                boolean finished = workInfo.getState().isFinished();
            }
        });

        subscribeForActiveUser();
        subscribeForNewFeedMessage();
        initSearchListener();

        InAppUpdate.getInstance(MainActivity.this).setAppUpdateProcess(false);
        StorageUtil.getFreeMemory();

        // registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);

        if (!CommonUtil.isEmulator()) {
            registerReceiver(mGpsSwitchStateReceiver, filter);
        }

        checkAppBlockerAvailable();

    }

    protected void requestMultiplePermissions() {
        DexterPermissionHelper.getInstance().requestForPermission(this, this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Todo we have to optimize the below implementation
        if (GroupCreateActivity.IS_NEW_GROUP_CREATED) {
            GroupCreateActivity.IS_NEW_GROUP_CREATED = false;

            if (mCurrentFragment != null
                    && !(mCurrentFragment instanceof MeshContactsFragment)) {

                binding.bottomNavigation.setSelectedItemId(R.id.action_contact);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_cross:
                if (TextUtils.isEmpty(binding.searchBar.editTextSearch.getText())) {
                    hideSearchBar();
                } else {
                    binding.searchBar.editTextSearch.setText("");
                }
                break;
            case R.id.image_view_back:
                binding.searchBar.editTextSearch.setText("");
                hideSearchBar();
                break;
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        initBottomBar(intent);
    }

    private void initBottomBar(Intent intent) {

        boolean fromSettings = intent.getBooleanExtra(MainActivity.class.getSimpleName(), false);
        boolean fromBroadcast = intent.getBooleanExtra(BroadcastDataHelper.class.getSimpleName(), false);

        Fragment mFragment = null;
        String title;
        if (fromSettings) {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_setting);
            menuItem.setChecked(true);
            mFragment = new SettingsFragment();
            title = LanguageUtil.getString(R.string.title_settings_fragment);
        } else if (fromBroadcast) {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_message_feed);
            menuItem.setChecked(true);
            mFragment = new MessageFeedFragment();
            title = LanguageUtil.getString(R.string.title_message_feed_fragment);
//            hideFeedBadge();
        } else {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_discover);
            menuItem.setChecked(true);
            mFragment = new DiscoverFragment();
            title = LanguageUtil.getString(R.string.title_discoverd_fragment);
        }
        mCurrentFragment = mFragment;
        loadFragment(mFragment, title);

        bottomNavigationMenuView = (BottomNavigationMenuView) binding.bottomNavigation
                .getChildAt(Constants.MenuItemPosition.POSITION_FOR_DISCOVER);

        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_DISCOVER);
        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);

        boolean isWalletBackUpDone = SharedPref.readBoolean(Constants.preferenceKey.IS_WALLET_BACKUP_DONE, false);

        if (!isWalletBackUpDone) {
            addBadgeToSettings();
        }

        if (fromBroadcast) {
            hideFeedBadge();
        }
    }

    // Again this api will be enable when its functionality will be added
    private void addBadgeToBottomBar(int menuItemPosition) {
        notificationBadgeBinding = NotificationBadgeBinding.inflate(getLayoutInflater());
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);
        if (itemView != null) {
            itemView.addView(notificationBadgeBinding.getRoot());
        }
    }

    private void addBadgeToSettings() {
        walletBadgeBinding = WalletBadgeBinding.inflate(getLayoutInflater());
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(Constants.MenuItemPosition.POSITION_FOR_MESSAGE_SETTINGS);
        if (itemView != null) {
            itemView.addView(walletBadgeBinding.getRoot());
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() > Constants.DefaultValue.NEG_INTEGER_ONE) {
            setFragmentsOnPosition(item);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    private void setFragmentsOnPosition(@NonNull MenuItem item) {
        Fragment mFragment = null;
        String toolbarTitle = "";
        switch (item.getItemId()) {
            case R.id.action_discover:
                toolbarTitle = LanguageUtil.getString(R.string.title_discoverd_fragment);
                mFragment = new DiscoverFragment();
                break;
            case R.id.action_contact:
                toolbarTitle = LanguageUtil.getString(R.string.title_personal_fragment);
                mFragment = new MeshContactsFragment();
                break;
            case R.id.action_message_feed:
                toolbarTitle = LanguageUtil.getString(R.string.title_message_feed_fragment);
                mFragment = new MessageFeedFragment();
                hideFeedBadge();
                break;
            case R.id.action_setting:
                toolbarTitle = LanguageUtil.getString(R.string.title_settings_fragment);
                mFragment = new SettingsFragment();
                break;
        }
        if (mFragment != null && !toolbarTitle.equals("")) {
            mCurrentFragment = mFragment;

            binding.searchBar.editTextSearch.setText("");
            hideSearchBar();

            loadFragment(mFragment, toolbarTitle);
        }
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
        setToolbarTitle(title);
    }

    public void setToolbarTitle(String title) {
        String myName = SharedPref.read(Constants.preferenceKey.USER_NAME);
        title = title + " [" + myName + "]";
        setTitle(title);
    }

    public void createUserBadgeCount(int latestCount, int menuItemPosition) {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);

        if (itemView != null) {

            ConstraintLayout constraintLayoutContainer = itemView.findViewById(R.id.constraint_layout_badge);
            constraintLayoutContainer.setVisibility(View.GONE);
            TextView textViewBadgeCount = itemView.findViewById(R.id.text_view_badge_count);

            if (latestCount > Constants.DefaultValue.INTEGER_VALUE_ZERO) {

                constraintLayoutContainer.setVisibility(View.VISIBLE);

                if (latestCount <= Constants.DefaultValue.MAXIMUM_BADGE_VALUE) {
                    textViewBadgeCount.setText(String.valueOf(latestCount));
                } else {
                    textViewBadgeCount.setText(R.string.badge_count_more_than_99);
                }
            } else {
                constraintLayoutContainer.setVisibility(View.GONE);
            }

        }
    }

    private void createFeedBadge(int latestCount, int menuItemPosition) {

        ConstraintLayout constraintLayoutContainer = getViewByMenu(menuItemPosition);
        if (constraintLayoutContainer == null) return;
        constraintLayoutContainer.setVisibility(View.GONE);
        if (!(mCurrentFragment instanceof MessageFeedFragment)) {
            if (latestCount > latestMessageCount) {
                constraintLayoutContainer.setVisibility(View.VISIBLE);
            } else {
                constraintLayoutContainer.setVisibility(View.GONE);
            }
        } else {
            constraintLayoutContainer.setVisibility(View.GONE);
        }


        latestMessageCount = latestCount;
    }

    private void createSettingBadge(int menuItemPosition) {

        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);

        if (itemView == null) {
            return;
        }

        ConstraintLayout constraintLayoutContainer = itemView.findViewById(R.id.wallet_badge);
        if (constraintLayoutContainer == null) return;
        constraintLayoutContainer.setVisibility(View.VISIBLE);

        //Todo need to hide when wallet backup done
    }

    private ConstraintLayout getViewByMenu(int menuItem) {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItem);

        if (itemView == null) {
            return null;
        }
        return itemView.findViewById(R.id.constraint_layout_badge);
    }

    private void hideFeedBadge() {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);

        if (itemView != null) {
            ConstraintLayout feedBadge = itemView.findViewById(R.id.constraint_layout_badge);
            feedBadge.setVisibility(View.GONE);
        }
    }

    public void showSearchBar() {
        binding.toolbarMain.setVisibility(View.INVISIBLE);
        binding.searchBar.getRoot().setVisibility(View.VISIBLE);

        binding.searchBar.editTextSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.searchBar.editTextSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideSearchBar() {
        binding.toolbarMain.setVisibility(View.VISIBLE);
        binding.searchBar.getRoot().setVisibility(View.INVISIBLE);
        UIHelper.hideKeyboardFrom(this, binding.searchBar.editTextSearch);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RmDataHelper.getInstance().destroy();
        sInstance = null;
        if (!CommonUtil.isEmulator()) {
            LocationTracker.getInstance(mContext).stopListener();
            unregisterReceiver(mGpsSwitchStateReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        if (!Constants.IS_APP_BLOCKER_ON) {

            if (binding.searchBar.getRoot().getVisibility() == View.VISIBLE) {
                binding.searchBar.editTextSearch.setText("");
                hideSearchBar();
                return;
            } else if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, LanguageUtil.getString(R.string.double_press_exit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, Constants.DefaultValue.DOUBLE_PRESS_INTERVAL);
        }
    }

    private void subscribeForActiveUser() {
        if (mViewModel != null) {
            mViewModel.getActiveUser().observe(this, userEntities -> {
                runOnUiThread(() -> createUserBadgeCount(userEntities.size(), Constants.MenuItemPosition.POSITION_FOR_DISCOVER));
            });
        }
    }

    private void subscribeForNewFeedMessage() {
        if (mViewModel != null) {
            mViewModel.getNewFeedsList().observe(this, feedList -> {
                runOnUiThread(() -> createFeedBadge(feedList.size(), Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED));
            });
        }
    }

    private void initSearchListener() {

        binding.searchBar.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCurrentFragment != null
                        && mCurrentFragment instanceof MeshContactsFragment) {
                    ((MeshContactsFragment) mCurrentFragment).searchContacts(s.toString());
                } else if (mCurrentFragment != null
                        && mCurrentFragment instanceof DiscoverFragment) {
                    ((DiscoverFragment) mCurrentFragment).searchContacts(s.toString());
                }
            }
        });
    }

    private void initAllText() {
        binding.searchBar.editTextSearch.setHint(LanguageUtil.getString(R.string.search));
    }

    /**
     * Following broadcast receiver is to listen the Location button toggle state in Android.
     */
    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGpsEnabled || isNetworkEnabled) {
                    //location is enabled
                    Log.e("gps_staus", "gps has been on");
                    LocationTracker.getInstance(mContext).getLocation();
                    CommonUtil.dismissDialog();
                } else {
                    //location is disabled
                    Log.e("gps_staus", "gps has been off");
                    CommonUtil.showGpsOrLocationOffPopup(MainActivity.this);
                }
            }
        }
    };

    public void checkPlayStoreAppUpdate(int type, String normalUpdateJson) {
        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else {
                RmDataHelper.getInstance().appUpdateFromOtherServer(type, normalUpdateJson);
            }
        });
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (mAppUpdateManager != null) {
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    }
                }
            };

    private void checkAppBlockerAvailable() {

        int currentUpdateType = SharedPref.readInt(Constants.preferenceKey.APP_UPDATE_TYPE);
        int currentVersionCode = SharedPref.readInt(Constants.preferenceKey.APP_UPDATE_VERSION_CODE);
        String currentVersionName = SharedPref.read(Constants.preferenceKey.APP_UPDATE_VERSION_NAME);

        if (BuildConfig.VERSION_CODE < currentVersionCode
                && currentUpdateType == Constants.AppUpdateType.BLOCKER) {

            openAppBlocker(currentVersionName);
        }
    }

    public void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.main_view),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        });


        snackbar.setActionTextColor(getResources().getColor(R.color.background_color));
        snackbar.show();
    }

    public void feedRefresh() {
        if ((mCurrentFragment instanceof MessageFeedFragment)) {

            Constants.IS_DATA_ON = true;
            //   RmDataHelper.getInstance().mLatitude = "22.8456";
            //  RmDataHelper.getInstance().mLongitude = "89.5403";

            MessageFeedFragment messageFeedFragment = (MessageFeedFragment) mCurrentFragment;
            messageFeedFragment.swipeRefreshOperation();
        }
    }

    public void stopAnimation() {
        if (mCurrentFragment instanceof DiscoverFragment) {
            ((DiscoverFragment) mCurrentFragment).enableEmpty();
        }
    }

    public void openAppBlocker(String versionName) {
        Constants.IS_APP_BLOCKER_ON = true;
        // App blocker ui open test
        HandlerUtil.postForeground(new Runnable() {
            @Override
            public void run() {
                AppBlockerUtil.openAppBlockerDialog(MainActivity.this, versionName);
            }
        }, 10 * 1000);
    }

    @Override
    public void onPermissionGranted() {
        //   locationTracker = new LocationTracker(mContext, MainActivity.this);

        if (!CommonUtil.isEmulator()) {
            LocationTracker.getInstance(mContext).getLocation();

            // Check if GPS enabled
            if (LocationTracker.getInstance(mContext).canGetLocation()) {

                double latitude = LocationTracker.getInstance(mContext).getLatitude();
                double longitude = LocationTracker.getInstance(mContext).getLongitude();

            } else {
                CommonUtil.showGpsOrLocationOffPopup(MainActivity.this);
            }
        }
    }
}
