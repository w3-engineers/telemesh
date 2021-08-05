package com.w3engineers.unicef.util.base.ui;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {
    private final List<T> mItemList;
    protected ItemClickListener mItemClickListener;


    public BaseAdapter() {
        mItemList = new ArrayList<T>();
    }

    public abstract boolean isEqual(T left, T right);

    public abstract BaseViewHolder newViewHolder(ViewGroup parent, int viewType);


    /**
     * Commit child fragment of BaseFragment on a frameLayout
     *
     * @param itemClickListener ItemClickListener object
     * @return void
     */
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return newViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
        T itemData = getItem(position);

        ViewDataBinding viewDataBinding = holder.getViewDataBinding();

        holder.bind(itemData, viewDataBinding);
    }

    /**
     * Clear current item list and update UI
     *
     * @return void
     */
    public void clear() {
        mItemList.clear();
        notifyDataSetChanged();
    }

    /**
     * @return current item list
     */
    public List<T> getItems() {
        return mItemList;
    }


    /**
     * Remove a Item from list and update UI
     *
     * @param t T type object
     * @return void
     */
    public void removeItem(T t) {

        int toIndex = mItemList.indexOf(t);
        if (toIndex < 0 || toIndex >= mItemList.size()) return;
        mItemList.remove(toIndex);
        notifyDataSetChanged();
    }

    /**
     * @param position int value
     * @return get current Item based on Position
     */
    public T getItem(int position) {
        if (position < 0 || position >= mItemList.size()) {
            return null;
        }
        try {
            return mItemList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

   /* public void addItem(T item) {

        int position = 0;
        addItem(item, position);

    }*/

    /**
     * @param item T type object
     * @return int value: current item inserted position in list
     */
    public int addItem(T item) {
        T tItem = findItem(item);

        if (tItem == null) {
            mItemList.add(item);
            notifyDataSetChanged();
//            notifyItemInserted(mItemList.size() - 1);
            return mItemList.size() - 1;
        }
        return updateItem(tItem, item);
    }

    /**
     * @param items List<T> type object list
     * @return void
     */
    public void addItem(List<T> items) {
        for (T item : items) {
            addItem(item);
        }
    }

    /**
     * @param item     T type object
     * @param position int value of position where value will be inserted
     * @return void
     */
    public void addItemToPosition(T item, int position) {
        mItemList.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * @param item T type object
     * @return if found then item from list otherwise null
     */
    public T findItem(T item) {
        for (T tItem : mItemList) {
            if (isEqual(item, tItem)) {
                return tItem;
            }
        }
        return null;
    }

    /**
     * @param items List<T> type object list
     * @return int value: current item inserted position in list
     */
    public void addItems(List<T> items) {
        for (T item : items) {
            addItem(item);
        }
    }

    /**
     * @param oldItem T type object
     * @param newItem T type object
     * @return int value: newItem position in list
     */
    public int updateItem(T oldItem, T newItem) {
        int toIndex = mItemList.indexOf(oldItem);
        mItemList.set(toIndex, newItem);

        notifyItemChanged(toIndex);
        return toIndex;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public ViewDataBinding inflate(ViewGroup viewGroup, int item_layout) {
        return DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), item_layout, viewGroup, false);
    }

}