package com.w3engineers.unicef.util.base.ui;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.disposables.CompositeDisposable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private final static int DEFAULT_ID_VALUE = 0;
    private ViewDataBinding mViewDataBinding;
    private CompositeDisposable mCompositeDisposable;

    /*
     * Child class have to implement this method. On this method you will pass the layout file of current fragment
     * */
    protected abstract int getLayoutId();

    /*
     * Child class can(optional) override this method. On this method you will pass the menu file of current fragment
     * */
    protected int getMenuId() {
        return DEFAULT_ID_VALUE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getMenuId() > DEFAULT_ID_VALUE) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getMenuId() > DEFAULT_ID_VALUE) {
            inflater.inflate(getMenuId(), menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        int layoutId = getLayoutId();

        if (layoutId > DEFAULT_ID_VALUE) {

            mCompositeDisposable = new CompositeDisposable();

            return updateLayoutView(inflater, layoutId, container);
        }
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startUI();
    }

    private View updateLayoutView(LayoutInflater inflater, int layoutId, ViewGroup container) {
        View view = null;

        try {
            mViewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false);

            view = mViewDataBinding.getRoot();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mViewDataBinding == null) {
            view = inflater.inflate(layoutId, container, false);
        }

        return view;
    }

    @Override
    public void onClick(View view) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopUI();
    }

    private BaseActivity getBaseActivity() {
        return ((BaseActivity) getActivity());
    }

    private boolean isBaseActivityInstance() {
        return BaseActivity.class.isInstance(getActivity());
    }

    /**
     * To set title on toolbar
     *
     * @param title string value
     */
    protected void setTitle(String title) {
        if (isBaseActivityInstance()) getBaseActivity().setTitle(title);
    }

    /**
     * To set sub title on toolbar
     *
     * @param subtitle string value
     */
    protected void setSubtitle(String subtitle) {
        if (isBaseActivityInstance()) getBaseActivity().setSubtitle(subtitle);
    }

    /**
     * To set both title and subtitle in toolbar
     *
     * @param title    string value
     * @param subtitle string value
     */
    public void setToolbarText(String title, String subtitle) {
        if (isBaseActivityInstance()) getBaseActivity().setToolbarText(title, subtitle);
    }

    /**
     * To set click listener on any view, You can pass multiple view at a time
     * Reduces code
     *
     * @param views View as params
     */
    protected void setClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }

        //if (isBaseActivityInstance()) getBaseActivity().setClickListener(views);
    }

    protected RecyclerView initRecyclerView(Context context, RecyclerView recyclerView, BaseAdapter adapter) {
        if (recyclerView == null) return null;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }

    protected RecyclerView.Adapter getAdapter(RecyclerView recyclerView) {
        return recyclerView.getAdapter();
    }
}