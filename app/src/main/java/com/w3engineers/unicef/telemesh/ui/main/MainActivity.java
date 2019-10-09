package com.w3engineers.unicef.telemesh.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.work.WorkInfo;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.ui.base.rm.RmBaseActivity;
import com.w3engineers.mesh.util.DiagramUtil;
import com.w3engineers.mesh.util.HandlerUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMainBinding;
import com.w3engineers.unicef.telemesh.databinding.NotificationBadgeBinding;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactsFragment;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedFragment;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;
import com.w3engineers.unicef.util.helper.LocationUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends RmBaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private MainActivityViewModel mViewModel;
    private boolean doubleBackToExitPressedOnce = false;
    private Menu bottomMenu;
    private BottomNavigationMenuView bottomNavigationMenuView;
    NotificationBadgeBinding notificationBadgeBinding;
    private static MainActivity sInstance;

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
        sInstance = this;
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

        // set new user count analytics so that the work manager will trigger
        mViewModel.setUserCountWorkRequest();
        mViewModel.setServerAppShareCountWorkerRequest();
        mViewModel.setLocalAppShareCountWorkerRequest();

        mViewModel.getNewUserWorkInfo().observe(this, workInfos -> {

            // If there are no matching work info, do nothing
            if (workInfos == null || workInfos.isEmpty()) {
                return;
            }

            // We only care about the first output status.
            WorkInfo workInfo = workInfos.get(0);

            boolean finished = workInfo.getState().isFinished();


        });

        subscribeForActiveUser();

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
        DiagramUtil.on(this).start();

        InAppUpdate.getInstance(MainActivity.this).setAppUpdateProcess(false);
    }

    public static MainActivity getInstance() {
        return sInstance;
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

        bottomNavigationMenuView = (BottomNavigationMenuView) binding.bottomNavigation
                .getChildAt(Constants.MenuItemPosition.POSITION_FOR_CONTACT);

        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_CONTACT);

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
    public void createBadgeCount(int latestCount, int menuItemPosition) {
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);

        if (itemView == null) {
            return;
        }
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

    @Override
    protected void onDestroy() {
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

    private void subscribeForActiveUser() {
        if (mViewModel != null) {
            mViewModel.getActiveUser().observe(this, userEntities -> {
                runOnUiThread(() -> createBadgeCount(userEntities.size(), Constants.MenuItemPosition.POSITION_FOR_CONTACT));
            });
        }
    }

    /*@Override
    public void sendToUi(String message) {
        Toast.makeText(this, "Message received:" + message, Toast.LENGTH_SHORT).show();
    }*/

}
