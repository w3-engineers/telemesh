package com.w3engineers.unicef.telemesh.data.pager;

import androidx.paging.PositionalDataSource;
import androidx.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * PositionalDataSource To be used when you have defined set of data i.e.
 * the number of items in the data is fixed (say a large data set)
 * and you want to paginate on that data.
 */
public class ChatEntityListDataSource extends PositionalDataSource<ChatEntity> {

    private List<ChatEntity> chatList;

    public ChatEntityListDataSource(@NonNull List<ChatEntity> list){
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


    /**
     * Load initial list data.
     *
     * This method is called to load the initial page(s) from the DataSource.
     * @param params -
     * @param callback -
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ChatEntity> callback) {
        // return info back to PagedList
        int totalCount = computeCount();
        int position = computeInitialLoadPosition(params, totalCount);
        int loadSize = computeInitialLoadSize(params, position, totalCount);
        callback.onResult(loadRangeInternal(position, loadSize), position, totalCount);
    }

    /**
     * Called to load a range of data from the DataSource.
     *
     * This method is called to load additional pages from the DataSource
     * after the LoadInitialCallback passed to dispatchLoadInitial has initialized a PagedList.
     * @param params -
     * @param callback -
     */

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ChatEntity> callback) {
        // return info back to PagedList
        callback.onResult(loadRangeInternal(params.startPosition, params.loadSize));
    }
}
