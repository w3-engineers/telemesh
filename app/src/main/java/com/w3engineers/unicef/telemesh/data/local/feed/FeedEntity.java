package com.w3engineers.unicef.telemesh.data.local.feed;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.w3engineers.unicef.telemesh.MessageFeedOuterClass;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
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
    public long feedTime;

    @ColumnInfo(name = ColumnNames.COLUMN_FEED_READ_STATUS)
    public boolean feedReadStatus;

    // Empty constructor for Room database
    public FeedEntity() {

    }

    @Ignore
    private FeedEntity(FeedEntityBuilder feedEntityBuilder) {
        if (feedEntityBuilder != null) {
            this.feedProviderName = feedEntityBuilder.mFeedProviderName;
            this.feedProviderLogo = feedEntityBuilder.mFeedProviderLogo;
            this.feedTitle = feedEntityBuilder.mFeedTitle;
            this.feedDetail = feedEntityBuilder.mFeedDetail;
            this.feedTime = feedEntityBuilder.mFeedTime;
            this.feedReadStatus = feedEntityBuilder.mFeedReadStatus;
        } else {
            throw new NullPointerException("FeedEntity model is not properly build");
        }
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

    public long getFeedTime() {
        return feedTime;
    }

    public boolean isFeedRead() {
        return feedReadStatus;
    }

    public static class FeedEntityBuilder {
        private String mFeedProviderName;
        private String mFeedProviderLogo;
        private String mFeedTitle;
        private String mFeedDetail;
        private long mFeedTime;
        private boolean mFeedReadStatus;

        public FeedEntityBuilder setFeedProviderName(String mFeedProviderName) {
            this.mFeedProviderName = mFeedProviderName;
            return this;
        }

        public FeedEntityBuilder setFeedProviderLogo(String mFeedProviderLogo) {
            this.mFeedProviderLogo = mFeedProviderLogo;
            return this;
        }

        public FeedEntityBuilder setFeedTitle(String mFeedTitle) {
            this.mFeedTitle = mFeedTitle;
            return this;
        }

        public FeedEntityBuilder setFeedDetail(String mFeedDetail) {
            this.mFeedDetail = mFeedDetail;
            return this;
        }

        public FeedEntityBuilder setFeedTime(long mFeedTime) {
            this.mFeedTime = mFeedTime;
            return this;
        }

        public FeedEntityBuilder setFeedReadStatus(boolean mFeedReadStatus) {
            this.mFeedReadStatus = mFeedReadStatus;
            return this;
        }

        public FeedEntity build() {
            if (isValidFeedMessage()) {
                return new FeedEntity(this);
            }
            return null;

        }

        // Validate the Feed message with the minimum requirement
        private boolean isValidFeedMessage() {
            return !TextUtils.isEmpty(mFeedTitle) && !TextUtils.isEmpty(mFeedDetail);
        }

        // Convert to Protocol buf message to transfer
        public static byte[] toProtoFeedMessage(FeedEntity feedEntity) {
            if (feedEntity != null) {
                MessageFeedOuterClass.Feed feed = MessageFeedOuterClass.Feed.newBuilder()
                        .setMessageTitle(feedEntity.getFeedTitle())
                        .setMessageProvider(feedEntity.getFeedProviderName())
                        .setMessageDetails(feedEntity.getFeedDetail())
                        .build();
                return MessageFeedOuterClass.MessageFeed.newBuilder()
                        .setMessageType(Constants.DataType.MESSAGE_FEED)
                        .setFeed(feed).setFeedTime(System.currentTimeMillis())
                        .build()
                        .toByteArray();
            }
            return null;

        }
    }

    protected FeedEntity(Parcel in) {
        mId = in.readLong();
        feedId = in.readString();
        feedProviderName = in.readString();
        feedProviderLogo = in.readString();
        feedTitle = in.readString();
        feedDetail = in.readString();
        feedTime = in.readLong();
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
        dest.writeLong(feedTime);
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
}
