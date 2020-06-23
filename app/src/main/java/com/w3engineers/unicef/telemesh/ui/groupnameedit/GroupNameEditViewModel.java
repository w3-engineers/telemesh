package com.w3engineers.unicef.telemesh.ui.groupnameedit;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GroupNameEditViewModel extends BaseRxAndroidViewModel {
    private DataSource dataSource;

    public GroupNameEditViewModel(@NonNull Application application) {
        super(application);
        dataSource = Source.getDbSource();
    }

    @NonNull
    public MutableLiveData<String> textChangeLiveData = new MutableLiveData<>();

    void textEditControl(@NonNull EditText editText) {
        getCompositeDisposable().add(RxTextView.afterTextChangeEvents(editText)
                .map(input -> input.editable() + "")
                .debounce(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(text -> textChangeLiveData.postValue(text), Throwable::printStackTrace));
    }

    void updateGroupName(String groupName, String groupId) {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupName(groupName);
        groupModel.setGroupId(groupId);
        groupModel.setInfoId(UUID.randomUUID().toString());
        dataSource.setGroupRenameEvent(groupModel);
    }
}
