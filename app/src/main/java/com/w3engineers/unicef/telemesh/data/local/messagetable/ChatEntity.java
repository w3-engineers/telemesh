package com.w3engineers.unicef.telemesh.data.local.messagetable;

import android.annotation.SuppressLint;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@SuppressLint("ParcelCreator")
public class ChatEntity extends DbBaseEntity {

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE_ID)
    public String messageId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_FRIENDS_ID)
    public String friendsId;

    @ColumnInfo(name = ColumnNames.COLUMN_IS_INCOMING)
    public boolean isIncoming;

    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE_TYPE)
    public int messageType;

    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE_TIME)
    public long time;

    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE_STATUS)
    public int status;

    public static final DiffUtil.ItemCallback<ChatEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChatEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ChatEntity oldItem, @NonNull ChatEntity newItem) {
                    // ChatEntity properties may have changed if reloaded from the DB, but time is fixed
                    return oldItem.time == newItem.time ;
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull ChatEntity oldItem, @NonNull ChatEntity newItem) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldItem.equals(newItem);

                }
            };

    /*@Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        ChatEntity chatEntity = (ChatEntity) obj;

        return chatEntity.time == this.time && chatEntity.messageId.equals(this.messageId);
    }*/

    @NonNull
    public String getMessageId() {
        return messageId;
    }

    @NonNull
    public ChatEntity setMessageId(@NonNull String messageId) {
        this.messageId = messageId;
        return this;
    }

    @NonNull
    public String getFriendsId() {
        return friendsId;
    }

    @NonNull
    public ChatEntity setFriendsId(@NonNull String friendsId) {
        this.friendsId = friendsId;
        return this;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    @NonNull
    public ChatEntity setIncoming(boolean incoming) {
        isIncoming = incoming;
        return this;
    }

    public int getMessageType() {
        return messageType;
    }

    @NonNull
    public ChatEntity setMessageType(int messageType) {
        this.messageType = messageType;
        return this;
    }

    public long getTime() {
        return time;
    }

    @NonNull
    public ChatEntity setTime(long time) {
        this.time = time;
        return this;
    }

    public int getStatus() {
        return status;
    }

    @NonNull
    public ChatEntity setStatus(int status) {
        this.status = status;
        return this;
    }

    /*@NonNull
    public TeleMeshChat toProtoChat() {
        throw new IllegalStateException();
    }*/

    @NonNull
    public MessageModel toMessageModel() {
        throw new IllegalStateException();
    }

    /*@NonNull
    public ChatEntity toChatEntity(@NonNull TeleMeshChat teleMeshChat) {
        throw new IllegalStateException();
    }*/

    @NonNull
    public ChatEntity toChatEntity(@NonNull MessageModel messageModel) {
        throw new IllegalStateException();
    }


}
