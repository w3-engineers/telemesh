package com.w3engineers.unicef.telemesh.data.local.appsharecount;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;

//import com.w3engineers.unicef.telemesh.TeleMeshAnalyticsOuterClass.AppShareCount;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


@Entity(tableName = TableNames.APP_SHARE_COUNT)
public class AppShareCountEntity extends DbBaseEntity implements Parcelable {

    @ColumnInfo(name = ColumnNames.COLUMN_USER_ID)
    private String userId;

    @ColumnInfo(name = ColumnNames.COLUMN_COUNT)
    private int count;

    @ColumnInfo(name = ColumnNames.COLUMN_DATE)
    private String date;

    @ColumnInfo(name = ColumnNames.COLUMN_IS_SEND)
    private boolean isSend;

    public AppShareCountEntity() {
    }


    protected AppShareCountEntity(Parcel in) {
        userId = in.readString();
        count = in.readInt();
        date = in.readString();
        isSend = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeInt(count);
        dest.writeString(date);
        dest.writeByte((byte) (isSend ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppShareCountEntity> CREATOR = new Creator<AppShareCountEntity>() {
        @Override
        public AppShareCountEntity createFromParcel(Parcel in) {
            return new AppShareCountEntity(in);
        }

        @Override
        public AppShareCountEntity[] newArray(int size) {
            return new AppShareCountEntity[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public ShareCountModel toAnalyticAppShareCount() {
        return new ShareCountModel()
                .setId(userId)
                .setTime(date)
                .setCount(count);
    }

    public AppShareCountEntity toAppShareCountEntity(ShareCountModel shareCountModel) {
        AppShareCountEntity entity = new AppShareCountEntity();
        entity.setCount(shareCountModel.getCount());
        entity.setUserId(shareCountModel.getId());
        entity.setDate(shareCountModel.getTime());
        return entity;
    }
}
