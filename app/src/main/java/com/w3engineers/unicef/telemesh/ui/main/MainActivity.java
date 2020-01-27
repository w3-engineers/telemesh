package com.w3engineers.unicef.telemesh.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.work.WorkInfo;

import com.crashlytics.android.Crashlytics;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMainBinding;
import com.w3engineers.unicef.telemesh.databinding.NotificationBadgeBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactsFragment;
import com.w3engineers.unicef.telemesh.ui.meshdiscovered.DiscoverFragment;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedFragment;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.DexterPermissionHelper;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.StorageUtil;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class MainActivity extends TelemeshBaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private MainActivityViewModel mViewModel;
    private boolean doubleBackToExitPressedOnce = false;
    private Menu bottomMenu;
    private BottomNavigationMenuView bottomNavigationMenuView;
    NotificationBadgeBinding notificationBadgeBinding;
    @SuppressLint("StaticFieldLeak")
    private static MainActivity sInstance;
    private BulletinTimeScheduler sheduler;
    private Fragment mCurrentFragment;

    private int latestUserCount;
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
        super.startUI();
        binding = (ActivityMainBinding) getViewDataBinding();
        sInstance = this;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        requestMultiplePermissions();

        Constants.IS_LOADING_ENABLE = false;
        mainActivity = this;

        sheduler = BulletinTimeScheduler.getInstance().connectivityRegister();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        bottomMenu = binding.bottomNavigation.getMenu();
        initBottomBar();
        initAllText();
        mViewModel = getViewModel();


        /*if (isRestart) {
            View view = binding.bottomNavigation.findViewById(R.id.action_contact);
            view.performClick();
        }*/

        // set new user count analytics so that the work manager will trigger
        mViewModel.setUserCountWorkRequest();
        mViewModel.setServerAppShareCountWorkerRequest();
        mViewModel.setLocalAppShareCountWorkerRequest();

        setClickListener(binding.textViewBackground, binding.searchBar.imageViewBack, binding.searchBar.imageViewCross);

        mViewModel.getNewUserWorkInfo().observe(this, workInfos -> {

            // If there are no matching work info, do nothing
            if (workInfos != null && !workInfos.isEmpty()) {
                // We only care about the first output status.
                WorkInfo workInfo = workInfos.get(0);

                boolean finished = workInfo.getState().isFinished();
            }
        });

        /*mViewModel.getMyUserMode().observe(this, integer -> {
            if (integer == null) return;
            showHideInternetWarning(integer, Constants.IS_DATA_ON);
        });*/

        subscribeForActiveUser();
        subscribeForNewFeedMessage();

        //when  counting need to add
        /*
        mViewModel.getMessageCount().observe(this, messageCount -> {
            //call createBadgeCount() put necessary params (count, position)
        });
        mViewModel.getSurveyCount().observe(this, surveyCount ->{
            //call createBadgeCount() put necessary params (count, position)
        });*/

//        mViewModel.makeSendingMessageAsFailed();


       /* new Handler().postDelayed(() -> {
            AppShareCountEntity entity = new AppShareCountEntity();
            entity.setCount(1);
            String myId = SharedPref.getSharedPref(App.getContext()).read(Constants.preferenceKey.MY_USER_ID);
            entity.setUserId(myId);
            entity.setDate(TimeUtil.getDateString(System.currentTimeMillis()));
            List<AppShareCountEntity> list = new ArrayList<>();
            list.add(entity);
            AnalyticsDataHelper.getInstance().sendAppShareCountAnalytics(list);
        }, 10000);*/
        /*DiagramUtil.on(this).start();*/

        initSearchListener();

        InAppUpdate.getInstance(MainActivity.this).setAppUpdateProcess(false);

        StorageUtil.getFreeMemory();

