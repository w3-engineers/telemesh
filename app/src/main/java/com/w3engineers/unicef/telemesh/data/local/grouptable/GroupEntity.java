package com.w3engineers.unicef.telemesh.data.local.grouptable;

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
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;


@Entity(tableName = TableNames.GROUP,
        indices = {@Index(value = {ColumnNames.COLUMN_GROUP_ID}, unique = true)})
public class GroupEntity extends DbBaseEntity {

    @NonNull
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_ID)
    public String groupId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_NAME)
    public String groupName;

    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_AVATAR)
    public int avatarIndex;

    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_OWN_STATUS)
    public int ownStatus;

    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_CREATION_TIME)
    public long groupCreationTime;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_ADMIN_INFO)
    public String adminInfo;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_MEMBERS_INFO)
    public String membersInfo;

    public int hasUnreadMessage;

    public GroupEntity() {
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.groupName);
        dest.writeString(this.groupId);
        dest.writeInt(this.avatarIndex);
        dest.writeLong(this.groupCreationTime);
        dest.writeString(this.adminInfo);
        dest.writeString(this.membersInfo);
        dest.writeInt(this.ownStatus);
        dest.writeInt(this.hasUnreadMessage);
    }

    protected GroupEntity(@NonNull Parcel in) {
        super(in);
        this.groupName = in.readString();
        this.groupId = in.readString();
        this.avatarIndex = in.readInt();
        this.groupCreationTime = in.readLong();
        this.adminInfo = in.readString();
        this.membersInfo = in.readString();
        this.ownStatus = in.readInt();
        this.hasUnreadMessage = in.readInt();
    }

    public static final Creator<GroupEntity> CREATOR = new Creator<GroupEntity>() {
        @Override
        public GroupEntity createFromParcel(Parcel source) {
            return new GroupEntity(source);
        }

        @Override
        public GroupEntity[] newArray(int size) {
            return new GroupEntity[size];
        }
    };

    @NonNull
    public String getGroupId() {
        return groupId;
    }

    public GroupEntity setGroupId(@Nullable String groupId) {
        this.groupId = groupId;
        return this;
    }

    @Nullable
    public String getGroupName() {
        return groupName;
    }

    public GroupEntity setGroupName(@Nullable String groupName) {
        this.groupName = groupName;
        return this;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public GroupEntity setAvatarIndex(int avatarIndex) {
        this.avatarIndex = avatarIndex;
        return this;
    }

    public int getOwnStatus() {
        return ownStatus;
    }

    public GroupEntity setOwnStatus(int ownStatus) {
        this.ownStatus = ownStatus;
        return this;
    }

    public long getGroupCreationTime() {
        return groupCreationTime;
    }

    public GroupEntity setGroupCreationTime(long groupCreationTime) {
        this.groupCreationTime = groupCreationTime;
        return this;
    }

    @Nullable
    public String getAdminInfo() {
        return adminInfo;
    }

    public GroupEntity setAdminInfo(@Nullable String adminInfo) {
        this.adminInfo = adminInfo;
        return this;
    }

    @Nullable
    public String getMembersInfo() {
        return membersInfo;
    }

    public GroupEntity setMembersInfo(@Nullable String membersInfo) {
        this.membersInfo = membersInfo;
        return this;
    }

    public int getHasUnreadMessage() {
        return hasUnreadMessage;
    }

    public GroupEntity setHasUnreadMessage(int hasUnreadMessage) {
        this.hasUnreadMessage = hasUnreadMessage;
        return this;
    }

    public GroupModel toGroupModel() {
        return new GroupModel().setGroupName(getGroupName())
                .setGroupId(getGroupId())
                .setAvatar(getAvatarIndex())
                .setAdminInfo(getAdminInfo())
                .setMemberInfo(getMembersInfo());
    }

    public GroupEntity toGroupEntity(GroupModel groupModel) {
        return setGroupId(groupModel.getGroupId())
                .setGroupName(groupModel.getGroupName())
                .setAvatarIndex(groupModel.getAvatar())
                .setMembersInfo(groupModel.getMemberInfo())
                .setAdminInfo(groupModel.getAdminInfo());
    }
}