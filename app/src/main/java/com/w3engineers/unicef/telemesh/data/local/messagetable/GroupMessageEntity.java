package com.w3engineers.unicef.telemesh.data.local.messagetable;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.ArrayList;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@SuppressLint("ParcelCreator")
@Entity(tableName = TableNames.GROUP_MESSAGE,
        indices = {@Index(value = {ColumnNames.COLUMN_MESSAGE_ID}, unique = true)})
public class GroupMessageEntity extends ChatEntity {
    @NonNull
    @ColumnInfo(name = ColumnNames.COLUMN_GROUP_ID)
    public String groupId;

    @ColumnInfo(name = ColumnNames.COLUMN_ORIGINAL_SENDER)
    public String originalSender; // Actual sender of the message

    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE)
    public String message;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_PATH)
    public String contentPath;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_THUMB_PATH)
    public String contentThumb;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_INFO)
    public String contentInfo;


    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_RECEIVED_USERS)
    public ArrayList<String> receivedUsers;


    public GroupMessageEntity setMessageId(@NonNull String messageId) {
        this.messageId = messageId;
        return this;
    }

    public GroupMessageEntity setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
        return this;
    }

    public GroupMessageEntity setMessage(String message) {
        this.message = message;
        return this;
    }

    public GroupMessageEntity setMessageType(int messageType) {
        this.messageType = messageType;
        return this;
    }

    public GroupMessageEntity setIncoming(boolean incoming) {
        isIncoming = incoming;
        return this;
    }

    public GroupMessageEntity setContentThumb(String contentThumb) {
        this.contentThumb = contentThumb;
        return this;
    }


    public GroupMessageEntity setReceivedUsers(ArrayList<String> receivedUsers) {
        this.receivedUsers = receivedUsers;
        return this;
    }

    public GroupMessageEntity setGroupId(@NonNull String groupId) {
        this.groupId = groupId;
        return this;
    }

    public GroupMessageEntity setContentPath(@Nullable String contentPath) {
        this.contentPath = contentPath;
        return this;
    }

    public GroupMessageEntity setContentInfo(@Nullable String contentInfo) {
        this.contentInfo = contentInfo;
        return this;
    }

    @NonNull
    public String getGroupId() {
        return groupId;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public String getMessage() {
        return message;
    }

    @Nullable
    public String getContentPath() {
        return contentPath;
    }

    @Nullable
    public String getContentThumb() {
        return contentThumb;
    }

    @Nullable
    public String getContentInfo() {
        return contentInfo;
    }

    @Nullable
    public ArrayList<String> getReceivedUsers() {
        return receivedUsers;
    }

    @NonNull
    @Override
    public MessageModel toMessageModel() {
        return new MessageModel()
                .setId(getMessageId())
                .setMessage(getMessage())
                .setIsGroup(true)
                .setGroupId(getGroupId())
                .setType(getMessageType())
                .setOriginalSender(originalSender);
    }

    @NonNull
    @Override
    public GroupMessageEntity toChatEntity(@NonNull MessageModel messageModel) {

        GroupMessageEntity messageEntity = setMessage(messageModel.getMessage())
                .setGroupId(messageModel.getGroupId())
                .setMessageId(messageModel.getId())
                .setOriginalSender(messageModel.getOriginalSender())
                .setMessageType(messageModel.getType());

        return messageEntity;
    }
}
