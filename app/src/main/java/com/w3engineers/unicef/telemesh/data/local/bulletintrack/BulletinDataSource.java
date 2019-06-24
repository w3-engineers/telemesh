package com.w3engineers.unicef.telemesh.data.local.bulletintrack;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;

import java.util.List;

import io.reactivex.Single;

public class BulletinDataSource {

    BulletinTrackDao bulletinTrackDao;

    private static BulletinDataSource bulletinDataSource = new BulletinDataSource();

    private BulletinDataSource() {
        bulletinTrackDao = AppDatabase.getInstance().bulletinTrackDao();
    }

    public static BulletinDataSource getInstance() {
        return bulletinDataSource;
    }

    public long insertOrUpdate(BulletinTrackEntity bulletinTrackEntity) {
        return bulletinTrackDao.insertBulletin(bulletinTrackEntity);
    }

    public Single<List<FeedEntity>> getUnsentMessage(String userId) {
        return bulletinTrackDao.getUnsentMessage(userId);
    }

    public Single<List<BulletinTrackEntity>> getAllSuccessBulletin() {
        return bulletinTrackDao.getAllSuccessBulletin();
    }

    public Single<Integer> setFullSuccess(String messageId, String userId) {
        return Single.fromCallable(() -> bulletinTrackDao.setFullSuccess(messageId, userId));
    }
}
