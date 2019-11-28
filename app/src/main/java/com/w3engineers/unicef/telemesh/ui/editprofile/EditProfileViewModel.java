package com.w3engineers.unicef.telemesh.ui.editprofile;

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

public class EditProfileViewModel extends BaseRxAndroidViewModel {

    private int imageIndex = EditProfileActivity.INITIAL_IMAGE_INDEX;

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public MutableLiveData<String> textChangeLiveData = new MutableLiveData<>();

    int getImageIndex() {
        return imageIndex;
    }

    void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    boolean storeData(@Nullable String userName) {

        // Store name and image on PrefManager
        SharedPref sharedPref = SharedPref.getSharedPref(getApplication().getApplicationContext());

        if (imageIndex < 0) {
            imageIndex = 1;
        }

        sharedPref.write(Constants.preferenceKey.USER_NAME, userName);
        sharedPref.write(Constants.preferenceKey.IMAGE_INDEX, imageIndex);
        sharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, true);

        return true;
    }

    boolean isNameValid(@Nullable String name) {
        return !TextUtils.isEmpty(name) && name != null &&
                name.length() >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT
                && name.length() <= Constants.DefaultValue.MAXIMUM_TEXT_LIMIT;
    }

    public void textEditControl(@NonNull EditText editText) {
        getCompositeDisposable().add(RxTextView.afterTextChangeEvents(editText)
                .map(input -> input.editable() + "")
                .debounce(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(text -> textChangeLiveData.postValue(text), Throwable::printStackTrace));
    }
}
