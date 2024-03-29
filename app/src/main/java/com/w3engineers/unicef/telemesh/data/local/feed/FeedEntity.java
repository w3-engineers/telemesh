package com.w3engineers.unicef.telemesh.data.local.feed;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    @Nullable
    public String feedId;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_PROVIDER_NAME)
    @Nullable
    public String feedProviderName;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_PROVIDER_LOGO)
    @Nullable
    public String feedProviderLogo;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_TITLE)
    @Nullable
    public String feedTitle;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_DETAIL)
    @Nullable
    public String feedDetail;


    @ColumnInfo(name = ColumnNames.COLUMN_FEED_TIME)
    @Nullable
    public String feedTime;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_READ_STATUS)
    @Nullable
    public boolean feedReadStatus;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_CONTENT_INFO)
    @Nullable
    public String feedContentInfo;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_TIME_MILLIS)
    @Nullable
    public long feedTimeMillis;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_EXPIRE_TIME)
    @Nullable
    public String feedExpireTime;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_LATITUDE)
    public double latitude;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_LONGITUDE)
    public double longitude;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_RANGE)
    public double range;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_BROADCASTADDRESS)
    public String broadcastAddress;

    // Empty constructor for Room database
    public FeedEntity() {

    }

    @NonNull
    public FeedEntity setFeedId(@Nullable String feedId) {
        this.feedId = feedId;
        return this;
    }

    @NonNull
    public FeedEntity setFeedProviderName(@Nullable String feedProviderName) {
        this.feedProviderName = feedProviderName;
        return this;
    }

    @NonNull
    public FeedEntity setFeedProviderLogo(@Nullable String feedProviderLogo) {
        this.feedProviderLogo = feedProviderLogo;
        return this;
    }

    @NonNull
    public FeedEntity setFeedTitle(@Nullable String feedTitle) {
        this.feedTitle = feedTitle;
        return this;
    }

    @NonNull
    public FeedEntity setFeedDetail(@Nullable String feedDetail) {
        this.feedDetail = feedDetail;
        return this;
    }

    @NonNull
    public FeedEntity setFeedTime(@Nullable String feedTime) {
        this.feedTime = feedTime;
        return this;
    }

    @NonNull
    public FeedEntity setFeedReadStatus(boolean feedReadStatus) {
        this.feedReadStatus = feedReadStatus;
        return this;
    }

    public FeedEntity setFeedContentInfo(@Nullable String feedContentInfo) {
        this.feedContentInfo = feedContentInfo;
        return this;
    }

    public FeedEntity setFeedTimeMillis(long feedTimeMillis) {
        this.feedTimeMillis = feedTimeMillis;
        return this;
    }

    public FeedEntity setFeedExpireTime(String feedExpireTime) {
        this.feedExpireTime = feedExpireTime;
        return this;
    }

    @Nullable
    public String getFeedId() {
        return feedId;
    }

    @Nullable
    public String getFeedProviderName() {
        return feedProviderName;
    }

    @Nullable
    public String getFeedProviderLogo() {
        return feedProviderLogo;
    }

    @Nullable
    public String getFeedTitle() {
        return feedTitle;
    }

    @Nullable
    public String getFeedDetail() {
        return feedDetail;
    }

    @Nullable
    public String getFeedTime() {
        return feedTime;
    }

    public boolean isFeedRead() {
        return feedReadStatus;
    }

    @Nullable
    public String getFeedContentInfo() {
        return feedContentInfo;
    }

    public long getFeedTimeMillis() {
        return feedTimeMillis;
    }

    @Nullable
    public String getFeedExpireTime() {
        return feedExpireTime;
    }

    public double getLatitude() {
        return latitude;
    }

    @NonNull
    public FeedEntity setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    @Nullable
    public FeedEntity setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getRange() {
        return range;
    }

    @Nullable
    public FeedEntity setRange(double range) {
        this.range = range;
        return this;
    }

    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    @Nullable
    public FeedEntity setBroadcastAddress(String broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
        return this;
    }

    protected FeedEntity(@NonNull Parcel in) {
        mId = in.readLong();
        feedId = in.readString();
        feedProviderName = in.readString();
        feedProviderLogo = in.readString();
        feedTitle = in.readString();
        feedDetail = in.readString();
        feedTime = in.readString();
        feedReadStatus = in.readByte() != 0;
        feedContentInfo = in.readString();
        feedTimeMillis = in.readLong();
        feedExpireTime = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        range = in.readDouble();
        broadcastAddress = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(feedId);
        dest.writeString(feedProviderName);
        dest.writeString(feedProviderLogo);
        dest.writeString(feedTitle);
        dest.writeString(feedDetail);
        dest.writeString(feedTime);
        dest.writeByte((byte) (feedReadStatus ? 1 : 0));
        dest.writeString(feedContentInfo);
        dest.writeLong(feedTimeMillis);
        dest.writeString(feedExpireTime);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(range);
        dest.writeString(broadcastAddress);
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

    /*@NonNull
    public BulletinFeed toBulletinFeed() {
        return new BulletinFeed()
                .setMessageBody(getFeedDetail())
                .setMessageId(getFeedId())
                .setCreatedAt(getFeedTime());
    }*/

    @NonNull
    public FeedEntity toFeedEntity(@NonNull BulletinFeed bulletinFeed) {
        return new FeedEntity().setFeedDetail(bulletinFeed.getMessageBody())
                .setFeedId(bulletinFeed.getMessageId())
                .setFeedTime(bulletinFeed.getCreatedAt());
    }

    @NonNull
    public FeedEntity prepareFeedEntity(@NonNull BulletinFeed bulletinFeed) {
        return new FeedEntity()
                .setFeedId(bulletinFeed.getMessageId())
                .setFeedProviderName(bulletinFeed.getUploaderInfo())
                .setFeedTitle(bulletinFeed.getMessageTitle())
                .setFeedDetail(bulletinFeed.getMessageBody())
                .setFeedTime(bulletinFeed.getCreatedAt())
                .setFeedExpireTime(bulletinFeed.getExpiredAt())
                .setLatitude(bulletinFeed.getLatitude())
                .setLongitude(bulletinFeed.getLongitude())
                .setRange(bulletinFeed.getRange())
                .setBroadcastAddress(bulletinFeed.getBroadcastAddress());
    }

    @NonNull
    public BroadcastMeta toBroadcastMeta() {
        return new BroadcastMeta()
                .setUploaderName(getFeedProviderName())
                .setMessageTitle(getFeedTitle())
                .setMessageBody(getFeedDetail())
                .setCreationTime(getFeedTime())
                .setBroadcastAddress(getBroadcastAddress());
    }

    @NonNull
    public FeedEntity toFeedEntity(@NonNull BroadcastMeta broadcastMeta) {

        return setFeedProviderName(broadcastMeta.getUploaderName())
                .setFeedTitle(broadcastMeta.getMessageTitle())
                .setFeedDetail(broadcastMeta.getMessageBody())
                .setFeedTime(broadcastMeta.getCreationTime())
                .setBroadcastAddress(broadcastMeta.getBroadcastAddress());
    }
}
