package com.w3engineers.unicef.telemesh.data.local.usertable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

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
    public int isOnline;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_REGISTRATION_TIME)
    public long registrationTime;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_IS_SYNCED)
    public boolean isUserSynced;

    public int hasUnreadMessage;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_IS_FAVOURITE)
    public int isFavourite;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_CONFIG_VERSION)
    public int configVersion;

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

    public int getOnlineStatus() {
        return isOnline;
    }

    @NonNull
    public UserEntity setOnlineStatus(int online) {
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

    public int getIsFavourite() {
        return isFavourite;
    }

    @NonNull
    public UserEntity setIsFavourite(int isFavourite) {
        this.isFavourite = isFavourite;
        return this;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    @NonNull
    public UserEntity setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
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
        dest.writeInt(isOnline);
        dest.writeInt(isFavourite);
        dest.writeLong(this.registrationTime);
        dest.writeByte((byte) (isUserSynced ? 1 : 0));
        dest.writeInt(this.hasUnreadMessage);
        dest.writeInt(this.configVersion);
    }

    protected UserEntity(@NonNull Parcel in) {
        super(in);
        this.userName = in.readString();
        this.meshId = in.readString();
        this.customId = in.readString();
        this.avatarIndex = in.readInt();
        this.lastOnlineTime = in.readLong();
        this.isOnline = in.readInt();
        this.isFavourite = in.readInt();
        this.registrationTime = in.readLong();
        this.isUserSynced = in.readByte() != 0;
        this.hasUnreadMessage = in.readInt();
        this.configVersion = in.readInt();
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
                .setMeshId(userModel.getUserId())
                .setConfigVersion(userModel.getConfigVersion());
    }

    @NonNull
    public UserEntity updateUserEntity(@NonNull UserModel userModel) {
        return setUserName(userModel.getName())
                .setAvatarIndex(userModel.getImage())
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