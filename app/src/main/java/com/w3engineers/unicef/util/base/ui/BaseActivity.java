package com.w3engineers.unicef.util.base.ui;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import io.reactivex.disposables.CompositeDisposable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    //FixMe: have to enable for commit Fragment in Activity
    protected BaseFragment mBaseCurrentFragment;
    private ViewDataBinding mViewDataBinding;
    private Menu mMenu;
    private Toolbar baseToolbar;
    private CompositeDisposable mCompositeDisposable;
    private final static int DEFAULT_ID_VALUE = 0;

    /*
     * Start Activity with intent
     * */
    protected static void runCurrentActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }

    /*
     * Child class have to implement this method. On this method you will pass the layout file of current activity
     * */
    protected abstract int getLayoutId();

    /*
     * Child class can(optional) override this method. On this method you will pass the toolbar id of current layout
     * */
    protected int getToolbarId() {
        return DEFAULT_ID_VALUE;
    }

    /*
     * Child class can(optional) override this method. On this method you will pass the menu file of current activity
     * */
    protected int getMenuId() {
        return DEFAULT_ID_VALUE;
    }

    /*
     * Child class can(optional) override this method. On this method you will pass the color id
     * */
    protected int statusBarColor() {
        return DEFAULT_ID_VALUE;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutId = getLayoutId();
        if (layoutId > DEFAULT_ID_VALUE) {

            mCompositeDisposable = new CompositeDisposable();

            updateLayoutView(layoutId);

            setStatusBarColor();

            int toolbarId = getToolbarId();
            if (toolbarId > DEFAULT_ID_VALUE) {
                baseToolbar = findViewById(toolbarId);
                setSupportActionBar(baseToolbar);
                setToolbarAction();
            }
        }
        startUI();
    }

    public void setToolbarAction() {
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void updateLayoutView(int layoutId) {
        try {
            mViewDataBinding = DataBindingUtil.setContentView(this, layoutId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mViewDataBinding == null) {
            setContentView(layoutId);
        }
    }

    /*
     * Child class have to implement this method. This method run on onStart lifecycle
     * */
    protected abstract void startUI();

    /*
     * Child class have to implement this method. This method run on onDestroy lifecycle
     * */
    protected void stopUI() {}

    /*
     * Return current viewDataBinding
     * */
    protected ViewDataBinding getViewDataBinding() {
        return mViewDataBinding;
    }

    private void setStatusBarColor() {

        int statusBarColor = statusBarColor();

        if (statusBarColor > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, statusBarColor));
            }
        }
    }

    @Override
    public void onClick(View view) {
    }

    /*
     * To get the current menu. It will return current menu if you set it. Otherwise return null.
     **/
    protected Menu getMenu() {
        return mMenu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenuId() > DEFAULT_ID_VALUE) {
            getMenuInflater().inflate(getMenuId(), menu);
            this.mMenu = menu;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void refreshMenu() {
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopUI();
    }

    /**
     * To set title on toolbar
     *
     * @param title string value
     */
    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    /**
     * To set sub title on toolbar
     *
     * @param subtitle string value
     */
    public void setSubtitle(String subtitle) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    /**
     * To set both title and subtitle in toolbar
     *
     * @param title    string value
     * @param subtitle string value
     */
    public void setToolbarText(String title, String subtitle) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setSubtitle(subtitle);
        }
    }

    /**
     * To set click listener on any view, You can pass multiple view at a time
     *
     * @param views View as params
     */
    protected void setClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    /**
     * Commit child fragment of BaseFragment on a frameLayout
     *
     * @param viewId       int value
     * @param baseFragment BaseFragment object
     */
    protected void commitFragment(int viewId, BaseFragment baseFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(viewId, baseFragment, baseFragment.getClass().getName())
                .commit();

        setCurrentFragment(baseFragment);
    }

    /*
     * Get current running fragment
     * */
    protected BaseFragment getCurrentFragment() {
        return mBaseCurrentFragment;
    }

    private void setCurrentFragment(BaseFragment baseFragment) {
        this.mBaseCurrentFragment = baseFragment;
    }
}
