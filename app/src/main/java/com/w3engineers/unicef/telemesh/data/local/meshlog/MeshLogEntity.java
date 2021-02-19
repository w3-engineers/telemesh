package com.w3engineers.unicef.telemesh.data.local.meshlog;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

@Entity(tableName = TableNames.MESH_LOG)
public class MeshLogEntity extends DbBaseEntity implements Parcelable {

    @ColumnInfo(name = ColumnNames.LOG_NAME)
    private String logName;

    public MeshLogEntity(){}

    protected MeshLogEntity(Parcel in) {
        logName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(logName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MeshLogEntity> CREATOR = new Creator<MeshLogEntity>() {
        @Override
        public MeshLogEntity createFromParcel(Parcel in) {
            return new MeshLogEntity(in);
        }

        @Override
        public MeshLogEntity[] newArray(int size) {
            return new MeshLogEntity[size];
        }
    };

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }
}
