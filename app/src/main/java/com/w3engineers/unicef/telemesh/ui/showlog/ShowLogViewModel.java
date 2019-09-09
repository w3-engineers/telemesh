package com.w3engineers.unicef.telemesh.ui.showlog;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.util.helper.LogProcessUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ShowLogViewModel extends BaseRxAndroidViewModel {

    private MutableLiveData<List<MeshLogModel>> getFilteredList = new MutableLiveData<>();
    private MutableLiveData<List<MeshLogModel>> getFilteredListWithTag = new MutableLiveData<>();

    public ShowLogViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<MeshLogModel>> startAllLogObserver() {
        return LiveDataReactiveStreams.fromPublisher(LogProcessUtil.getInstance().getAllMeshLog());
    }

    public LiveData<MeshLogModel> startLogObserver() {
        return LiveDataReactiveStreams.fromPublisher(LogProcessUtil.getInstance().getMeshLog());
    }

    @NonNull
    public MutableLiveData<List<MeshLogModel>> getFilteredList() {
        return getFilteredList;
    }

    public void startSearch(@NonNull String searchText, @Nullable List<MeshLogModel> showLogEntities) {
        if (showLogEntities != null) {
            List<MeshLogModel> filteredItemList = new ArrayList<>();

            if (!TextUtils.isEmpty(searchText)) {
                for (MeshLogModel log : showLogEntities) {
                    if (log.getLog().toLowerCase().contains(searchText.toLowerCase())) {
                        filteredItemList.add(log);
                    }
                }
            } else  {
                filteredItemList.addAll(showLogEntities);
            }

            getFilteredList.postValue(filteredItemList);
        }
    }

    @NonNull
    public MutableLiveData<List<MeshLogModel>> getFilteredListWithTag() {
        return getFilteredListWithTag;
    }

    public void filterListWithTag(@NonNull int type, @Nullable List<MeshLogModel> meshLogModels) {
        if (meshLogModels != null) {
            if (type == 0) {
                getFilteredListWithTag().postValue(meshLogModels);
            } else {
                List<MeshLogModel> filteredItemList = new ArrayList<>();

                for (MeshLogModel log : meshLogModels) {
                    if (log.getType() == type) {
                        filteredItemList.add(log);
                    }
                }
                getFilteredListWithTag().postValue(filteredItemList);
            }
        }
    }
}
