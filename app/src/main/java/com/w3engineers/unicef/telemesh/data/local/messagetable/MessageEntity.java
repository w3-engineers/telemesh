package com.w3engineers.unicef.telemesh.data.local.messagetable;

import android.annotation.SuppressLint;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@SuppressLint("ParcelCreator")
@Entity(tableName = TableNames.MESSAGE,
        indices = {@Index(value = {ColumnNames.COLUMN_FRIENDS_ID, ColumnNames.COLUMN_MESSAGE_ID}, unique = true)},
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = ColumnNames.COLUMN_USER_MESH_ID,
                childColumns = ColumnNames.COLUMN_FRIENDS_ID))

public class MessageEntity extends ChatEntity {

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE)
    public String message;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_ID)
    public String contentId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_PATH)
    public String contentPath;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_THUMB_PATH)
    public String contentThumbPath;

    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_PROGRESS)
    public int contentProgress;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_STATUS)
    public int contentStatus;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_INFO)
    public String contentInfo;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_ID)
    public String groupId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE_PLACE)
    public boolean messagePlaceGroup;

    @Nullable
    public String getMessage() {
        return message;
    }

    @NonNull
    public MessageEntity setMessage(@NonNull String message) {
        this.message = message;
        return this;
    }

    @Nullable
    public String getContentId() {
        return contentId;
    }

    public MessageEntity setContentId(@Nullable String contentId) {
        this.contentId = contentId;
        return this;
    }

    @Nullable
    public String getContentPath() {
        return contentPath;
    }

    public MessageEntity setContentPath(@Nullable String contentPath) {
        this.contentPath = contentPath;
        return this;
    }

    @Nullable
    public String getContentThumbPath() {
        return contentThumbPath;
    }

    public MessageEntity setContentThumbPath(@Nullable String contentThumbPath) {
        this.contentThumbPath = contentThumbPath;
        return this;
    }

    public int getContentProgress() {
        return contentProgress;
    }

    public MessageEntity setContentProgress(int contentProgress) {
        this.contentProgress = contentProgress;
        return this;
    }

    @Nullable
    public String getContentInfo() {
        return contentInfo;
    }

    public MessageEntity setContentInfo(@Nullable String contentInfo) {
        this.contentInfo = contentInfo;
        return this;
    }

    public int getContentStatus() {
        return contentStatus;
    }

    public MessageEntity setContentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
        return this;
    }

    @Nullable
    public String getGroupId() {
        return groupId;
    }

    public MessageEntity setGroupId(@Nullable String groupId) {
        this.groupId = groupId;
        return this;
    }

    public boolean getMessagePlace() {
        return messagePlaceGroup;
    }

    public MessageEntity setMessagePlace(boolean messagePlaceGroup) {
        this.messagePlaceGroup = messagePlaceGroup;
        return this;
    }

    /*@NonNull
    @Override
    public TeleMeshChat toProtoChat() {

        TeleMeshMessage teleMeshMessage = TeleMeshMessage.newBuilder()
                .setMessageText(getMessage()).build();

        return TeleMeshChat.newBuilder()
                .setFriendId(getFriendsId())
                .setMessageId(getMessageId())
                .setMessageType(getMessageType())
                .setMessageTime(getTime())
                .setMessageStatus(getStatus())
                .setTeleMeshMessage(teleMeshMessage)
                .build();
    }*/

    @NonNull
    @Override
    public MessageModel toMessageModel() {
        return new MessageModel()
                .setId(getMessageId())
                .setMessage(getMessage())
                .setIsGroup(getMessagePlace())
                .setGroupId(getGroupId())
                .setType(getMessageType());
    }

    @NonNull
    @Override
    public ChatEntity toChatEntity(@NonNull MessageModel messageModel) {

        MessageEntity messageEntity = setMessage(messageModel.getMessage())
                .setMessagePlace(messageModel.isGroup())
                .setGroupId(messageModel.getGroupId());

        messageEntity.setMessageId(messageModel.getId())
                .setMessageType(messageModel.getType());

        return messageEntity;
    }

    public static class MessageAnalyticsEntity {

        @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE_TIME)
        public long time;

        public int syncMessageCountToken;

        public String userId;

        public MessageAnalyticsEntity setTime(long time) {
            this.time = time;
            return this;
        }

        public MessageAnalyticsEntity setSyncMessageCountToken(int syncMessageCountToken) {
            this.syncMessageCountToken = syncMessageCountToken;
            return this;
        }

        public MessageAnalyticsEntity setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public MessageCount toAnalyticMessageCount() {
            return new MessageCount().setTime(time)
                    .setCount(syncMessageCountToken)
                    .setId(userId);
        }

        public MessageAnalyticsEntity toMessageAnalyticsEntity(MessageCount messageCount) {
            return setSyncMessageCountToken(messageCount.getCount())
                    .setTime(messageCount.getTime())
                    .setUserId(messageCount.getId());
        }

        public MessageCountModel toMessageCountModel() {
            return new MessageCountModel()
                    .setUserId(userId)
                    .setMsgCount(syncMessageCountToken)
                    .setMsgTime(time);
        }
    }
}
