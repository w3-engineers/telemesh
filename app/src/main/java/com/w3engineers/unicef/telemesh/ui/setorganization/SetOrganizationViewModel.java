package com.w3engineers.unicef.telemesh.ui.setorganization;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class SetOrganizationViewModel extends BaseRxAndroidViewModel {

    private SharedPref sharedPref;

    public SetOrganizationViewModel(@NonNull Application application) {
        super(application);
        sharedPref = SharedPref.getSharedPref(getApplication().getApplicationContext());
    }

    public MutableLiveData<String> textChangeLiveData = new MutableLiveData<>();

    int getImageIndex() {
        return sharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
    }

    String getName() {
        return sharedPref.read(Constants.preferenceKey.USER_NAME);
    }

    boolean isInfoValid(@Nullable String name) {
        return !TextUtils.isEmpty(name) && name != null &&
                name.length() >= Constants.DefaultValue.MINIMUM_INFO_LIMIT;
    }

    boolean storeData(@Nullable String companyName, @NonNull String companyId) {

        // Store name and image on PrefManager
        SharedPref sharedPref = SharedPref.getSharedPref(getApplication().getApplicationContext());

        sharedPref.write(Constants.preferenceKey.COMPANY_NAME, companyName);
        sharedPref.write(Constants.preferenceKey.COMPANY_ID, companyId);
        sharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, true);
        return true;
    }

    public void textOrganizationEditControl(EditText editText) {
        getCompositeDisposable().add(RxTextView.afterTextChangeEvents(editText)
                .map(input -> input.editable() + "")
                .debounce(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(text -> textChangeLiveData.postValue(text)));
    }

    public void textIdEditControl(EditText editText) {
        getCompositeDisposable().add(RxTextView.afterTextChangeEvents(editText)
                .map(input -> input.editable() + "")
                .debounce(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(text -> textChangeLiveData.postValue(text)));
    }
}
