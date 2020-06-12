package com.w3engineers.unicef.telemesh.data.local.grouptable;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;

public class GroupDataSource {
    private GroupDao mGroupDao;
    private ExecutorService mIoExecutor;
    private static GroupDataSource groupDataSource = new GroupDataSource();

    public static GroupDataSource getInstance() {
        return groupDataSource;
    }

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

    public Flowable<List<GroupEntity>> getGroupList() {
        return mGroupDao.getAllGroups();
    }

    public Flowable<GroupEntity> getLastCreatedGroup() {
        return mGroupDao.getLastCreatedGroup();
    }

    public GroupEntity getGroupById(String groupId) {
        return mGroupDao.getGroupById(groupId);
    }

    public List<GroupEntity> getGroupByUserId(String userId) {
        return mGroupDao.getGroupByUserId(userId);
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
