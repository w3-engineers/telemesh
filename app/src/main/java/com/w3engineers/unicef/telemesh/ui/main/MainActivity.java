package com.w3engineers.unicef.telemesh.ui.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.work.WorkInfo;

import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.BaseServiceLocator;
import com.w3engineers.mesh.application.ui.base.TelemeshBaseActivity;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMainBinding;
import com.w3engineers.unicef.telemesh.databinding.NotificationBadgeBinding;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactsFragment;
import com.w3engineers.unicef.telemesh.ui.meshdiscovered.DiscoverFragment;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedFragment;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;


public class MainActivity extends TelemeshBaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private MainActivityViewModel mViewModel;
    private boolean doubleBackToExitPressedOnce = false;
    private Menu bottomMenu;
    private BottomNavigationMenuView bottomNavigationMenuView;
    NotificationBadgeBinding notificationBadgeBinding;
    private static MainActivity sInstance;
    private BulletinTimeScheduler sheduler;
    private Fragment mCurrentFragment;

    private int latestUserCount;
    private int latestMessageCount;

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
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
    }


    @Override
    protected void startUI() {
        binding = (ActivityMainBinding) getViewDataBinding();
        sInstance = this;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        Constants.IS_LOADING_ENABLE = false;
        mainActivity = this;

        sheduler = BulletinTimeScheduler.getInstance().connectivityRegister();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        bottomMenu = binding.bottomNavigation.getMenu();
        initBottomBar();
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
            if (workInfos == null || workInfos.isEmpty()) {
                return;
            }

            // We only care about the first output status.
            WorkInfo workInfo = workInfos.get(0);

            boolean finished = workInfo.getState().isFinished();


        });

        mViewModel.getMyUserMode().observe(this, integer -> {
            if (integer == null) return;
            showHideInternetWarning(integer, Constants.IS_DATA_ON);
        });

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

    }

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onResume() {
        super.onResume();
        int myMode = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readInt(Constants.preferenceKey.MY_MODE);
        initNoNetworkCallback(myMode);
        showHideInternetWarning(myMode, Constants.IS_DATA_ON);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.text_view_background:
                // disableLoading();
                break;
            case R.id.image_view_cross:
                if(TextUtils.isEmpty(binding.searchBar.editTextSearch.getText())){
                    hideSearchBar();
                }else{
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

    private void initBottomBar() {

        boolean fromSettings = getIntent().getBooleanExtra(MainActivity.class.getSimpleName(), false);
        Log.d("BottomTest", "settings: " + fromSettings);

        /*boolean isRestart = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readBoolean(Constants.preferenceKey.IS_RESTART);
        if (isRestart) {
            fromSettings = true;
            SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.IS_RESTART, false);
        }*/

        Fragment mFragment = null;
        if (fromSettings) {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_setting);
            menuItem.setChecked(true);
            mFragment = new SettingsFragment();
        } else {
            MenuItem menuItem = bottomMenu.findItem(R.id.action_discover);
            menuItem.setChecked(true);
            mFragment = new DiscoverFragment();
        }
        mCurrentFragment = mFragment;
        loadFragment(mFragment, getString(R.string.title_discoverd_fragment));

        bottomNavigationMenuView = (BottomNavigationMenuView) binding.bottomNavigation
                .getChildAt(Constants.MenuItemPosition.POSITION_FOR_CONTACT);

        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_CONTACT);
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
                toolbarTitle = getString(R.string.title_discoverd_fragment);
                mFragment = new DiscoverFragment();
                hideUserBadge();
                break;
            case R.id.action_contact:
                toolbarTitle = getString(R.string.title_personal_fragment);
                mFragment = new MeshContactsFragment();
                hideUserBadge();
                break;
            case R.id.action_message_feed:
                toolbarTitle = getString(R.string.title_message_feed_fragment);
              /*  createBadgeCount(Constants.DefaultValue.INTEGER_VALUE_ZERO
                        , Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);*/
                mFragment = new MessageFeedFragment();
                hideFeedBadge();
                break;
            case R.id.action_setting:
                toolbarTitle = getString(R.string.title_settings_fragment);
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

    // Again this api will be enable when its functionality will be added
    public void createBadgeCount(int latestCount, int menuItemPosition) {
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

       /* if (latestCount > Constants.DefaultValue.INTEGER_VALUE_ZERO) {

            constraintLayoutContainer.setVisibility(View.VISIBLE);

            if (latestCount <= Constants.DefaultValue.MAXIMUM_BADGE_VALUE) {
                textViewBadgeCount.setText(String.valueOf(latestCount));
            } else {
                textViewBadgeCount.setText(R.string.badge_count_more_than_99);
            }
        } else {
            constraintLayoutContainer.setVisibility(View.GONE);
        }*/
    }

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

    private void hideUserBadge() {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(Constants.MenuItemPosition.POSITION_FOR_CONTACT);

        if (itemView == null) {
            return;
        }
        ConstraintLayout userBadgeView = itemView.findViewById(R.id.constraint_layout_badge);

        userBadgeView.setVisibility(View.GONE);
    }

    private void hideFeedBadge() {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);

        if (itemView == null) {
            return;
        }
        ConstraintLayout feedBadge = itemView.findViewById(R.id.constraint_layout_badge);

        feedBadge.setVisibility(View.GONE);
    }

    public void enableLoading() {
        binding.searchingView.setVisibility(View.VISIBLE);
        binding.mainView.setVisibility(View.GONE);
    }

    public void disableLoading() {
        binding.searchingView.setVisibility(View.GONE);
        binding.mainView.setVisibility(View.VISIBLE);
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
//        mViewModel.userOfflineProcess();
        sInstance = null;
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
        Toaster.showShort(getString(R.string.double_press_exit));
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, Constants.DefaultValue.DOUBLE_PRESS_INTERVAL);
    }

    private void subscribeForActiveUser() {
        if (mViewModel != null) {
            mViewModel.getActiveUser().observe(this, userEntities -> {
                runOnUiThread(() -> createBadgeCount(userEntities.size(), Constants.MenuItemPosition.POSITION_FOR_CONTACT));
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
                }else if (mCurrentFragment != null
                        && mCurrentFragment instanceof DiscoverFragment){
                    ((DiscoverFragment) mCurrentFragment).searchContacts(s.toString());
                }
            }
        });
    }

    /*@Override
    public void sendToUi(String message) {
        Toast.makeText(this, "Message received:" + message, Toast.LENGTH_SHORT).show();
    }*/

}
