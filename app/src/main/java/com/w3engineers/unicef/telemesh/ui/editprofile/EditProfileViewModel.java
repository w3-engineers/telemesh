package com.w3engineers.unicef.telemesh.ui.editprofile;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.data.broadcast.Util;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.util.base.ui.BaseRxAndroidViewModel;

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

        int currentImageIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);

        if (imageIndex < 0) {
            if (currentImageIndex == Constants.DEFAULT_AVATAR) {
                imageIndex = Constants.DEFAULT_AVATAR;
            } else {
                imageIndex = currentImageIndex;
            }
        }

        userName = Util.convertToTitleCaseIteratingChars(userName);

        SharedPref.write(Constants.preferenceKey.USER_NAME, userName);
        SharedPref.write(Constants.preferenceKey.IMAGE_INDEX, imageIndex);
        SharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, true);

        return true;
    }

    void sendUserInfoToAll() {
        int currentImageIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
        String name = SharedPref.read(Constants.preferenceKey.USER_NAME);
        RmDataHelper.getInstance().broadcastUpdateProfileInfo(name, currentImageIndex);
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
