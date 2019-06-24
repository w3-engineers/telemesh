package com.w3engineers.unicef.telemesh.data.local.bulletintrack;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.unicef.telemesh.TeleMeshBulletinOuterClass.TeleMeshBulletin;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

/**
 * This class represents BroadcastMessage which is broadcast through the local mesh.
 * <p>
 * This message is broadcast by BaseStation(Net enable device)
 * </p>
 */

@Entity(tableName = TableNames.BULLETIN_TRACK,
        indices = {@Index(value = {ColumnNames.COLUMN_BULLETIN_MESSAGE_ID,
                ColumnNames.COLUMN_BULLETIN_TRACK_USER_ID}, unique = true)},
        foreignKeys = @ForeignKey(entity = FeedEntity.class,
        parentColumns = ColumnNames.COLUMN_FEED_ID,
        childColumns = ColumnNames.COLUMN_BULLETIN_MESSAGE_ID))
public class BulletinTrackEntity extends DbBaseEntity implements Parcelable {

    // This id will be used for uniqueness of a message in future
    @ColumnInfo(name = ColumnNames.COLUMN_BULLETIN_MESSAGE_ID)
    @Nullable
    public String bulletinMessageId;

    @ColumnInfo(name = ColumnNames.COLUMN_BULLETIN_TRACK_USER_ID)
    @Nullable
    public String bulletinTrackUserId;

    @ColumnInfo(name = ColumnNames.COLUMN_BULLETIN_ACK_STATUS)
    public int bulletinAckStatus;

    @ColumnInfo(name = ColumnNames.COLUMN_BULLETIN_OWNER_STATUS)
    public int bulletinOwnerStatus;

    // Empty constructor for Room database
    public BulletinTrackEntity() {

    }

    @Nullable
    public String getBulletinMessageId() {
        return bulletinMessageId;
    }

    @Nullable
    public String getBulletinTrackUserId() {
        return bulletinTrackUserId;
    }

    public int getBulletinAckStatus() {
        return bulletinAckStatus;
    }

    public int getBulletinOwnerStatus() {
        return bulletinOwnerStatus;
    }

    @NonNull
    public BulletinTrackEntity setBulletinMessageId(@Nullable String bulletinMessageId) {
        this.bulletinMessageId = bulletinMessageId;
        return this;
    }

    @NonNull
    public BulletinTrackEntity setBulletinTrackUserId(@Nullable String bulletinTrackUserId) {
        this.bulletinTrackUserId = bulletinTrackUserId;
        return this;
    }

    @NonNull
    public BulletinTrackEntity setBulletinAckStatus(int bulletinAckStatus) {
        this.bulletinAckStatus = bulletinAckStatus;
        return this;
    }

    @NonNull
    public BulletinTrackEntity setBulletinOwnerStatus(int bulletinOwnerStatus) {
        this.bulletinOwnerStatus = bulletinOwnerStatus;
        return this;
    }

    protected BulletinTrackEntity(@NonNull Parcel in) {
        mId = in.readLong();
        bulletinMessageId = in.readString();
        bulletinTrackUserId = in.readString();
        bulletinAckStatus = in.readInt();
        bulletinOwnerStatus = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(bulletinMessageId);
        dest.writeString(bulletinTrackUserId);
        dest.writeInt(bulletinAckStatus);
        dest.writeInt(bulletinOwnerStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BulletinTrackEntity> CREATOR = new Creator<BulletinTrackEntity>() {
        @Override
        public BulletinTrackEntity createFromParcel(Parcel in) {
            return new BulletinTrackEntity(in);
        }

        @Override
        public BulletinTrackEntity[] newArray(int size) {
            return new BulletinTrackEntity[size];
        }
    };
}
