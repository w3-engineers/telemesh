package com.w3engineers.unicef.telemesh.pager;


import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;

public class DataSourceFactory extends DataSource.Factory<Integer, ChatEntity> {

    private final ChatEntityListDataSource chatEntityListDataSource;
    private MutableLiveData<ChatEntityListDataSource> showsDataSourceLiveData;


    public DataSourceFactory(ChatEntityListDataSource dataSource) {
        this.chatEntityListDataSource = dataSource;
        showsDataSourceLiveData = new MutableLiveData<>();
    }


    @Override
    public DataSource<Integer, ChatEntity> create() {
        showsDataSourceLiveData.postValue(chatEntityListDataSource);
        return chatEntityListDataSource;
    }
}
