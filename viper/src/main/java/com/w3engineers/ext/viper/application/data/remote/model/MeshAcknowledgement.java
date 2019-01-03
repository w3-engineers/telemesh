package com.w3engineers.ext.viper.application.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-08 at 10:33 AM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-08 at 10:33 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-08 at 10:33 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class MeshAcknowledgement implements Parcelable {

    public int id;
    public MeshPeer mMeshPeer;

    public MeshAcknowledgement(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.mMeshPeer, flags);
    }

    protected MeshAcknowledgement(Parcel in) {
        this.id = in.readInt();
        this.mMeshPeer = in.readParcelable(MeshPeer.class.getClassLoader());
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
