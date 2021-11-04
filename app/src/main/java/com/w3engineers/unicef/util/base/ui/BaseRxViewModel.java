package com.w3engineers.unicef.util.base.ui;

import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public abstract class BaseRxViewModel extends ViewModel {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    protected CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

}
