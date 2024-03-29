package com.w3engineers.unicef.telemesh.data.local.grouptable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.ArrayList;


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

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_IS_SYNCED)
    public boolean isSynced;

    public int hasUnreadMessage;

    @Ignore
    public String groupInfoId;

    public String lastMessage;

    public String lastPersonName;

    public String lastPersonId;

    public int lastMessageType;

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
        dest.writeString(this.groupInfoId);
        dest.writeString(this.lastMessage);
        dest.writeString(this.lastPersonName);
        dest.writeString(this.lastPersonId);
        dest.writeInt(this.lastMessageType);
        dest.writeByte((byte)(this.isSynced? 1: 0));
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
        this.groupInfoId = in.readString();
        this.lastMessage = in.readString();
        this.lastPersonName = in.readString();
        this.lastPersonId = in.readString();
        this.lastMessageType = in.readInt();
        this.isSynced = in.readByte() != 0;
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

    public String getGroupInfoId() {
        return groupInfoId;
    }

    public GroupEntity setGroupInfoId(String groupInfoId) {
        this.groupInfoId = groupInfoId;
        return this;
    }

    public GroupEntity setSynced(boolean synced) {
        this.isSynced = synced;
        return this;
    }

    public boolean isSynced() {
        return this.isSynced;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public GroupEntity setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }

    public GroupModel toGroupModel() {
        return new GroupModel().setGroupName(getGroupName())
                .setGroupId(getGroupId())
                .setAvatar(getAvatarIndex())
                .setCreatedTime(getGroupCreationTime())
                .setAdminInfo(getAdminInfo())
                .setMemberInfo(getMembersInfo())
                .setSynced(isSynced());

    }

    public GroupEntity toGroupEntity(GroupModel groupModel) {
        return setGroupId(groupModel.getGroupId())
                .setGroupName(groupModel.getGroupName())
                .setAvatarIndex(groupModel.getAvatar())
                .setMembersInfo(groupModel.getMemberInfo())
                .setAdminInfo(groupModel.getAdminInfo())
                .setGroupCreationTime(groupModel.getCreatedTime())
                .setSynced(groupModel.isSynced());
    }

    public GroupCountModel toGroupCountModel() {
        return new GroupCountModel().setGroupId(getGroupId())
                .setGroupOwnerId(getAdminInfo())
                .setMemberCount(getMemberCount())
                .setCreatedTime(TimeUtil.getDateStringFromMillisecond(getGroupCreationTime()));
    }

    public ArrayList<GroupMembersInfo> getMembersArray(){
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();
        return gsonBuilder.getGroupMemberInfoObj(getMembersInfo());
    }

    public int getMemberCount(){
        return getMembersArray().size();
    }
}