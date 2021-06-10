package com.w3engineers.unicef.telemesh.data.helper;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.w3engineers.mesh.util.MeshLog;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.models.ContentMetaInfo;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.NotifyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class ContentDataHelper extends RmDataHelper {

    private static ContentDataHelper contentDataHelper = new ContentDataHelper();

    private HashMap<String, ContentReceiveModel> contentReceiveModelHashMap = new HashMap<>();
    private HashMap<String, ContentSendModel> contentSendModelHashMap = new HashMap<>();
    private List<ContentSequenceModel> contentSequenceModels = new ArrayList<>();
    private final int CONTENT_PROGRESS = 1, CONTENT_RECEIVED = 2;

    private ContentDataHelper() {

    }

    @NonNull
    public static ContentDataHelper getInstance() {
        return contentDataHelper;
    }

    // Observer for send content message
    public void prepareContentObserver(MessageEntity messageEntity, boolean isSend) {
        if (isSend) {
            dataSource.updateMessageStatus(messageEntity.getMessageId(),
                    Constants.MessageStatus.STATUS_SENDING_START);
            contentMessageSend(messageEntity);
        } else {
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_RESEND_START) {
                if (messageEntity.isIncoming()) {
                    HandlerUtil.postBackground(() -> resendContentRequestAction(messageEntity));
                } else {
                    HandlerUtil.postBackground(() -> resendContentAction(messageEntity));
                }
            }
        }
    }

    private void resendContentAction(MessageEntity messageEntity) {
        try {
            dataSource.updateMessageStatus(messageEntity.getMessageId(), Constants.MessageStatus.STATUS_SENDING_START);
            contentMessageResendSend(messageEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resendContentRequestAction(MessageEntity messageEntity) {
        try {
            dataSource.updateMessageStatus(messageEntity.getMessageId(), Constants.MessageStatus.STATUS_READ);
            contentMessageResendRequest(messageEntity, messageEntity.getFriendsId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Content send action +++++++++++++++++++++++++++++++++++++++++++++++++++

    private void contentMessageSend(MessageEntity messageEntity) {
        ContentModel contentModel = new ContentModel()
                .setMessageId(messageEntity.getMessageId())
                .setMessageType(messageEntity.getMessageType())
                .setContentPath(messageEntity.getContentPath())
                .setThumbPath(messageEntity.getContentThumbPath())
                .setUserId(messageEntity.getFriendsId())
                .setContentInfo(messageEntity.getContentInfo());

        contentMessageSend(contentModel);
    }

    // ---------------------------------------------------------------------

    private void contentMessageSend(ContentModel contentModel) {
        prepareRightMeshDataSource();

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> rightMeshDataSource.ContentDataSend(contentModel));
    }

    // Resend action ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private void contentMessageResendSend(MessageEntity messageEntity) {

        String contentId = messageEntity.getContentId();

        if (TextUtils.isEmpty(contentId)) {
            contentMessageSend(messageEntity);
        } else {
            ContentModel contentModel = new ContentModel()
                    .setContentId(contentId)
                    .setMessageId(messageEntity.getMessageId())
                    .setMessageType(messageEntity.getMessageType())
                    .setContentPath(messageEntity.getContentPath())
                    .setThumbPath(messageEntity.getContentThumbPath())
                    .setContentInfo(messageEntity.getContentInfo())
                    .setUserId(messageEntity.getFriendsId())
                    .setResendMessage(true);
            contentMessageSend(contentModel);
        }
    }

    private void contentMessageResendRequest(MessageEntity messageEntity, String userId) {
        String contentId = messageEntity.getContentId();

        ContentModel contentModel = new ContentModel()
                .setContentId(contentId)
                .setMessageId(messageEntity.getMessageId())
                .setMessageType(messageEntity.getMessageType())
                .setContentPath(messageEntity.getContentPath())
                .setThumbPath(messageEntity.getContentThumbPath())
                .setUserId(userId)
                .setContentInfo(messageEntity.getContentInfo())
                .setRequestFromReceiver(true)
                .setResendMessage(true);
        contentMessageSend(contentModel);
    }

    // ------------------------------------------------------------------------

    // Cross check between sender and receiver +++++++++++++++++++++++++++++++++

    protected void contentMessageSuccessResponse(byte[] rawData) {

        if (rawData == null)
            return;
        String messageId = new String(rawData);

        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromId(messageId);

        if (messageEntity != null) {
            ContentModel contentModel = new ContentModel()
                    .setMessageId(messageId)
                    .setAckStatus(Constants.MessageStatus.STATUS_RECEIVED);

            setContentMessage(contentModel, false);

            if (!TextUtils.isEmpty(messageEntity.getContentId())) {
                prepareRightMeshDataSource();
                rightMeshDataSource.removeSendContents(messageEntity.getContentId());
            }
        } else {

            messageEntity = MessageSourceData.getInstance().getMessageEntityFromContentId(messageId);
            if (messageEntity != null) {
                messageEntity.setStatus(Constants.MessageStatus.STATUS_RECEIVED);
                MessageSourceData.getInstance().insertOrUpdateData(messageEntity);
            }

            if (!TextUtils.isEmpty(messageId)) {
                prepareRightMeshDataSource();
                rightMeshDataSource.removeSendContents(messageId);
            }
        }
    }

    private void successForMainContent(String userId, String messageId) {
        if (!TextUtils.isEmpty(messageId)) {
            dataSend(messageId.getBytes(), Constants.DataType.SUCCESS_CONTENT_MESSAGE, userId, false);
        }
    }

    // -------------------------------------------------------------------------

    private void setContentMessage(ContentModel contentModel, boolean isNewMessage) {
        String messageId = contentModel.getMessageId();
        int ackStatus = contentModel.getAckStatus();

        try {
            if (isNewMessage) {

                MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromId(messageId);

                if (messageEntity == null) {

                    int contentStatus = contentModel.getReceivingStatus();
                    String userId = contentModel.getUserId();

                    MessageEntity newMessageEntity = new MessageEntity()
                            .setMessage("Image")
                            .setContentId(contentModel.getContentId())
                            .setContentPath(contentModel.getContentPath())
                            .setContentThumbPath(contentModel.getThumbPath())
                            .setContentInfo(contentModel.getContentInfo());

                    if (contentModel.getReceiveSuccessStatus()) {
                        if (contentStatus != -1) {
                            newMessageEntity.setContentStatus(contentStatus);
                        }
                    }

                    ChatEntity chatEntity = newMessageEntity
                            .setMessageId(messageId)
                            .setFriendsId(contentModel.getUserId())
                            .setIncoming(true)
                            .setMessageType(contentModel.getMessageType())
                            .setTime(System.currentTimeMillis())
                            .setStatus(Constants.MessageStatus.STATUS_READ);

                    if (TextUtils.isEmpty(dataSource.getCurrentUser()) || TextUtils.isEmpty(userId)
                            || !userId.equals(dataSource.getCurrentUser())) {
                        chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
                        NotifyUtil.showNotification(chatEntity);
                    }

                    if (!contentModel.getReceiveSuccessStatus()) {
                        // FAILED MAINTAINED

                        if (chatEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED
                                || chatEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD) {
                            chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD_FAILED);
                        } else {
                            chatEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
                        }
                    }

                    MessageSourceData.getInstance().insertOrUpdateData(chatEntity);

                    Iterator<ContentSequenceModel> sequenceModelIterator = contentSequenceModels.iterator();
                    while (sequenceModelIterator.hasNext()) {
                        ContentSequenceModel contentSequenceModel = sequenceModelIterator.next();

                        if (contentSequenceModel.getContentId().equals(contentModel.getContentId())) {
                            if (contentSequenceModel.getReceiveStatus() == CONTENT_PROGRESS) {
                                contentReceiveInProgress(contentSequenceModel.getContentId(), contentSequenceModel.getProgress());
                                sequenceModelIterator.remove();
                            } else if (contentSequenceModel.getReceiveStatus() == CONTENT_RECEIVED) {
                                contentReceiveDone(contentSequenceModel.getContentId(), contentSequenceModel.isContentStatus());
                                sequenceModelIterator.remove();
                            }
                        }
                    }

                } else {
                    String thumbPath = contentModel.getThumbPath(), contentPath = contentModel.getContentPath();
                    int contentStatus = contentModel.getReceivingStatus();

                    if (!TextUtils.isEmpty(thumbPath)) {
                        messageEntity.setContentThumbPath(thumbPath);
                    }

                    if (!TextUtils.isEmpty(contentPath)) {
                        messageEntity.setContentPath(contentPath);
                    }

                    if (!TextUtils.isEmpty(contentModel.getContentId())) {
                        messageEntity.setContentId(contentModel.getContentId());
                    }

                    if (messageEntity.getContentStatus() != Constants.ContentStatus.CONTENT_STATUS_RECEIVED
                            && !contentModel.getReceiveSuccessStatus()) {
                        // FAILED MAINTAINED

                        inComingGeneralToFailed(messageEntity);
                    } else {
                        if (contentStatus != -1) {
                            messageEntity.setContentStatus(contentStatus);

                            inComingFailedToGeneral(messageEntity);

                        }
                    }

                    MessageSourceData.getInstance().insertOrUpdateData(messageEntity);
                }

                if (contentModel.getReceiveSuccessStatus() && contentModel.getReceivingStatus() ==
                        Constants.ContentStatus.CONTENT_STATUS_RECEIVED) {
                    successForMainContent(contentModel.getUserId(), messageId);
                }

            } else {
                MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromId(messageId);
                if (messageEntity != null) {
                    if (!messageEntity.isIncoming()) {
                        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_RECEIVED) {
                            ackStatus = messageEntity.getStatus();
                        }
                    }
                }
                dataSource.updateMessageStatus(messageId, ackStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inComingFailedToGeneral(MessageEntity messageEntity) {
        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD) {
            messageEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
        } else {
            messageEntity.setStatus(Constants.MessageStatus.STATUS_READ);
        }
    }

    private void inComingGeneralToFailed(MessageEntity messageEntity) {
        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD) {
            messageEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD_FAILED);
        } else {
            messageEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
        }
    }


    // Half done info sync from service ++++++++++++++++++++++++++++++++

    public void receiveIncomingContentInfo(ContentModel contentModel) {

        if (!TextUtils.isEmpty(contentModel.getMessageId())) {

            MessageEntity messageEntity = MessageSourceData.getInstance()
                    .getMessageEntityFromId(contentModel.getMessageId());

            if (messageEntity == null) {

                int contentStatus = -1;
                String userId = contentModel.getUserId();

                if (contentModel.getAckStatus() == Constants.ServiceContentState.SUCCESS) {
                    contentStatus = Constants.ContentStatus.CONTENT_STATUS_RECEIVED;
                } else {
                    contentStatus = Constants.ContentStatus.CONTENT_STATUS_RECEIVING;
                }

                String thumbPath = contentModel.getThumbPath();
                String contentPath = contentModel.getContentPath();

                if (contentModel.getAckStatus() == Constants.ServiceContentState.SUCCESS) {
                    if (!TextUtils.isEmpty(contentPath)) {
                        contentPath = ContentUtil.getInstance().getCopiedFilePath(contentPath, false);
                    }
                }

                MessageEntity newMessageEntity = new MessageEntity()
                        .setMessage("Image")
                        .setContentPath(contentPath)
                        .setContentThumbPath(thumbPath)
                        .setContentInfo(contentModel.getContentInfo());

                Timber.tag("FileMessage").v(" step 1: %s", contentStatus);

                if (contentStatus != -1) {
                    newMessageEntity.setContentStatus(contentStatus);
                }

                if (contentStatus == Constants.ContentStatus.CONTENT_STATUS_RECEIVING) {
                    newMessageEntity.setContentProgress(contentModel.getProgress());
                }

                newMessageEntity.setContentId(contentModel.getContentId());

                ChatEntity chatEntity = newMessageEntity
                        .setMessageId(contentModel.getMessageId())
                        .setFriendsId(contentModel.getUserId())
                        .setIncoming(true)
                        .setMessageType(contentModel.getMessageType())
                        .setTime(System.currentTimeMillis())
                        .setStatus(Constants.MessageStatus.STATUS_READ);

                if (TextUtils.isEmpty(dataSource.getCurrentUser()) || TextUtils.isEmpty(userId)
                        || !userId.equals(dataSource.getCurrentUser())) {
                    chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
                    NotifyUtil.showNotification(chatEntity);
                }

                if (contentModel.getAckStatus() == Constants.ServiceContentState.FAILED) {
                    // FAILED MAINTAINED
                    if (chatEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED
                            || chatEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD) {
                        chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD_FAILED);
                    } else {
                        chatEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
                    }
                }

                if (contentModel.getAckStatus() == Constants.ServiceContentState.PROGRESS) {
                    prepareProgressContent((MessageEntity) chatEntity);
                }

                MessageSourceData.getInstance().insertOrUpdateData(chatEntity);
            } else {

                String thumbPath = contentModel.getThumbPath();
                String contentPath = contentModel.getContentPath();
                String userId = contentModel.getUserId();
                int contentStatus = -1;

                if (!TextUtils.isEmpty(thumbPath)) {
                    messageEntity.setContentThumbPath(thumbPath);
                }

                if (!TextUtils.isEmpty(contentPath)) {
                    messageEntity.setContentPath(contentPath);
                }

                if (contentModel.getAckStatus() == Constants.ServiceContentState.SUCCESS) {

                    contentPath = messageEntity.getContentPath();
                    if (!TextUtils.isEmpty(contentPath)) {
                        contentPath = ContentUtil.getInstance().getCopiedFilePath(contentPath, false);
                        messageEntity.setContentPath(contentPath);
                    }
                }

                if (!TextUtils.isEmpty(userId)) {
                    messageEntity.setFriendsId(userId);
                }

                if (contentModel.getAckStatus() == Constants.ServiceContentState.PROGRESS) {
                    messageEntity.setContentProgress(contentModel.getProgress());
                }

                messageEntity.setContentId(contentModel.getContentId());

                if (contentModel.getAckStatus() == Constants.ServiceContentState.SUCCESS) {
                    contentStatus = Constants.ContentStatus.CONTENT_STATUS_RECEIVED;
                } else {
                    contentStatus = Constants.ContentStatus.CONTENT_STATUS_RECEIVING;
                }

                Timber.tag("FileMessage").v(" step 2: %s", contentStatus);

                if (messageEntity.getContentStatus() != Constants.ContentStatus.CONTENT_STATUS_RECEIVED) {
                    messageEntity.setContentStatus(contentStatus);
                }

                // FAILED MAINTAINED
                if (contentModel.getAckStatus() == Constants.ServiceContentState.FAILED) {
                    inComingGeneralToFailed(messageEntity);
                } else {

                    inComingFailedToGeneral(messageEntity);
                }

                if (contentModel.getAckStatus() == Constants.ServiceContentState.PROGRESS) {
                    prepareProgressContent(messageEntity);
                }

                MessageSourceData.getInstance().insertOrUpdateData(messageEntity);
            }
        } else {

            String contentId = contentModel.getContentId();
            int contentProgress = contentModel.getProgress();
            int state = contentModel.getAckStatus();

            MessageEntity messageEntity = MessageSourceData.getInstance()
                    .getMessageEntityFromContentId(contentId);

            if (messageEntity != null) {
                messageEntity.setContentProgress(contentProgress);

                int contentStatus = -1;
                if (state == Constants.ServiceContentState.SUCCESS) {
                    contentStatus = Constants.ContentStatus.CONTENT_STATUS_RECEIVED;
                } else {
                    contentStatus = Constants.ContentStatus.CONTENT_STATUS_RECEIVING;
                }

                Timber.tag("FileMessage").v(" step 3: %s", contentStatus);
                messageEntity.setContentStatus(contentStatus);

                // FAILED MAINTAINED
                if (contentModel.getAckStatus() == Constants.ServiceContentState.FAILED) {
                    inComingGeneralToFailed(messageEntity);
                } else {
                    inComingFailedToGeneral(messageEntity);
                }

                if (contentModel.getAckStatus() == Constants.ServiceContentState.SUCCESS) {

                    String contentPath = messageEntity.getContentPath();
                    if (!TextUtils.isEmpty(contentPath)) {
                        contentPath = ContentUtil.getInstance().getCopiedFilePath(contentPath, false);
                        messageEntity.setContentPath(contentPath);
                    }
                }

                if (contentModel.getAckStatus() == Constants.ServiceContentState.PROGRESS) {
                    prepareProgressContent(messageEntity);
                }

                MessageSourceData.getInstance().insertOrUpdateData(messageEntity);
            }

        }
    }

    public void sendOutgoingContentInfo(ContentModel contentModel) {
        String contentId = contentModel.getContentId();
        int contentProgress = contentModel.getProgress();
        int state = contentModel.getAckStatus();

        MessageEntity messageEntity = MessageSourceData.getInstance()
                .getMessageEntityFromContentId(contentId);

        if (messageEntity != null) {
            messageEntity.setContentProgress(contentProgress);

            if (state == Constants.ServiceContentState.FAILED) {
                messageEntity.setStatus(Constants.MessageStatus.STATUS_FAILED);
            }

            if (state == Constants.ServiceContentState.SUCCESS) {
                messageEntity.setStatus(Constants.MessageStatus.STATUS_RECEIVED);
            }

            if (state == Constants.ServiceContentState.PROGRESS) {
                messageEntity.setStatus(Constants.MessageStatus.STATUS_SENDING_START);
                prepareSendProgressContent(messageEntity);
            }

            MessageSourceData.getInstance().insertOrUpdateData(messageEntity);
        }
    }

    // -----------------------------------------------------------------

    // Half done info sync in app layer ++++++++++++++++++++++++++++++++++++

    private void prepareSendProgressContent(MessageEntity messageEntity) {
        ContentModel contentModel = new ContentModel().setContentId(messageEntity.getContentId())
                .setUserId(messageEntity.getFriendsId())
                .setMessageId(messageEntity.getMessageId())
                .setProgress(messageEntity.getContentProgress())
                .setContentInfo(messageEntity.getContentInfo());
        setProgressInfoInMap(contentModel, false);
    }

    private void prepareProgressContent(MessageEntity messageEntity) {
        ContentModel contentModel = new ContentModel().setContentId(messageEntity.getContentId())
                .setContentPath(messageEntity.getContentPath())
                .setUserId(messageEntity.getFriendsId())
                .setMessageId(messageEntity.getMessageId())
                .setProgress(messageEntity.getContentProgress())
                .setContentInfo(messageEntity.getContentInfo())
                .setMessageType(messageEntity.getMessageType());
        setProgressInfoInMap(contentModel, true);
    }

    // --------------------------------------------------------

    // DB update task +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void setMessageContentId(String messageId, String contentId, String contentPath) {
        if (TextUtils.isEmpty(messageId) || TextUtils.isEmpty(contentId))
            return;

        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromId(messageId);
        if (messageEntity != null) {
            messageEntity.setContentId(contentId);
            if (!TextUtils.isEmpty(contentPath)) {
                messageEntity.setContentPath(contentPath);
            }
            MessageSourceData.getInstance().insertOrUpdateData(messageEntity);
        }
    }

    public void updateMessageStatus(String messageId) {
        if (TextUtils.isEmpty(messageId))
            return;

        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromId(messageId);

        // FAILED MAINTAINED
        if (messageEntity != null) {

            inComingFailedToGeneral(messageEntity);
            MessageSourceData.getInstance().insertOrUpdateData(messageEntity);
        }
    }

    public void setContentProgress(String messageId, int progress, String contentId) {
        if (progress == 0 || TextUtils.isEmpty(messageId))
            return;
        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromId(messageId);
        if (messageEntity != null) {
            int existingProgress = messageEntity.getContentProgress();
            if (progress > existingProgress) {
                messageEntity.setContentProgress(progress);
                messageEntity.setContentId(contentId);

                if (messageEntity.isIncoming() &&
                        messageEntity.getContentStatus() != Constants.ContentStatus.CONTENT_STATUS_RECEIVED) {
                    messageEntity.setContentStatus(Constants.ContentStatus.CONTENT_STATUS_RECEIVING);
                }

                if (messageEntity.isIncoming()) {
                    inComingFailedToGeneral(messageEntity);
                } else {
                    if (messageEntity.getStatus() != Constants.MessageStatus.STATUS_RECEIVED) {
                        messageEntity.setStatus(Constants.MessageStatus.STATUS_SENDING_START);
                    }
                }

                MessageSourceData.getInstance().insertOrUpdateData(messageEntity);

                if (progress == 100){
                    contentReceiveDone(contentId, true);
                }

            }
        }
    }

    public ContentModel setContentProgressByContentIdForSender(String contentId, int progress) {
        if (TextUtils.isEmpty(contentId))
            return null;

        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromContentId(contentId);
        if (messageEntity != null) {
            int existingProgress = messageEntity.getContentProgress();
            if (progress > existingProgress) {
                messageEntity.setContentProgress(progress);
            }

            messageEntity.setContentId(contentId);
            messageEntity.setStatus(Constants.MessageStatus.STATUS_SENDING_START);
            MessageSourceData.getInstance().insertOrUpdateData(messageEntity);

            return new ContentModel().setMessageId(messageEntity.getMessageId())
                    .setUserId(messageEntity.getFriendsId());
        }
        return null;
    }

    private ContentMetaInfo getContentMetaInfoByContentId(String contentId) {
        ContentMetaInfo contentMetaInfo = new ContentMetaInfo();
        try {
            MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromContentId(contentId);
            if (messageEntity != null) {
                contentMetaInfo.setMessageId(messageEntity.getMessageId())
                        .setMessageType(messageEntity.getMessageType())
                        .setMetaInfo(messageEntity.getContentInfo());
                return contentMetaInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentMetaInfo;
    }

    // -----------------------------------------------------------

    /////////??????????????????????????????????????????????????????/////////
    /////////??????????????????????????????????????????????????????/////////
    /////////?????????                                   ??????????/////////
    /////////?????????          Gateway API's            ??????????/////////
    /////////?????????                                   ??????????/////////
    /////////??????????????????????????????????????????????????????/////////
    /////////??????????????????????????????????????????????????????/////////

    void contentDataSend(String contentInfo, ContentModel contentModel) {

        if (contentModel.isRequestFromReceiver()) {
            return;
        }

        if (!TextUtils.isEmpty(contentInfo)) {

            try {
                JSONObject jsonObject = new JSONObject(contentInfo);
                boolean success = jsonObject.getBoolean("success");
                String msg = jsonObject.getString("msg");

                if (success) {

                    ContentSendModel contentSendModel = contentSendModelHashMap.get(msg);

                    if (contentSendModel == null) {
                        contentSendModel = new ContentSendModel();
                    }

                    contentSendModel.contentId = msg;
                    contentSendModel.messageId = contentModel.getMessageId();
                    contentSendModel.userId = contentModel.getUserId();
                    contentSendModel.successStatus = true;

                    HandlerUtil.postBackground(() -> setMessageContentId(contentModel.getMessageId(),
                            msg, contentModel.getContentPath()));
                    contentSendModelHashMap.put(msg, contentSendModel);
                } else {
                    contentModel.setAckStatus(Constants.MessageStatus.STATUS_FAILED);
                    HandlerUtil.postBackground(() -> setContentMessage(contentModel, false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            contentModel.setAckStatus(Constants.MessageStatus.STATUS_FAILED);
            HandlerUtil.postBackground(() -> setContentMessage(contentModel, false));
        }
    }

    private void setProgressInfoInMap(ContentModel contentModel, boolean isReceived) {

        if (isReceived) {
            if (contentReceiveModelHashMap.get(contentModel.getContentId()) == null) {
                ContentMetaInfo contentMetaInfo = new ContentMetaInfo()
                        .setMessageId(contentModel.getMessageId())
                        .setMessageType(contentModel.getMessageType())
                        .setMetaInfo(contentModel.getContentInfo());

                ContentReceiveModel contentReceiveModel = new ContentReceiveModel()
                        .setContentId(contentModel.getContentId())
                        .setContentPath(contentModel.getContentPath())
                        .setUserId(contentModel.getUserId())
                        .setContentMetaInfo(contentMetaInfo)
                        .setSuccessStatus(true);

                contentReceiveModelHashMap.put(contentModel.getContentId(), contentReceiveModel);
                contentSendModelHashMap.remove(contentModel.getContentId());
            }
        } else {
            if (contentSendModelHashMap.get(contentModel.getContentId()) == null) {
                ContentSendModel contentSendModel = new ContentSendModel();

                contentSendModel.contentId = contentModel.getContentId();
                contentSendModel.messageId = contentModel.getMessageId();
                contentSendModel.userId = contentModel.getUserId();
                contentSendModel.successStatus = true;

                contentSendModelHashMap.put(contentModel.getContentId(), contentSendModel);
            }
        }
    }

    // Callback action +++++++++++++++++++++++++++++++++++++++++++++++++++++
    void contentReceiveStart(String contentId, String contentPath, String userId, byte[] metaData) {

        try {
            Timber.tag("FileMessage").v(" Start id: %s", contentId);

            ContentReceiveModel contentReceiveModel = contentReceiveModelHashMap.get(contentId);

            ContentMetaInfo contentMetaInfo = null;

            if (metaData != null) {
                String contentMessageText = new String(metaData);
                contentMetaInfo = new Gson().fromJson(contentMessageText,
                        ContentMetaInfo.class);
            }

            if (contentReceiveModel == null) {
                contentReceiveModel = new ContentReceiveModel();
            }

            contentReceiveModel
                    .setContentId(contentId)
                    .setContentPath(contentPath)
                    .setUserId(userId)
                    .setContentMetaInfo(contentMetaInfo)
                    .setSuccessStatus(true);

            if (contentMetaInfo != null) {

                ContentMetaInfo finalContentMetaInfo = contentMetaInfo;

                HandlerUtil.postBackground(() -> updateMessageStatus(finalContentMetaInfo.getMessageId()));

                String thumbPath = "";
                if (contentMetaInfo.getThumbData() != null) {
                    thumbPath = ContentUtil.getInstance().getThumbFileFromByte(contentMetaInfo.getThumbData());
                }


                ContentModel contentModel = new ContentModel()
                        .setMessageId(contentMetaInfo.getMessageId())
                        .setMessageType(contentMetaInfo.getMessageType())
                        .setContentPath(contentPath)
                        .setThumbPath(thumbPath)
                        .setContentId(contentId)
                        .setUserId(contentReceiveModel.getUserId())
                        .setReceiveSuccessStatus(true)
                        .setContentInfo(contentMetaInfo.getMetaInfo())
                        .setReceivingStatus(Constants.ContentStatus.CONTENT_STATUS_RECEIVING);

                HandlerUtil.postBackground(() -> setContentMessage(contentModel, true));
            }
            contentReceiveModelHashMap.put(contentId, contentReceiveModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void contentReceiveInProgress(String contentId, int progress) {

        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromContentId(contentId);
        if (messageEntity == null) {
            ContentSequenceModel contentSequenceModel = new ContentSequenceModel().setContentId(contentId)
                    .setProgress(progress).setReceiveStatus(CONTENT_PROGRESS);
            contentSequenceModels.add(contentSequenceModel);
            return;
        }

        if (progress > 100)
            progress = 100;

        Timber.tag("FileMessage").v(" Progress: %s", progress);
        ContentReceiveModel contentReceiveModel = contentReceiveModelHashMap.get(contentId);
        if (contentReceiveModel != null) {
            contentReceiveModel.setContentReceiveProgress(progress);
            contentReceiveModelHashMap.put(contentId, contentReceiveModel);

            ContentMetaInfo contentMetaInfo = contentReceiveModel.getContentMetaInfo();

            if (contentMetaInfo == null) {
                contentMetaInfo = getContentMetaInfoByContentId(contentId);
                contentReceiveModel.setContentMetaInfo(contentMetaInfo);
                contentReceiveModelHashMap.put(contentId, contentReceiveModel);
            }

            String messageId = contentMetaInfo.getMessageId();
            setContentProgress(messageId, progress, contentId);

            return;
        }

        ContentSendModel contentSendModel = contentSendModelHashMap.get(contentId);
        if (contentSendModel != null) {
            setContentProgress(contentSendModel.messageId, progress, contentSendModel.contentId);
            contentSendModel.contentReceiveProgress = progress;

            contentSendModelHashMap.put(contentId, contentSendModel);
        } else {

            ContentModel contentModel = setContentProgressByContentIdForSender(contentId, progress);

            if (contentModel != null) {
                contentSendModel = new ContentSendModel();

                contentSendModel.contentId = contentId;
                contentSendModel.messageId = contentModel.getMessageId();
                contentSendModel.userId = contentModel.getUserId();
                contentSendModel.successStatus = true;

                contentSendModelHashMap.put(contentId, contentSendModel);

                if (progress == 100){
                    contentReceiveDone(contentId, true);
                }
            }
        }
    }

    void contentReceiveDone(String contentId, boolean contentStatus) {
        Log.v("FILE_SPEED_TEST_13 ", Calendar.getInstance().getTime()+"");
        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromContentId(contentId);
        if (messageEntity == null) {
            ContentSequenceModel contentSequenceModel = new ContentSequenceModel().setContentId(contentId)
                    .setContentStatus(contentStatus).setReceiveStatus(CONTENT_RECEIVED);
            contentSequenceModels.add(contentSequenceModel);
            return;
        }

        ContentReceiveModel contentReceiveModel = contentReceiveModelHashMap.get(contentId);
        if (contentReceiveModel != null) {

            if (messageEntity.getContentStatus() == Constants.ContentStatus.CONTENT_STATUS_RECEIVED){
                return;
            }
            ContentMetaInfo contentMetaInfo = contentReceiveModel.getContentMetaInfo();

            String contentPath = null, thumbPath = null;

            if (contentMetaInfo == null) {
                contentMetaInfo = getContentMetaInfoByContentId(contentId);
            }

            ContentModel contentModel = new ContentModel()
                    .setMessageId(contentMetaInfo.getMessageId())
                    .setMessageType(contentMetaInfo.getMessageType())
                    .setContentPath(contentPath)
                    .setThumbPath(thumbPath)
                    .setContentId(contentId)
                    .setUserId(contentReceiveModel.getUserId())
                    .setReceiveSuccessStatus(contentStatus)
                    .setContentInfo(contentMetaInfo.getMetaInfo())
                    .setReceivingStatus(Constants.ContentStatus.CONTENT_STATUS_RECEIVED);

            HandlerUtil.postBackground(() -> setContentMessage(contentModel, true));

            return;
        }

        /*****************************Sender side calculation*******************************/
        ContentSendModel contentSendModel = contentSendModelHashMap.get(contentId);
        if (contentSendModel != null) {
            if (contentStatus) {
                ContentModel contentModel = new ContentModel()
                        .setMessageId(contentSendModel.messageId)
                        .setAckStatus(Constants.MessageStatus.STATUS_RECEIVED);

                HandlerUtil.postBackground(() -> setContentMessage(contentModel, false));

            } else {

                ContentModel contentModel = new ContentModel()
                        .setMessageId(contentSendModel.messageId)
                        .setAckStatus(Constants.MessageStatus.STATUS_FAILED);

                HandlerUtil.postBackground(() -> setContentMessage(contentModel, false));
            }
            contentSendModelHashMap.remove(contentId);
        }
    }

    void pendingContents(ContentPendingModel contentPendingModel) {
        if (contentPendingModel.isIncoming()) {

            ContentMetaInfo contentMetaInfo = contentPendingModel.getContentMetaInfo();
            String filePath = contentPendingModel.getContentPath();
            String userAddress = contentPendingModel.getSenderId();

            String contentId = contentPendingModel.getContentId();
            int state = contentPendingModel.getState();
            int progress = contentPendingModel.getProgress();

            if (!TextUtils.isEmpty(filePath)) {
                // receive started from root
                if (contentMetaInfo != null) {

                    String thumbPath = getThumbPathFromContent(contentId, contentMetaInfo.getThumbData());
                    String contentPath = state == Constants.ServiceContentState.SUCCESS ?
                            ContentUtil.getInstance().getCopiedFilePath(filePath, false) : filePath;

                    prepareMessageContentModel(contentMetaInfo.getMessageId(), contentPath, thumbPath,
                            userAddress, contentId, contentMetaInfo.getMessageType(), progress, state,
                            contentMetaInfo.getMetaInfo());
                } else {
                    prepareMessageContentModel(contentId, progress, state);
                }
            } else {
                if (!TextUtils.isEmpty(contentId)) {
                    prepareMessageContentModel(contentId, progress, state);
                }
            }
        } else {
            String contentId = contentPendingModel.getContentId();
            int state = contentPendingModel.getState();
            int progress = contentPendingModel.getProgress();
            String userId = contentPendingModel.getSenderId();

            if (!TextUtils.isEmpty(contentId)) {
                prepareSendContentModel(contentId, userId, progress, state);
            }
        }
    }

    private String getThumbPathFromContent(String contentId, byte[] thumbData) {
        MessageEntity messageEntity = MessageSourceData.getInstance()
                .getMessageEntityFromContentId(contentId);

        if (messageEntity != null) {
            return messageEntity.getContentThumbPath();
        }

        if (thumbData != null) {
            return ContentUtil.getInstance().getThumbFileFromByte(thumbData);
        }

        return null;
    }

    // ---------------------------------------------------------------------

    private void prepareMessageContentModel(String contentId, int progress, int status) {
        ContentModel contentModel = new ContentModel()
                .setContentId(contentId)
                .setProgress(progress)
                .setAckStatus(status);

        HandlerUtil.postBackground(() -> receiveIncomingContentInfo(contentModel));
    }

    private void prepareSendContentModel(String contentId, String userId, int progress, int status) {
        ContentModel contentModel = new ContentModel()
                .setContentId(contentId)
                .setProgress(progress)
                .setUserId(userId)
                .setAckStatus(status);

        HandlerUtil.postBackground(() -> sendOutgoingContentInfo(contentModel));
    }

    private void prepareMessageContentModel(String messageId, String contentPath, String thumbPath,
                                            String userId, String contentId, int messageType,
                                            int progress, int status, String contentInfo) {
        ContentModel contentModel = new ContentModel()
                .setMessageId(messageId)
                .setMessageType(messageType)
                .setContentPath(contentPath)
                .setThumbPath(thumbPath)
                .setUserId(userId)
                .setContentId(contentId)
                .setProgress(progress)
                .setContentInfo(contentInfo)
                .setAckStatus(status);

        HandlerUtil.postBackground(() -> receiveIncomingContentInfo(contentModel));
    }

    // --------------------------------------------------------------------
}
