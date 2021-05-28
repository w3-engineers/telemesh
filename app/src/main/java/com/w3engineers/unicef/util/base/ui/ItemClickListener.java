package com.w3engineers.unicef.util.base.ui;

import android.view.View;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public interface ItemClickListener<T> {
    /**
     * Called when a item has been clicked.
     *
     * @param view The view that was clicked.
     * @param item The T type object that was clicked.
     */
    void onItemClick(View view, T item);


    /**
     * Developers might often need the index of item.
     * @param view
     * @param item
     * @param index
     */
    default void onItemClick(View view, T item, int index) { }
}
