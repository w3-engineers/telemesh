package com.w3engineers.ext.viper.application.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MeshAcknowledgement implements Parcelable {

    public long id;
    public MeshPeer mMeshPeer;

    public boolean isSuccess;

    public MeshAcknowledgement(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.mMeshPeer, flags);
        dest.writeByte((byte) (isSuccess ? 1 : 0));
    }

    protected MeshAcknowledgement(Parcel in) {
        this.id = in.readLong();
        this.mMeshPeer = in.readParcelable(MeshPeer.class.getClassLoader());
        this.isSuccess = in.readByte() != 0;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public MeshAcknowledgement setSuccess(boolean success) {
        isSuccess = success;
        return this;
    }

    public static final Creator<MeshAcknowledgement> CREATOR = new Creator<MeshAcknowledgement>() {
        @Override
        public MeshAcknowledgement createFromParcel(Parcel source) {
            return new MeshAcknowledgement(source);
        }

        @Override
        public MeshAcknowledgement[] newArray(int size) {
            return new MeshAcknowledgement[size];
        }
    };
}
