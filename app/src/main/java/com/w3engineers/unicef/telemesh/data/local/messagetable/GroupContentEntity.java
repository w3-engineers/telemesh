package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

/**
 * Created by Azizul Islam on 8/10/21.
 */

@Entity(tableName = TableNames.GROUP_CONTENT,
        foreignKeys = @ForeignKey(entity = GroupMessageEntity.class,
                parentColumns = ColumnNames.COLUMN_MESSAGE_ID,
                childColumns = ColumnNames.COLUMN_CONTENT_MESSAGE_ID))
public class GroupContentEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_MESSAGE_ID)
    public String contentMessageId;

    @ColumnInfo(name = ColumnNames.COLUMN_CONTENT_ID)
    public String contentId;

    @ColumnInfo(name = ColumnNames.COLUMN_SENDER_ID)
    public String senderId;

    @ColumnInfo(name = ColumnNames.COLUMN_RECEIVER_ID)
    public String receiverId;

    public GroupContentEntity setId(int id) {
        this.id = id;
        return this;
    }

    public GroupContentEntity setContentMessageId(String contentMessageId) {
        this.contentMessageId = contentMessageId;
        return this;
    }

    public GroupContentEntity setContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }

    public GroupContentEntity setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public GroupContentEntity setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }
}
