package com.w3engineers.unicef.telemesh.data.local.bulletintrack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @NonNull
    public static BulletinDataSource getInstance() {
        return bulletinDataSource;
    }

    public long insertOrUpdate(@NonNull BulletinTrackEntity bulletinTrackEntity) {
        return bulletinTrackDao.insertBulletin(bulletinTrackEntity);
    }

    @Nullable
    public Single<List<FeedEntity>> getUnsentMessage(@NonNull String userId) {
        return bulletinTrackDao.getUnsentMessage(userId);
    }

    @Nullable
    public Single<List<BulletinTrackEntity>> getAllSuccessBulletin() {
        return bulletinTrackDao.getAllSuccessBulletin();
    }

    @NonNull
    public Single<Integer> setFullSuccess(@NonNull String messageId, @NonNull String userId) {
        return Single.fromCallable(() -> bulletinTrackDao.setFullSuccess(messageId, userId));
    }
}
