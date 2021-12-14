package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.w3engineers.mesh.util.MeshLog;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.models.ContentMetaInfo;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.GroupContentEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.GroupMessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.NotifyUtil;

import org.json.JSONException;
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

    public void resendContentAction(MessageEntity messageEntity) {
        try {
            dataSource.updateMessageStatus(messageEntity.getMessageId(), Constants.MessageStatus.STATUS_SENDING_START);
            contentMessageResendSend(messageEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resendContentRequestAction(MessageEntity messageEntity) {
        try {
            dataSource.updateMessageStatus(messageEntity.getMessageId(), Constants.MessageStatus.STATUS_READ);
            contentMessageResendRequest(messageEntity, messageEntity.getFriendsId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Content send action +++++++++++++++++++++++++++++++++++++++++++++++++++

    private void contentMessageSend(@NonNull MessageEntity messageEntity) {
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

    private void contentMessageResendSend(@NonNull MessageEntity messageEntity) {

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

    private void contentMessageResendRequest(@NonNull MessageEntity messageEntity, String userId) {
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

    public void setContentMessageFromMessageEntity(String contentId, String messageId){
        ContentModel contentModel = new ContentModel()
                .setMessageId(messageId)
                //.setAckStatus(Constants.MessageStatus.STATUS_RECEIVED);
                .setAckStatus(Constants.MessageStatus.STATUS_UNREAD); // we set it unread if it is received

        setContentMessage(contentModel, false);

        if (!TextUtils.isEmpty(contentId)) {
            prepareRightMeshDataSource();
            rightMeshDataSource.removeSendContents(contentId);
        }
    }

    // ------------------------------------------------------------------------

    // Cross check between sender and receiver +++++++++++++++++++++++++++++++++

    protected void contentMessageSuccessResponse(byte[] rawData) {

        if (rawData == null)
            return;
        String messageId = new String(rawData);

        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromId(messageId);

        if (messageEntity != null) {
            setContentMessageFromMessageEntity(messageEntity.getContentId(), messageId);
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

    public void successForMainContent(String userId, String messageId) {
        if (!TextUtils.isEmpty(messageId)) {
            dataSend(messageId.getBytes(), Constants.DataType.SUCCESS_CONTENT_MESSAGE, userId, false);
        }
    }

    // -------------------------------------------------------------------------

    private void setGroupContentMessage(@NonNull ContentModel contentModel, boolean isNewMessage) {
/*        String messageId = contentModel.getMessageId();
        int ackStatus = contentModel.getAckStatus();

        try {
            if (isNewMessage) {

                GroupMessageEntity groupMessageEntity = MessageSourceData.getInstance().getGroupMessageEntityFromId(messageId);


                if (groupMessageEntity == null) {

                    int contentStatus = contentModel.getReceivingStatus();
                    String userId = contentModel.getUserId();

                    GroupMessageEntity newMessageEntity = new GroupMessageEntity()
                            .setMessage("Image")
                            .setContentPath(contentModel.getContentPath())
                            .setContentThumb(contentModel.getThumbPath())
                            .setContentInfo(contentModel.getContentInfo());

                    //TODO senderId should be nearby real sender.
                    GroupContentEntity groupContentEntity = new GroupContentEntity()
                            .setContentId(contentModel.getContentId())
                            .setContentMessageId(contentModel.getMessageId())
                            .setSenderId(contentModel.getOriginalSender())
                            .setReceiverId(getMyMeshId());
                    MessageSourceData.getInstance().addOrUpdateContent(groupContentEntity);


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
                                contentReceiveDone(contentSequenceModel.getContentId(), contentSequenceModel.isContentStatus(), "");
                                sequenceModelIterator.remove();
                            }
                        }
                    }

                } else {
                    String thumbPath = contentModel.getThumbPath(), contentPath = contentModel.getContentPath();
                    int contentStatus = contentModel.getReceivingStatus();

                    if (!TextUtils.isEmpty(thumbPath)) {
                        groupMessageEntity.setContentThumb(thumbPath);
                    }

                    if (!TextUtils.isEmpty(contentPath)) {
                        groupMessageEntity.setContentPath(contentPath);
                    }


                    GroupContentEntity groupContentEntity = MessageSourceData.getInstance().getContentById(contentModel.getContentId());
                    if (groupContentEntity == null) {
                        groupContentEntity = new GroupContentEntity()
                                .setContentId(contentModel.getContentId())
                                .setContentMessageId(contentModel.getMessageId())
                                .setSenderId(contentModel.getOriginalSender())
                                .setReceiverId(getMyMeshId());
                        MessageSourceData.getInstance().addOrUpdateContent(groupContentEntity);
                    }

                    if (groupMessageEntity.getContentStatus() != Constants.ContentStatus.CONTENT_STATUS_RECEIVED
                            && !contentModel.getReceiveSuccessStatus()) {
                        // FAILED MAINTAINED

                        inComingGeneralToFailed(groupMessageEntity);
                    } else {
                        if (contentStatus != -1) {
                            groupMessageEntity.setContentStatus(contentStatus);

                            inComingFailedToGeneral(groupMessageEntity);

                        }
                    }
                    MessageSourceData.getInstance().insertOrUpdateData(groupMessageEntity);
                }

                if (contentModel.getReceiveSuccessStatus() && contentModel.getReceivingStatus() ==
                        Constants.ContentStatus.CONTENT_STATUS_RECEIVED) {
                    successForMainContent(contentModel.getUserId(), messageId);
                }

            } else {
                GroupMessageEntity groupContentEntity = MessageSourceData.getInstance().getGroupMessageEntityFromId(messageId);

                if (groupContentEntity != null) {
                    if (!groupContentEntity.isIncoming()) {
                        if (groupContentEntity.getStatus() == Constants.MessageStatus.STATUS_RECEIVED) {
                            ackStatus = groupContentEntity.getStatus();
                        }
                    }
                }
                dataSource.updateGroupMessageStatus(messageId, ackStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void setContentMessage(@NonNull ContentModel contentModel, boolean isNewMessage) {
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
                                contentReceiveDone(contentSequenceModel.getContentId(), contentSequenceModel.isContentStatus(), "");
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

    private void inComingFailedToGeneral(ChatEntity messageEntity) {
        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD) {
            messageEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
        } else {
            messageEntity.setStatus(Constants.MessageStatus.STATUS_READ);
        }
    }

    private void inComingGeneralToFailed(ChatEntity messageEntity) {
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

                if (progress == 100) {
                    contentReceiveDone(contentId, true, "");
                }

            }
        }
    }

    public void setGroupContentProgress(String messageId, int progress, String contentId) {
/*        if (progress == 0 || TextUtils.isEmpty(messageId))
            return;
        Log.e("Group_content", "Group content progress: " + progress);

        GroupMessageEntity messageEntity = MessageSourceData.getInstance().getGroupMessageEntityByContentId(contentId);
        if (messageEntity != null) {
            int existingProgress = messageEntity.getContentProgress();
            if (progress > existingProgress) {
                messageEntity.setContentProgress(progress);

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

                if (progress == 100) {
                    contentReceiveDone(contentId, true, "");
                }

            }
        }*/
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

    private ContentMetaInfo getContentMetaInfoByContentId(String contentId, boolean isGroup) {
        return getMetaByID(contentId, isGroup);
    }

    public ContentMetaInfo getMetaByID(String contentId, boolean isGroup){
        ContentMetaInfo contentMetaInfo = new ContentMetaInfo();
        try {
            ChatEntity chatEntity = null;
            String contentInfo = null;
            if (isGroup) {
                chatEntity = MessageSourceData.getInstance().getGroupMessageEntityByContentId(contentId);
                contentInfo = ((GroupMessageEntity) chatEntity).getContentInfo();

            } else {
                chatEntity = MessageSourceData.getInstance().getMessageEntityFromContentId(contentId);
                contentInfo = ((MessageEntity) chatEntity).getContentInfo();
            }

            if (chatEntity != null) {
                contentMetaInfo.setMessageId(chatEntity.getMessageId())
                        .setMessageType(chatEntity.getMessageType())
                        .setMetaInfo(contentInfo);
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


    void onGroupContentDataSend(String contentSendInfo, ContentModel contentModel) {
      /*  try {
            JSONObject jsonObject = new JSONObject(contentSendInfo);
            boolean success = jsonObject.getBoolean("success");
            String msg = jsonObject.getString("msg");
            if (success) {
                GroupContentEntity contentEntity = new GroupContentEntity()
                        .setContentId(msg)
                        .setReceiverId(contentModel.getUserId())
                        .setContentMessageId(contentModel.getMessageId());
                MessageSourceData.getInstance().addOrUpdateContent(contentEntity);
            } else {
                //TODO failed state handle
            }
        } catch (JSONException e) {

        }*/
    }

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
                    HandlerUtil.postForeground(() -> {
                        if (jsonObject.has("file_size")) {
                            showAlertDialog(msg);
                        }
                    });


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            contentModel.setAckStatus(Constants.MessageStatus.STATUS_FAILED);
            HandlerUtil.postBackground(() -> setContentMessage(contentModel, false));
        }
    }

    public void showAlertDialog(String message) {
        Activity activity = TeleMeshApplication.getCurrentActivity();
        if (activity == null) return;
        AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.alert_hardware_permission, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        TextView title = dialogView.findViewById(R.id.interruption_title);
        TextView messageTv = dialogView.findViewById(R.id.interruption_message);
        Button okay = dialogView.findViewById(R.id.okay_button);
        title.setText("File size alert");
        messageTv.setText(message);

        alertDialog.setCancelable(false);
        alertDialog.show();
        okay.setOnClickListener(v -> alertDialog.dismiss());
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
        //Log.v("FILE_SPEED_TEST_12 ", Calendar.getInstance().getTime()+"");
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
                if (finalContentMetaInfo.isGroupContent()) {
                    //TODO
                } else {
                    HandlerUtil.postBackground(() -> updateMessageStatus(finalContentMetaInfo.getMessageId()));
                }

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
                        .setReceivingStatus(Constants.ContentStatus.CONTENT_STATUS_RECEIVING)
                        .setGroupContent(contentMetaInfo.isGroupContent());

                if (contentModel.isGroupContent()) {
                    //HandlerUtil.postBackground(() -> setGroupContentMessage(contentModel, true));
                } else {
                    HandlerUtil.postBackground(() -> setContentMessage(contentModel, true));
                }
            }
            contentReceiveModelHashMap.put(contentId, contentReceiveModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contentReceiveInProgress(String contentId, int progress) {
        // Log.v("FILE_SPEED_TEST_12.5 ", Calendar.getInstance().getTime()+"");
        boolean isGroup = false;
        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromContentId(contentId);
        if (messageEntity == null) {
            GroupMessageEntity groupMessageEntity = MessageSourceData.getInstance().getGroupMessageEntityByContentId(contentId);
            if (groupMessageEntity == null) {
                ContentSequenceModel contentSequenceModel = new ContentSequenceModel().setContentId(contentId)
                        .setProgress(progress).setReceiveStatus(CONTENT_PROGRESS);
                contentSequenceModels.add(contentSequenceModel);
                return;
            } else {
                isGroup = true;
            }
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
                contentMetaInfo = getContentMetaInfoByContentId(contentId, isGroup);
                contentReceiveModel.setContentMetaInfo(contentMetaInfo);

                contentReceiveModelHashMap.put(contentId, contentReceiveModel);
            }

            String messageId = contentMetaInfo.getMessageId();
            if (isGroup) {
                //setGroupContentProgress(messageId, progress, contentId);
            } else {
                setContentProgress(messageId, progress, contentId);
            }
            return;
        }

        if (!isGroup) {
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

                    if (progress == 100) {
                        contentReceiveDone(contentId, true, "");
                    }
                }
            }
        }
    }

    void contentReceiveDone(String contentId, boolean contentStatus, String msg) {
        //Log.v("FILE_SPEED_TEST_13 ", Calendar.getInstance().getTime()+"");

        boolean isGroup = false;
        GroupMessageEntity groupMessageEntity = null;
        MessageEntity messageEntity = MessageSourceData.getInstance().getMessageEntityFromContentId(contentId);
        if (messageEntity == null) {
            groupMessageEntity = MessageSourceData.getInstance().getGroupMessageEntityByContentId(contentId);
            if (groupMessageEntity == null) {
                ContentSequenceModel contentSequenceModel = new ContentSequenceModel().setContentId(contentId)
                        .setContentStatus(contentStatus).setReceiveStatus(CONTENT_RECEIVED);
                contentSequenceModels.add(contentSequenceModel);
                return;
            } else {
                isGroup = true;
            }
        }

        if (!contentStatus) {
            HandlerUtil.postForeground(() -> {
                Toast.makeText(TeleMeshApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
            });
        }

        ContentReceiveModel contentReceiveModel = contentReceiveModelHashMap.get(contentId);
        if (contentReceiveModel != null) {

            if ((messageEntity != null && messageEntity.getContentStatus() == Constants.ContentStatus.CONTENT_STATUS_RECEIVED)
                    || (groupMessageEntity != null && groupMessageEntity.getContentStatus() == Constants.ContentStatus.CONTENT_STATUS_RECEIVED)) {
                return;
            }

            ContentMetaInfo contentMetaInfo = contentReceiveModel.getContentMetaInfo();

            String contentPath = null, thumbPath = null;

            if (contentMetaInfo == null) {
                contentMetaInfo = getContentMetaInfoByContentId(contentId, isGroup);
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

            if (isGroup) {
                //andlerUtil.postBackground(() -> setGroupContentMessage(contentModel, true));
            } else {
                HandlerUtil.postBackground(() -> setContentMessage(contentModel, true));
            }


            return;
        }

        /*****************************Sender side calculation*******************************/
        ContentSendModel contentSendModel = contentSendModelHashMap.get(contentId);
        if (contentSendModel != null) {
            if (contentStatus) {
                ContentModel contentModel = new ContentModel()
                        .setMessageId(contentSendModel.messageId)
                        .setAckStatus(Constants.MessageStatus.STATUS_RECEIVED);

                if (isGroup) {
                    //HandlerUtil.postBackground(() -> setGroupContentMessage(contentModel, false));
                } else {
                    HandlerUtil.postBackground(() -> setContentMessage(contentModel, false));
                }

            } else {

                ContentModel contentModel = prepareContentModel(contentSendModel.messageId);

                if (isGroup) {
                    //HandlerUtil.postBackground(() -> setGroupContentMessage(contentModel, false));
                } else {
                    HandlerUtil.postBackground(() -> setContentMessage(contentModel, false));
                }

            }
            contentSendModelHashMap.remove(contentId);
        }
    }

    public ContentModel prepareContentModel(String messageId){
        ContentModel contentModel = new ContentModel()
                .setMessageId(messageId)
                .setAckStatus(Constants.MessageStatus.STATUS_FAILED);
       return contentModel;
    }

    void pendingContents(@NonNull ContentPendingModel contentPendingModel) {
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

    public void addReceiveContentInMap(String contentId, ContentReceiveModel receiveModel) {
        contentReceiveModelHashMap.put(contentId, receiveModel);
    }

    public void addSendContentInMap(String contentId, ContentSendModel sendModel) {
        contentSendModelHashMap.put(contentId, sendModel);
    }

    // --------------------------------------------------------------------
}
