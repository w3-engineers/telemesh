package com.w3engineers.unicef.telemesh.data.local.feed;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    protected FeedEntity(@NonNull Parcel in) {
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
    public void writeToParcel(@NonNull Parcel dest, int flags) {
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
    public BulletinModel toTelemeshBulletin() {
        return new BulletinModel()
                .setId(getFeedId())
                .setMessage(getFeedDetail())
                .setTime(getFeedTime());
    }

    @NonNull
    public FeedEntity toFeedEntity(@NonNull BulletinModel bulletinModel) {
        return new FeedEntity().setFeedDetail(bulletinModel.getMessage())
                .setFeedId(bulletinModel.getId())
                .setFeedTime(bulletinModel.getTime());
    }
}
