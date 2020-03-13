/*
package com.w3engineers.unicef.telemesh.ui.security;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.data.broadcast.Util;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SecurityViewModel extends BaseRxAndroidViewModel {

    @NonNull
    public MutableLiveData<String> textChangeLiveData = new MutableLiveData<>();


    public SecurityViewModel(@NonNull Application application) {
        super(application);
    }

    boolean storeData(@Nullable String userName, int imageIndex, String password, String address, String publickKey) {

        if (imageIndex < 0) {
            imageIndex = Constants.DEFAULT_AVATAR;
        }

        // Store name and image on PrefManager
        SharedPref sharedPref = SharedPref.getSharedPref(getApplication().getApplicationContext());

        userName = Util.convertToTitleCaseIteratingChars(userName);

        sharedPref.write(Constants.preferenceKey.USER_NAME, userName);
        sharedPref.write(Constants.preferenceKey.IMAGE_INDEX, imageIndex);
        sharedPref.write(Constants.preferenceKey.MY_PASSWORD, password);
        sharedPref.write(Constants.preferenceKey.MY_WALLET_ADDRESS, address);
        sharedPref.write(Constants.preferenceKey.MY_PUBLIC_KEY, publickKey);
        sharedPref.write(Constants.preferenceKey.MY_REGISTRATION_TIME, System.currentTimeMillis());
        sharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, true);

        return true;
    }

    void textEditControl(@NonNull EditText editText) {
        getCompositeDisposable().add(RxTextView.afterTextChangeEvents(editText)
                .map(input -> input.editable() + "")
                .debounce(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(text -> textChangeLiveData.postValue(text), Throwable::printStackTrace));
    }

    boolean isValidPassword(final String password) {

        if (password != null) {
            Pattern pattern;
            Matcher matcher;

            final String PASSWORD_PATTERN = "^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[@$%&#_()=+?»«<>£§€{}\\\\[\\\\]-]).{8,}$";

            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);

            return matcher.matches();
        } else {
            return false;
        }
    }

    boolean isValidChar(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*?[A-Za-z]).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    boolean isDigitPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*?[0-9]).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    boolean isValidSpecial(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*?[@$%&#_()=+?»«<>£§€{}\\\\[\\\\]-]).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
*/