/*        if (!CommonUtil.isLocationGpsOn(this)){
            CommonUtil.showGpsOrLocationOffPopup(this);
        }*/

        registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    protected void requestMultiplePermissions() {

        DexterPermissionHelper.getInstance().requestForPermission(this, () -> {

                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        /*Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            CommonUtil.showPermissionPopUp(MainActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> requestMultiplePermissions()).onSameThread().check();*/
    }

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    public void onResume() {
        super.onResume();
        int myMode = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readInt(Constants.preferenceKey.MY_MODE);
        initNoNetworkCallback(myMode);
        showHideInternetWarning(myMode, Constants.IS_DATA_ON);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            /*case R.id.text_view_background:
                // disableLoading();
                break;*/
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

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e("AppUpdateProcess", "onActivityResult: app download failed");
            }
        }
    }*/

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

        /*boolean isRestart = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readBoolean(Constants.preferenceKey.IS_RESTART);
        if (isRestart) {
            fromSettings = true;
            SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.IS_RESTART, false);
        }*/

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

        bottomNavigationMenuView = (BottomNavigationMenuView) binding.bottomNavigation
                .getChildAt(Constants.MenuItemPosition.POSITION_FOR_DISCOVER);

        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_DISCOVER);
        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);

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
    private void addBadgeToBottomBar(int menuItemPosition) {
        notificationBadgeBinding = NotificationBadgeBinding.inflate(getLayoutInflater());
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);
        if (itemView != null) {
            itemView.addView(notificationBadgeBinding.getRoot());
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() > Constants.DefaultValue.NEG_INTEGER_ONE) {
            setFragmentsOnPosition(item);
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
              /*  createBadgeCount(Constants.DefaultValue.INTEGER_VALUE_ZERO
                        , Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);*/
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
        setTitle(title);
    }

    public void createUserBadgeCount(int latestCount, int menuItemPosition) {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);

        if (itemView != null) {

            ConstraintLayout constraintLayoutContainer = itemView.findViewById(R.id.constraint_layout_badge);
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

    // Again this api will be enable when its functionality will be added
    /*public void createBadgeCount(int latestCount, int menuItemPosition) {
        ConstraintLayout constraintLayoutContainer = getViewByMenu(menuItemPosition);
        if (constraintLayoutContainer == null) return;
        // TextView textViewBadgeCount = itemView.findViewById(R.id.text_view_badge_count);

        if (!(mCurrentFragment instanceof DiscoverFragment)) {
            if (latestCount > latestUserCount) {
                constraintLayoutContainer.setVisibility(View.VISIBLE);
            } else {
                constraintLayoutContainer.setVisibility(View.GONE);
            }
        } else {
            constraintLayoutContainer.setVisibility(View.GONE);
        }


        latestUserCount = latestCount;

       *//* if (latestCount > Constants.DefaultValue.INTEGER_VALUE_ZERO) {

            constraintLayoutContainer.setVisibility(View.VISIBLE);

            if (latestCount <= Constants.DefaultValue.MAXIMUM_BADGE_VALUE) {
                textViewBadgeCount.setText(String.valueOf(latestCount));
            } else {
                textViewBadgeCount.setText(R.string.badge_count_more_than_99);
            }
        } else {
            constraintLayoutContainer.setVisibility(View.GONE);
        }*//*
    }*/

    private void createFeedBadge(int latestCount, int menuItemPosition) {

        ConstraintLayout constraintLayoutContainer = getViewByMenu(menuItemPosition);
        if (constraintLayoutContainer == null) return;
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

    private ConstraintLayout getViewByMenu(int menuItem) {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItem);

        if (itemView == null) {
            return null;
        }
        return itemView.findViewById(R.id.constraint_layout_badge);
    }

    /*private void hideUserBadge() {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(Constants.MenuItemPosition.POSITION_FOR_DISCOVER);

        if (itemView == null) {
            return;
        }
        ConstraintLayout userBadgeView = itemView.findViewById(R.id.constraint_layout_badge);

        userBadgeView.setVisibility(View.GONE);
    }*/

    private void hideFeedBadge() {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);

        if (itemView != null) {
            ConstraintLayout feedBadge = itemView.findViewById(R.id.constraint_layout_badge);
            feedBadge.setVisibility(View.GONE);
        }
    }

    /*public void enableLoading() {
        binding.searchingView.setVisibility(View.VISIBLE);
        binding.mainView.setVisibility(View.GONE);
    }

    public void disableLoading() {
        binding.searchingView.setVisibility(View.GONE);
        binding.mainView.setVisibility(View.VISIBLE);
    }*/

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
//        mViewModel.userOfflineProcess();
        sInstance = null;

        unregisterReceiver(mGpsSwitchStateReceiver);
    }

    @Override
    public void onBackPressed() {
        if (binding.searchBar.getRoot().getVisibility() == View.VISIBLE) {
            binding.searchBar.editTextSearch.setText("");
            hideSearchBar();
            return;
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toaster.showShort(LanguageUtil.getString(R.string.double_press_exit));
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, Constants.DefaultValue.DOUBLE_PRESS_INTERVAL);
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

    /*@Override
    public void sendToUi(String message) {
        Toast.makeText(this, "Message received:" + message, Toast.LENGTH_SHORT).show();
    }*/

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
//                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (statusOfGPS) {
                    CommonUtil.dismissDialog();
                } else {
                    CommonUtil.showGpsOrLocationOffPopup(MainActivity.this);
                }
            }
        }
    };

    public void checkPlayStoreAppUpdate() {
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
                RmDataHelper.getInstance().appUpdateFromOtherServer();
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

                    } /*else {
                        Log.i("AppUpdateProcess", "InstallStateUpdatedListener: state: " + state.installStatus());
                    }*/
                }
            };

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
            RmDataHelper.getInstance().mLatitude = "22.8456";
            RmDataHelper.getInstance().mLongitude = "89.5403";

            MessageFeedFragment messageFeedFragment = (MessageFeedFragment) mCurrentFragment;
            messageFeedFragment.swipeRefreshOperation();
        }
    }
}
