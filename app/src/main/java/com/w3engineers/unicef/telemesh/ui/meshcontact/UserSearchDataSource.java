package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserSearchDataSource extends PositionalDataSource<UserEntity> {

    private List<UserEntity> userEntities;

    public UserSearchDataSource(@NonNull List<UserEntity> list){
        this.userEntities = list;

    }

    private int computeCount() {
        // actual count code here
        return userEntities.size();
    }

    private List<UserEntity> loadRangeInternal(int startPosition, int loadCount) {
        // actual load code here
        List<UserEntity> modelList = new ArrayList<>();
        int end = Math.min(computeCount(), startPosition + loadCount);
        for (int i = startPosition; i != end; i++) {
            modelList.add(userEntities.get(i));
        }

        return modelList;

    }



    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<UserEntity> callback) {
        // return info back to PagedList
        int totalCount = computeCount();
        int position = computeInitialLoadPosition(params, totalCount);
        int loadSize = computeInitialLoadSize(params, position, totalCount);
        callback.onResult(loadRangeInternal(position, loadSize), position, totalCount);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<UserEntity> callback) {
        // return info back to PagedList
        callback.onResult(loadRangeInternal(params.startPosition, params.loadSize));
    }
}
