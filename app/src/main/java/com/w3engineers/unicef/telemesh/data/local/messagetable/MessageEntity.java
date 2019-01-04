package com.w3engineers.unicef.telemesh.data.local.messagetable;

import android.annotation.SuppressLint;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import com.w3engineers.unicef.telemesh.TeleMeshChatOuterClass.*;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/2/2018 at 12:36 PM.
 *  *
 *  * Purpose: Message table creation for room db
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/2/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */
@SuppressLint("ParcelCreator")
@Entity(tableName = TableNames.MESSAGE,
        indices = {@Index(value = {ColumnNames.COLUMN_MESSAGE_ID,
                ColumnNames.COLUMN_FRIENDS_ID}, unique = true)},
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = ColumnNames.COLUMN_USER_MESH_ID,
                childColumns = ColumnNames.COLUMN_FRIENDS_ID))
public class MessageEntity extends ChatEntity {

    @ColumnInfo(name = ColumnNames.COLUMN_MESSAGE)
    public String message;

    public String getMessage() {
        return message;
    }

    public MessageEntity setMessage(String message) {
        this.message = message;
        return this;
    }

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
    }

    @Override
    public ChatEntity toChatEntity(TeleMeshChat teleMeshChat) {

        MessageEntity messageEntity = setMessage(teleMeshChat.getTeleMeshMessage()
                .getMessageText());

        messageEntity.setFriendsId(teleMeshChat.getFriendId())
                .setMessageId(teleMeshChat.getMessageId())
                .setMessageType(teleMeshChat.getMessageType())
                .setStatus(teleMeshChat.getMessageStatus())
                .setTime(teleMeshChat.getMessageTime());

        return messageEntity;
    }
}
