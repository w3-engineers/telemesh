package com.w3engineers.unicef.telemesh.data.local.feed;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;

import com.w3engineers.unicef.telemesh.TeleMeshBulletinOuterClass.TeleMeshBulletin;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

/**
 * This class represents BroadcastMessage which is broadcast through the local mesh.
 * <p>
 * This message is broadcast by BaseStation(Net enable device)
 * </p>
 */

@Entity(tableName = TableNames.FEED,
        indices = {@Index(value = {ColumnNames.COLUMN_FEED_ID}, unique = true)})
public class FeedEntity extends DbBaseEntity implements Parcelable {
    // This id will be used for uniqueness of a message in future
    @ColumnInfo(name = ColumnNames.COLUMN_FEED_ID)
    public String feedId;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_PROVIDER_NAME)
    public String feedProviderName;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_PROVIDER_LOGO)
    public String feedProviderLogo;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_TITLE)
    public String feedTitle;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_DETAIL)
    public String feedDetail;


    @ColumnInfo(name = ColumnNames.COLUMN_FEED_TIME)
    public String feedTime;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_READ_STATUS)
    public boolean feedReadStatus;

    // Empty constructor for Room database
    public FeedEntity() {

    }

    public FeedEntity setFeedId(String feedId) {
        this.feedId = feedId;
        return this;
    }

    public FeedEntity setFeedProviderName(String feedProviderName) {
        this.feedProviderName = feedProviderName;
        return this;
    }

    public FeedEntity setFeedProviderLogo(String feedProviderLogo) {
        this.feedProviderLogo = feedProviderLogo;
        return this;
    }

    public FeedEntity setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
        return this;
    }

    public FeedEntity setFeedDetail(String feedDetail) {
        this.feedDetail = feedDetail;
        return this;
    }

    public FeedEntity setFeedTime(String feedTime) {
        this.feedTime = feedTime;
        return this;
    }

    public FeedEntity setFeedReadStatus(boolean feedReadStatus) {
        this.feedReadStatus = feedReadStatus;
        return this;
    }

    public String getFeedId() {
        return feedId;
    }

    public String getFeedProviderName() {
        return feedProviderName;
    }

    public String getFeedProviderLogo() {
        return feedProviderLogo;
    }

    public String getFeedTitle() {
        return feedTitle;
    }

    public String getFeedDetail() {
        return feedDetail;
    }

    public String getFeedTime() {
        return feedTime;
    }

    public boolean isFeedRead() {
        return feedReadStatus;
    }

    protected FeedEntity(Parcel in) {
        mId = in.readLong();
        feedId = in.readString();
        feedProviderName = in.readString();
        feedProviderLogo = in.readString();
        feedTitle = in.readString();
        feedDetail = in.readString();
        feedTime = in.readString();
        feedReadStatus = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(feedId);
        dest.writeString(feedProviderName);
        dest.writeString(feedProviderLogo);
        dest.writeString(feedTitle);
        dest.writeString(feedDetail);
        dest.writeString(feedTime);
        dest.writeByte((byte) (feedReadStatus ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FeedEntity> CREATOR = new Creator<FeedEntity>() {
        @Override
        public FeedEntity createFromParcel(Parcel in) {
            return new FeedEntity(in);
        }

        @Override
        public FeedEntity[] newArray(int size) {
            return new FeedEntity[size];
        }
    };


    @Override
    public String toString() {
        return "FeedEntity{" +
                "feedId='" + feedId + '\'' +
                ", feedProviderName='" + feedProviderName + '\'' +
                ", feedProviderLogo='" + feedProviderLogo + '\'' +
                ", feedTitle='" + feedTitle + '\'' +
                ", feedDetail='" + feedDetail + '\'' +
                ", feedTime=" + feedTime +
                ", feedReadStatus=" + feedReadStatus +
                ", id=" + mId +
                '}';
    }

    public BulletinFeed toBulletinFeed() {
        return new BulletinFeed()
                .setMessageBody(getFeedDetail())
                .setMessageId(getFeedId())
                .setCreatedAt(getFeedTime());
    }

    public FeedEntity toFeedEntity(BulletinFeed bulletinFeed) {
        return new FeedEntity().setFeedDetail(bulletinFeed.getMessageBody())
                .setFeedId(bulletinFeed.getMessageId())
                .setFeedTime(bulletinFeed.getCreatedAt());
    }

    public TeleMeshBulletin toTelemeshBulletin() {
        return TeleMeshBulletin.newBuilder()
                .setBulletinId(getFeedId())
                .setBulletinMessage(getFeedDetail())
                .setBulletinTime(getFeedTime())
                .build();
    }

    public FeedEntity toFeedEntity(TeleMeshBulletin teleMeshBulletin) {
        return new FeedEntity().setFeedDetail(teleMeshBulletin.getBulletinMessage())
                .setFeedId(teleMeshBulletin.getBulletinId())
                .setFeedTime(teleMeshBulletin.getBulletinTime());
    }
}
