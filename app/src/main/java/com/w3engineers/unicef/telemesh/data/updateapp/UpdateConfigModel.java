package com.w3engineers.unicef.telemesh.data.updateapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateConfigModel implements Parcelable {
    @SerializedName("version_name")
    @Expose
    private String versionName;

    @SerializedName("version_code")
    @Expose
    private int versionCode;

    @SerializedName("update_type")
    @Expose
    private int updateType;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    protected UpdateConfigModel(Parcel in) {
        versionName = in.readString();
        versionCode = in.readInt();
        updateType = in.readInt();
    }

    public static final Creator<UpdateConfigModel> CREATOR = new Creator<UpdateConfigModel>() {
        @Override
        public UpdateConfigModel createFromParcel(Parcel in) {
            return new UpdateConfigModel(in);
        }

        @Override
        public UpdateConfigModel[] newArray(int size) {
            return new UpdateConfigModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(versionName);
        parcel.writeInt(versionCode);
        parcel.writeInt(updateType);
    }
}
