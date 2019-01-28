package com.w3engineers.unicef.telemesh.pager;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;

import java.util.ArrayList;
import java.util.List;

public class ChatEntityListDataSource extends PositionalDataSource<ChatEntity> {

    private List<ChatEntity> chatList;

    public ChatEntityListDataSource(List<ChatEntity> list){
        this.chatList = list;

    }

    private int computeCount() {
        // actual count code here
        return chatList.size();
    }

    private List<ChatEntity> loadRangeInternal(int startPosition, int loadCount) {
        // actual load code here
        List<ChatEntity> modelList = new ArrayList<>();
        int end = Math.min(computeCount(), startPosition + loadCount);
        for (int i = startPosition; i != end; i++) {
            modelList.add(chatList.get(i));
        }

        return modelList;

    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ChatEntity> callback) {
        // return info back to PagedList
        int totalCount = computeCount();
        int position = computeInitialLoadPosition(params, totalCount);
        int loadSize = computeInitialLoadSize(params, position, totalCount);
        callback.onResult(loadRangeInternal(position, loadSize), position, totalCount);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ChatEntity> callback) {
        // return info back to PagedList
        callback.onResult(loadRangeInternal(params.startPosition, params.loadSize));
    }
}
