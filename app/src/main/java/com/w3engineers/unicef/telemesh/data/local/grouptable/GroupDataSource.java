package com.w3engineers.unicef.telemesh.data.local.grouptable;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupDataSource {
    private GroupDao mGroupDao;
    private ExecutorService mIoExecutor;
    private static GroupDataSource groupDataSource = new GroupDataSource();

    private GroupDataSource() {
        mGroupDao = AppDatabase.getInstance().groupDao();
        mIoExecutor = Executors.newSingleThreadExecutor();
    }

    public long insertOrUpdateGroup(GroupEntity entity) {
        Callable<Long> insertCallable = () -> mGroupDao.insertOrUpdate(entity);

        try {
            return mIoExecutor.submit(insertCallable).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<GroupEntity> getGroupList() {
        return mGroupDao.getAllGroups();
    }

    public boolean joinAGroup(String groupId, String userId) {
        //Todo we have to write some query in dao
        return false;
    }

    public boolean leaveAGroup(String groupId, String userId) {
        //Todo we have to write some query in dao
        return false;
    }
}
