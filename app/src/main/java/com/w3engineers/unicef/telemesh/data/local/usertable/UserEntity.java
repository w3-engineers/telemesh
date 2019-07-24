package com.w3engineers.unicef.telemesh.data.local.usertable;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;


@Entity(tableName = TableNames.USERS,
        indices = {@Index(value = {ColumnNames.COLUMN_USER_MESH_ID}, unique = true)})
public class UserEntity extends DbBaseEntity {

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_USER_MESH_ID)
    public String meshId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_USER_CUSTOM_ID)
    public String customId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_USER_NAME)
    public String userName;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_AVATAR)
    public int avatarIndex;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_LAST_ONLINE_TIME)
    public long lastOnlineTime;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_IS_ONLINE)
    public boolean isOnline;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_REGISTRATION_TIME)
    public long registrationTime;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_IS_SYNCED)
    public boolean isUserSynced;

    public int hasUnreadMessage;

    //@Ignore
    //private String userLastName;
    public UserEntity() {
    }

    @Nullable
    public String getMeshId() {
        return meshId;
    }

    @NonNull
    public UserEntity setMeshId(@NonNull String meshId) {
        this.meshId = meshId;
        return this;
    }

    @Nullable
    public String getCustomId() {
        return customId;
    }

    @NonNull
    public UserEntity setCustomId(@NonNull String customId) {
        this.customId = customId;
        return this;
    }

    @Nullable
    public String getUserName() {
        return userName;
    }

    @NonNull
    public UserEntity setUserName(@NonNull String userName) {
        this.userName = userName;
        return this;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    @NonNull
    public UserEntity setAvatarIndex(int avatarIndex) {
        this.avatarIndex = avatarIndex;
        return this;
    }

    public long getLastOnlineTime() {
        return lastOnlineTime;
    }

    @NonNull
    public UserEntity setLastOnlineTime(long lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
        return this;
    }

    public boolean isOnline() {
        return isOnline;
    }

    @NonNull
    public UserEntity setOnline(boolean online) {
        isOnline = online;
        return this;
    }

    public long getRegistrationTime() {
        return registrationTime;
    }

    @NonNull
    public UserEntity setRegistrationTime(long registrationTime) {
        this.registrationTime = registrationTime;
        return this;
    }

   /* public boolean isUserSynced() {
        return isUserSynced;
    }

    @NonNull
    public UserEntity setUserSynced(boolean userSynced) {
        isUserSynced = userSynced;
        return this;
    }*/

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.userName);
        dest.writeString(this.meshId);
        dest.writeString(this.customId);
        dest.writeInt(this.avatarIndex);
        dest.writeLong(this.lastOnlineTime);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeLong(this.registrationTime);
        dest.writeByte((byte) (isUserSynced ? 1 : 0));
        dest.writeInt(this.hasUnreadMessage);
    }

    protected UserEntity(@NonNull Parcel in) {
        super(in);
        this.userName = in.readString();
        this.meshId = in.readString();
        this.customId = in.readString();
        this.avatarIndex = in.readInt();
        this.lastOnlineTime = in.readLong();
        this.isOnline = in.readByte() != 0;
        this.registrationTime = in.readLong();
        this.isUserSynced = in.readByte() != 0;
        this.hasUnreadMessage = in.readInt();
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel source) {
            return new UserEntity(source);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    @NonNull
    public UserModel getProtoUser() {
        return new UserModel()
                .setName(getUserName())
                .setImage(getAvatarIndex())
                .setTime(getRegistrationTime());
    }

    // if lots of similar task holds in entity then ti should be used in util class
    @Nullable
    public String getFullName() {
        return userName;
    }

    @NonNull
    public UserEntity toUserEntity(@NonNull UserModel userModel) {
        return setUserName(userModel.getName())
                .setAvatarIndex(userModel.getImage())
                .setRegistrationTime(userModel.getTime())
                .setMeshId(userModel.getUserId());
    }

    public static class NewMeshUserCount {

        @Nullable
        @ColumnInfo(name = ColumnNames.COLUMN_USER_MESH_ID)
        public String meshId;

        @ColumnInfo(name = ColumnNames.COLUMN_USER_REGISTRATION_TIME)
        public long registrationTime;

       /* @Nullable
        public String getMeshId() {
            return meshId;
        }*/

        public NewMeshUserCount setMeshId(@Nullable String meshId) {
            this.meshId = meshId;
            return this;
        }

        public NewNodeModel toNewNodeModel() {
            return new NewNodeModel()
                    .setUserAddingTime(registrationTime)
                    .setUserId(meshId);
        }
    }
}