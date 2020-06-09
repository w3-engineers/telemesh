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

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_ID)
    public String groupId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_NAME)
    public String groupName;

    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_AVATAR)
    public int avatarIndex;

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
}