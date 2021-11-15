package com.w3engineers.unicef.telemesh.ui.createuser;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.util.base.ui.BaseRxAndroidViewModel;
import com.w3engineers.unicef.util.helper.ViperUtil;

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
public class CreateUserViewModel extends BaseRxAndroidViewModel {

    private int imageIndex = CreateUserActivity.INITIAL_IMAGE_INDEX;

    public CreateUserViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public MutableLiveData<String> firstNameChangeLiveData = new MutableLiveData<>();

    @NonNull
    public MutableLiveData<String> lastNameChangeLiveData = new MutableLiveData<>();
    
    void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    boolean storeData(@Nullable String firstName, String lastName) {

        if (imageIndex < 0) {
            imageIndex = Constants.DEFAULT_AVATAR;
        }

        //firstName = Util.convertToTitleCaseIteratingChars(firstName);


        SharedPref.write(Constants.preferenceKey.USER_NAME, firstName);
        SharedPref.write(Constants.preferenceKey.LAST_NAME, lastName);
        SharedPref.write(Constants.preferenceKey.IMAGE_INDEX, imageIndex);
//        sharedPref.write(Constants.preferenceKey.MY_PASSWORD, password);
        SharedPref.write(Constants.preferenceKey.MY_REGISTRATION_TIME, System.currentTimeMillis());
        SharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, true);

        // Save the information in service side.

        RmDataHelper.getInstance().saveMyInfo();

        return true;
    }

    boolean isNameValid(@Nullable String name) {
        return !TextUtils.isEmpty(name) && name != null &&
                name.length() >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT
                && name.length() <= Constants.DefaultValue.MAXIMUM_TEXT_LIMIT;
    }

    public void firstNameEditControl(@NonNull EditText editText) {
        getCompositeDisposable().add(RxTextView.afterTextChangeEvents(editText)
                .map(input -> input.editable() + "")
                .debounce(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(text -> firstNameChangeLiveData.postValue(text), Throwable::printStackTrace));
    }

    public void lastNameEditControl(@NonNull EditText editText) {
        getCompositeDisposable().add(RxTextView.afterTextChangeEvents(editText)
                .map(input -> input.editable() + "")
                .debounce(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(text -> lastNameChangeLiveData.postValue(text), Throwable::printStackTrace));
    }

    public void launchWalletPage(boolean isNeedToImportWallet) {
        if (isNeedToImportWallet) {
            ServiceLocator.getInstance().launchActivity(ViperUtil.WALLET_IMPORT_ACTIVITY);
        } else {
            ServiceLocator.getInstance().launchActivity(ViperUtil.WALLET_SECURITY_ACTIVITY);
        }
    }
}
