package com.w3engineers.unicef.telemesh.data.remote.model;


import android.databinding.BaseObservable;



/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/2/2018 at 10:34 AM.
 *  * Email : azizul@w3engineers.com
 *  *
 *  * Purpose: Base class for message model. Hole all common property
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/2/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


/**
 * MessageBase hole common property of message
 */

public class BaseMessage extends BaseObservable {

    private String messageId;
    private String friendsId;
    private boolean isIncoming;
    private long messageTime;
    private int messageStatus;
    private int messageType;

    public String getMessageId() {
        return messageId;
    }

    public BaseMessage setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getFriendsId() {
        return friendsId;
    }

    public BaseMessage setFriendsId(String friendsId) {
        this.friendsId = friendsId;
        return this;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public BaseMessage setIncoming(boolean incoming) {
        isIncoming = incoming;
        return this;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public BaseMessage setMessageTime(long messageTime) {
        this.messageTime = messageTime;
        return this;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public BaseMessage setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
        return this;
    }

    public int getMessageType() {
        return messageType;
    }

    public BaseMessage setMessageType(int messageType) {
        this.messageType = messageType;
        return this;
    }

//    public BaseMessage getBaseMessage(MessageEntity messageEntity) {
//
//        BaseMessage baseMessage;
//        switch (messageEntity.getMessageType()) {
//            case Constants.MessageType.TEXT_MESSAGE:
//                baseMessage = new MessageText().setTextMessage(messageEntity.getMessage());
//                break;
//
//            default:
//                baseMessage = new MessageText().setTextMessage(messageEntity.getMessage());
//                break;
//        }
//        return baseMessage.setFriendsId(messageEntity.getFriendsId())
//                .setMessageId(messageEntity.getMessageId())
//                .setIncoming(messageEntity.isIncoming())
//                .setMessageStatus(messageEntity.getStatus())
//                .setMessageTime(messageEntity.getTime())
//                .setMessageType(messageEntity.getMessageType());
//    }


}
