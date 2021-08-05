package com.w3engineers.unicef.telemesh.ui.meshcontact;

import androidx.paging.PositionalDataSource;
import androidx.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;

import java.util.ArrayList;
import java.util.List;

public class GroupPositionalDataSource extends PositionalDataSource<GroupEntity> {

    private List<GroupEntity> groupEntities;

    public GroupPositionalDataSource(@NonNull List<GroupEntity> list){
        this.groupEntities = list;

    }

    private int computeCount() {
        // actual count code here
        return groupEntities.size();
    }

    private List<GroupEntity> loadRangeInternal(int startPosition, int loadCount) {
        // actual load code here
        List<GroupEntity> modelList = new ArrayList<>();
        int end = Math.min(computeCount(), startPosition + loadCount);
        for (int i = startPosition; i != end; i++) {
            modelList.add(groupEntities.get(i));
        }

        return modelList;

    }



    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<GroupEntity> callback) {
        // return info back to PagedList
        int totalCount = computeCount();
        int position = computeInitialLoadPosition(params, totalCount);
        int loadSize = computeInitialLoadSize(params, position, totalCount);
        callback.onResult(loadRangeInternal(position, loadSize), position, totalCount);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<GroupEntity> callback) {
        // return info back to PagedList
        callback.onResult(loadRangeInternal(params.startPosition, params.loadSize));
    }
}
