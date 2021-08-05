package com.w3engineers.unicef.util.base.ui;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public abstract class BaseRxAndroidViewModel extends AndroidViewModel {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public BaseRxAndroidViewModel(@NonNull Application application) {
        super(application);
    }

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
