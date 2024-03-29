package com.w3engineers.unicef.telemesh.ui.chat;

import androidx.paging.PagedListAdapter;

import android.content.Context;

import androidx.databinding.ViewDataBinding;

import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.w3engineers.mesh.util.MeshLog;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.GroupMessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupImageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupImageOutBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupInfoBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupTextMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupTextMessageOutBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupVideoInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupVideoOutBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemImageMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemImageMessageOutBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemMessageSeparatorBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageOutBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemVideoMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemVideoMessageOutBinding;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import io.supercharge.shimmerlayout.ShimmerLayout;

import static com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity.DIFF_CALLBACK;

public class ChatPagedAdapterRevised extends PagedListAdapter<ChatEntity, ChatPagedAdapterRevised.GenericViewHolder> {

    private View.OnClickListener clickListener;
    private HashMap<String, UserEntity> userMap;

    @NonNull
    public Context mContext;
    @Nullable
    protected ChatViewModel chatViewModel;


    public ChatPagedAdapterRevised(@NonNull Context context, @Nullable ChatViewModel chatViewModel,
                                   View.OnClickListener onClickListener) {
        super(DIFF_CALLBACK);
        mContext = context;
        userMap = new HashMap<>();

        this.chatViewModel = chatViewModel;
        this.clickListener = onClickListener;
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        ChatEntity chatEntity = getItem(position);
        if (chatEntity instanceof GroupMessageEntity) {
            if (chatEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
                return chatEntity.isIncoming ? Constants.ViewHolderType.GROUP_TEXT_INCOMING : Constants.ViewHolderType.GROUP_TEXT_OUTGOING;
            } else if (chatEntity.getMessageType() == Constants.MessageType.IMAGE_MESSAGE) {
                return chatEntity.isIncoming ? Constants.ViewHolderType.GROUP_IMG_INCOMING : Constants.ViewHolderType.GROUP_IMG_OUTGOING;
            } else {
                return chatEntity.isIncoming ? Constants.ViewHolderType.GROUP_VID_INCOMING : Constants.ViewHolderType.GROUP_VID_OUTGOING;
            }
        }

        if (chatEntity != null) {

            if (chatEntity.getMessageType() != Constants.MessageType.DATE_MESSAGE) {

                if (chatEntity.getMessageType() == Constants.MessageType.GROUP_CREATE
                        || chatEntity.getMessageType() == Constants.MessageType.GROUP_JOIN
                        || chatEntity.getMessageType() == Constants.MessageType.GROUP_LEAVE
                        || chatEntity.getMessageType() == Constants.MessageType.GROUP_RENAMED
                        || chatEntity.getMessageType() == Constants.MessageType.GROUP_MEMBER_ADD
                        || chatEntity.getMessageType() == Constants.MessageType.GROUP_MEMBER_REMOVE) {
                    return Constants.ViewHolderType.GROUP_INFO;
                }

                if (chatEntity.isIncoming()) {
                    switch (chatEntity.getMessageType()) {
                        case Constants.MessageType.TEXT_MESSAGE:
                            return Constants.ViewHolderType.TEXT_INCOMING;
                        case Constants.MessageType.IMAGE_MESSAGE:
                            return Constants.ViewHolderType.IMG_INCOMING;
                        case Constants.MessageType.VIDEO_MESSAGE:
                            return Constants.ViewHolderType.VID_INCOMING;
                    }
                } else {
                    switch (chatEntity.getMessageType()) {
                        case Constants.MessageType.TEXT_MESSAGE:
                            return Constants.ViewHolderType.TEXT_OUTGOING;
                        case Constants.MessageType.IMAGE_MESSAGE:
                            return Constants.ViewHolderType.IMG_OUTGOING;
                        case Constants.MessageType.VIDEO_MESSAGE:
                            return Constants.ViewHolderType.VID_OUTGOING;
                    }
                }
            }
        }

        return -1;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == Constants.ViewHolderType.TEXT_INCOMING) {
            ItemTextMessageInBinding itemTextMessageInBinding = ItemTextMessageInBinding.inflate(inflater, viewGroup, false);
            return new TextMessageInHolder(itemTextMessageInBinding);
        } else if (viewType == Constants.ViewHolderType.TEXT_OUTGOING) {
            ItemTextMessageOutBinding itemTextMessageOutBinding = ItemTextMessageOutBinding.inflate(inflater, viewGroup, false);
            return new TextMessageOutHolder(itemTextMessageOutBinding);
        } else if (viewType == Constants.ViewHolderType.IMG_INCOMING) {
            ItemImageMessageInBinding itemImageMessageInBinding = ItemImageMessageInBinding.inflate(inflater, viewGroup, false);
            return new ImageMessageInHolder(itemImageMessageInBinding);
        } else if (viewType == Constants.ViewHolderType.IMG_OUTGOING) {
            ItemImageMessageOutBinding itemImageMessageOutBinding = ItemImageMessageOutBinding.inflate(inflater, viewGroup, false);
            return new ImageMessageOutHolder(itemImageMessageOutBinding);
        } else if (viewType == Constants.ViewHolderType.VID_INCOMING) {
            ItemVideoMessageInBinding itemVideoMessageInBinding = ItemVideoMessageInBinding.inflate(inflater, viewGroup, false);
            return new VideoMessageInHolder(itemVideoMessageInBinding);
        } else if (viewType == Constants.ViewHolderType.VID_OUTGOING) {
            ItemVideoMessageOutBinding itemVideoMessageOutBinding = ItemVideoMessageOutBinding.inflate(inflater, viewGroup, false);
            return new VideoMessageOutHolder(itemVideoMessageOutBinding);
        } else if (viewType == Constants.ViewHolderType.GROUP_INFO) {
            ItemGroupInfoBinding itemGroupInfoBinding = ItemGroupInfoBinding.inflate(inflater, viewGroup, false);
            return new GroupInfoViewHolder(itemGroupInfoBinding);
        } else if (viewType == Constants.ViewHolderType.GROUP_TEXT_INCOMING) {
            ItemGroupTextMessageInBinding viewBinder = ItemGroupTextMessageInBinding.inflate(inflater, viewGroup, false);
            return new GroupTextInHolder(viewBinder);
        } else if (viewType == Constants.ViewHolderType.GROUP_TEXT_OUTGOING) {
            ItemGroupTextMessageOutBinding viewBinder = ItemGroupTextMessageOutBinding.inflate(inflater, viewGroup, false);
            return new GroupTextOutHolder(viewBinder);
        } else if (viewType == Constants.ViewHolderType.GROUP_IMG_INCOMING) {
            ItemGroupImageInBinding viewBinder = ItemGroupImageInBinding.inflate(inflater, viewGroup, false);
            return new GroupImageInHolder(viewBinder);
        } else if (viewType == Constants.ViewHolderType.GROUP_IMG_OUTGOING) {
            ItemGroupImageOutBinding viewBinder = ItemGroupImageOutBinding.inflate(inflater, viewGroup, false);
            return new GroupImageOutHolder(viewBinder);
        } else if (viewType == Constants.ViewHolderType.GROUP_VID_INCOMING) {
            ItemGroupVideoInBinding viewBinder = ItemGroupVideoInBinding.inflate(inflater, viewGroup, false);
            return new GroupVideoInHolder(viewBinder);
        } else if (viewType == Constants.ViewHolderType.GROUP_VID_OUTGOING) {
            ItemGroupVideoOutBinding videBinder = ItemGroupVideoOutBinding.inflate(inflater, viewGroup, false);
            return new GroupVideoOutHolder(videBinder);
        } else {
            ItemMessageSeparatorBinding itemMessageSeparatorBinding = ItemMessageSeparatorBinding.inflate(inflater, viewGroup, false);
            return new SeparatorViewHolder(itemMessageSeparatorBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder baseViewHolder, int position) {

        ChatEntity messageEntity = getItem(position);
        if (messageEntity != null) {
            baseViewHolder.bindView(messageEntity);
        } else {
            baseViewHolder.clearView();
        }
    }

    void addAvatarIndex(List<UserEntity> userEntities, UserEntity myUserEntity) {
        for (UserEntity userEntity : userEntities) {
            userMap.put(userEntity.meshId, userEntity);
        }
        userMap.put(myUserEntity.meshId, myUserEntity);
        notifyDataSetChanged();
    }

    void addAvatarIndex(UserEntity userEntity) {
        userMap.put(userEntity.meshId, userEntity);
        notifyDataSetChanged();
    }

    private int getAvatarIndex(ChatEntity messageEntity) {
        UserEntity userEntity = null;
        if (messageEntity instanceof GroupMessageEntity) {
            userEntity = userMap.get(((GroupMessageEntity) messageEntity).getOriginalSender());
        } else {
            userEntity = userMap.get(((MessageEntity) messageEntity).getFriendsId());
        }

        if (userEntity != null) {
            return userEntity.getAvatarIndex();
        }
        return Constants.DEFAULT_AVATAR;
    }

    private UserEntity getUserName(MessageEntity messageEntity) {
        UserEntity userEntity = userMap.get(messageEntity.friendsId);
        if (userEntity != null) {
            return userEntity;
        }
        return null;
    }

    private UserEntity getUserName(GroupMessageEntity messageEntity) {
        UserEntity userEntity = userMap.get(messageEntity.friendsId);
        if (userEntity != null) {
            return userEntity;
        }
        return null;
    }

    public abstract class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bindView(@NonNull ChatEntity item);

        protected abstract void clearView();
    }

    private class TextMessageInHolder extends GenericViewHolder {
        private ItemTextMessageInBinding binding;

        public TextMessageInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemTextMessageInBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            MessageEntity item = (MessageEntity) chatEntity;
            binding.setTextMessage(item);
            binding.setAvatarIndex(getAvatarIndex(item));
            ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.white));
            UserEntity entity = getUserName(item);
            String name = entity == null ? "" : entity.userName;
            binding.userName.setText("" + name);

            if (entity == null) {
                UIHelper.updateImageNameField(binding.textViewImageName, "", "");
            } else {
                UIHelper.updateImageNameField(binding.textViewImageName, entity.userName, entity.getUserLastName());
            }

        }

        @Override
        protected void clearView() {
            binding.textViewMessage.invalidate();
        }
    }

    private class TextMessageOutHolder extends GenericViewHolder {
        private ItemTextMessageOutBinding binding;

        public TextMessageOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemTextMessageOutBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            MessageEntity item = (MessageEntity) chatEntity;
            binding.setTextMessage(item);
            binding.setChatViewModel(chatViewModel);
        }

        @Override
        protected void clearView() {
            binding.textViewMessage.invalidate();
        }
    }

    private class ImageMessageInHolder extends GenericViewHolder {
        private ItemImageMessageInBinding binding;

        public ImageMessageInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemImageMessageInBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            MessageEntity messageEntity = (MessageEntity) chatEntity;
            Log.v("FILE_SPEED_TEST_14 ", Calendar.getInstance().getTime() + "");

            binding.setTextMessage(messageEntity);
            binding.setAvatarIndex(getAvatarIndex(messageEntity));

            incomingShimmerEffect(binding.shimmerIncomingLoading, messageEntity);
            incomingLoadingEffect(binding.circleView, messageEntity);

            binding.viewFailed.setVisibility(View.GONE);
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED
                    || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED) {
                binding.viewFailed.setVisibility(View.VISIBLE);
            } else {
                binding.viewFailed.setVisibility(View.GONE);
            }

            binding.imageViewMessage.setTag(R.id.image_view_message, messageEntity);
            binding.imageViewMessage.setOnClickListener(clickListener);

            binding.hover.setTag(R.id.image_view_message, messageEntity);
            binding.hover.setOnClickListener(clickListener);

            binding.hoverView.setTag(R.id.image_view_message, messageEntity);
            binding.hoverView.setOnClickListener(clickListener);

            binding.viewFailed.setTag(R.id.image_view_message, messageEntity);
            binding.viewFailed.setOnClickListener(clickListener);

            UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.contentThumbPath);

            UserEntity entity = getUserName(messageEntity);
            if (entity == null) {
                UIHelper.updateImageNameField(binding.textViewImageName, "", "");
            } else {
                UIHelper.updateImageNameField(binding.textViewImageName, entity.getUserName(), entity.getUserLastName());
            }

        }

        @Override
        protected void clearView() {
            binding.imageViewMessage.invalidate();
        }
    }

    private class ImageMessageOutHolder extends GenericViewHolder {
        private ItemImageMessageOutBinding binding;

        public ImageMessageOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemImageMessageOutBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            MessageEntity messageEntity = (MessageEntity) chatEntity;
            binding.setTextMessage(messageEntity);
            binding.setChatViewModel(chatViewModel);

            outgoingLoadingEffect(binding.circleView, messageEntity);

            binding.viewFailed.setVisibility(View.GONE);
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED) {
                binding.viewFailed.setVisibility(View.VISIBLE);
            } else {
                binding.viewFailed.setVisibility(View.GONE);
            }

            binding.imageViewMessage.setTag(R.id.image_view_message, messageEntity);
            binding.imageViewMessage.setOnClickListener(clickListener);

            binding.hover.setTag(R.id.image_view_message, messageEntity);
            binding.hover.setOnClickListener(clickListener);

            binding.hoverView.setTag(R.id.image_view_message, messageEntity);
            binding.hoverView.setOnClickListener(clickListener);

            binding.viewFailed.setTag(R.id.image_view_message, messageEntity);
            binding.viewFailed.setOnClickListener(clickListener);

            UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.contentThumbPath);
        }

        @Override
        protected void clearView() {
            binding.imageViewMessage.invalidate();
        }
    }

    private class VideoMessageInHolder extends GenericViewHolder {
        private ItemVideoMessageInBinding binding;

        public VideoMessageInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemVideoMessageInBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            MessageEntity messageEntity = (MessageEntity) chatEntity;
            binding.setTextMessage(messageEntity);
            binding.setAvatarIndex(getAvatarIndex(messageEntity));

            incomingShimmerEffect(binding.shimmerIncomingLoading, messageEntity);
            incomingLoadingEffect(binding.circleView, messageEntity);

            binding.viewFailed.setVisibility(View.GONE);
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED
                    || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED) {
                binding.viewFailed.setVisibility(View.VISIBLE);
            } else {
                binding.viewFailed.setVisibility(View.GONE);
            }

            binding.imageViewMessage.setTag(R.id.image_view_message, messageEntity);
            binding.imageViewMessage.setOnClickListener(clickListener);

            binding.hover.setTag(R.id.image_view_message, messageEntity);
            binding.hover.setOnClickListener(clickListener);

            binding.hoverView.setTag(R.id.image_view_message, messageEntity);
            binding.hoverView.setOnClickListener(clickListener);

            binding.viewFailed.setTag(R.id.image_view_message, messageEntity);
            binding.viewFailed.setOnClickListener(clickListener);

            UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.contentThumbPath);

            UserEntity entity = getUserName(messageEntity);

            if (entity == null) {
                UIHelper.updateImageNameField(binding.textViewImageName, "", "");
            } else {
                UIHelper.updateImageNameField(binding.textViewImageName, entity.getUserName(), entity.getUserLastName());
            }
        }

        @Override
        protected void clearView() {
            binding.imageViewMessage.invalidate();
        }
    }

    private class VideoMessageOutHolder extends GenericViewHolder {
        private ItemVideoMessageOutBinding binding;

        public VideoMessageOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemVideoMessageOutBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            MessageEntity messageEntity = (MessageEntity) chatEntity;
            binding.setTextMessage(messageEntity);
            binding.setChatViewModel(chatViewModel);

            outgoingLoadingEffect(binding.circleView, messageEntity);

            binding.viewFailed.setVisibility(View.GONE);
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED) {
                binding.viewFailed.setVisibility(View.VISIBLE);
            } else {
                binding.viewFailed.setVisibility(View.GONE);
            }

            binding.imageViewMessage.setTag(R.id.image_view_message, messageEntity);
            binding.imageViewMessage.setOnClickListener(clickListener);

            binding.hover.setTag(R.id.image_view_message, messageEntity);
            binding.hover.setOnClickListener(clickListener);

            binding.hoverView.setTag(R.id.image_view_message, messageEntity);
            binding.hoverView.setOnClickListener(clickListener);

            binding.viewFailed.setTag(R.id.image_view_message, messageEntity);
            binding.viewFailed.setOnClickListener(clickListener);

            UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.contentThumbPath);
        }

        @Override
        protected void clearView() {
            binding.imageViewMessage.invalidate();
        }
    }

    private void incomingShimmerEffect(ShimmerLayout shimmerLayout, ChatEntity messageEntity) {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();

        int contentStatus = -1;

        if (messageEntity instanceof GroupMessageEntity) {
            contentStatus = ((GroupMessageEntity) messageEntity).getContentStatus();
        } else {
            contentStatus = ((MessageEntity) messageEntity).getContentStatus();
        }

        if (contentStatus == Constants.ContentStatus.CONTENT_STATUS_RECEIVING) {
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmerAnimation();
        } else {
            shimmerLayout.setVisibility(View.GONE);
            shimmerLayout.stopShimmerAnimation();
        }

        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED) {
            shimmerLayout.stopShimmerAnimation();
        }
        shimmerLayout.setTag(R.id.image_view_message, messageEntity);
        shimmerLayout.setOnClickListener(clickListener);
    }

    private void incomingLoadingEffect(CircleProgressView circleProgressView, MessageEntity messageEntity) {
        circleProgressView.setVisibility(View.GONE);

        if (messageEntity.getContentStatus() == Constants.ContentStatus.CONTENT_STATUS_RECEIVING) {
            circleProgressView.setVisibility(View.VISIBLE);
        }

        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED) {
            circleProgressView.setVisibility(View.GONE);
        }

        // During content is receiving then -> content status
        // Constants.MessageStatus.STATUS_CONTENT_RECEIVING
        // Constants.MessageStatus.STATUS_CONTENT_RECEIVED

        // During content is receiving then -> message status
        // Constants.MessageStatus.STATUS_READ
        // Constants.MessageStatus.STATUS_UNREAD
        // Constants.MessageStatus.STATUS_FAILED

        int progress = messageEntity.getContentProgress();

        if (progress == 0) {
            circleProgressView.spin();
        } else {
            if (progress > 0) {
                circleProgressView.setValue(progress);
            }
        }
    }

    private void incomingLoadingEffectForGroup(CircleProgressView circleProgressView, GroupMessageEntity messageEntity) {


        if (messageEntity.getContentStatus() == Constants.ContentStatus.CONTENT_STATUS_RECEIVING) {
            circleProgressView.setVisibility(View.VISIBLE);
        } else {
            circleProgressView.setVisibility(View.GONE);
        }

       /* if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED) {
            circleProgressView.setVisibility(View.GONE);
        }*/

        // During content is receiving then -> content status
        // Constants.MessageStatus.STATUS_CONTENT_RECEIVING
        // Constants.MessageStatus.STATUS_CONTENT_RECEIVED

        // During content is receiving then -> message status
        // Constants.MessageStatus.STATUS_READ
        // Constants.MessageStatus.STATUS_UNREAD
        // Constants.MessageStatus.STATUS_FAILED

        int progress = messageEntity.getContentProgress();

        if (progress == 0) {
            circleProgressView.spin();
        } else {
            if (progress > 0) {
                circleProgressView.setValue(progress);
            }
        }
    }

    private void outgoingLoadingEffect(CircleProgressView circleProgressView, ChatEntity messageEntity) {
        circleProgressView.setVisibility(View.GONE);

        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING_START
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_RESEND_START) {
            circleProgressView.setVisibility(View.VISIBLE);
        }

        // During content send message status ->
        // Constants.MessageStatus.STATUS_SENDING_START
        // Constants.MessageStatus.STATUS_RESEND_START
        // Constants.MessageStatus.STATUS_RECEIVED
        // Constants.MessageStatus.STATUS_FAILED

        int progress = -1;
        if (messageEntity instanceof GroupMessageEntity) {
            progress = ((GroupMessageEntity) messageEntity).getContentProgress();
        } else {
            progress = ((MessageEntity) messageEntity).getContentProgress();
        }


        if (progress == 0) {
            circleProgressView.spin();
        } else {
            if (progress > 0) {
                circleProgressView.setValue(progress);
            }
        }
    }

    private void outgoingLoadingEffectForGroup(CircleProgressView circleProgressView, GroupMessageEntity messageEntity) {
        circleProgressView.setVisibility(View.GONE);

        if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING_START
                || messageEntity.getStatus() == Constants.MessageStatus.STATUS_RESEND_START) {
            circleProgressView.setVisibility(View.VISIBLE);
        }

        // During content send message status ->
        // Constants.MessageStatus.STATUS_SENDING_START
        // Constants.MessageStatus.STATUS_RESEND_START
        // Constants.MessageStatus.STATUS_RECEIVED
        // Constants.MessageStatus.STATUS_FAILED

        int progress = messageEntity.getContentProgress();

        if (progress == 0) {
            circleProgressView.spin();
        } else {
            if (progress > 0) {
                circleProgressView.setValue(progress);
            }
        }
    }


    private class SeparatorViewHolder extends GenericViewHolder {
        private ItemMessageSeparatorBinding binding;

        protected SeparatorViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemMessageSeparatorBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            MessageEntity item = (MessageEntity) chatEntity;
            binding.setSeparatorMessage(item);
        }

        @Override
        protected void clearView() {
            binding.textViewSeprator.invalidate();
        }
    }

    private class GroupInfoViewHolder extends GenericViewHolder {
        private ItemGroupInfoBinding binding;

        protected GroupInfoViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemGroupInfoBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            MessageEntity item = (MessageEntity) chatEntity;
            UserEntity entity = getUserName(item);
            String name = entity == null ? "" : entity.getUserName();
            binding.groupInfoBlock.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(name)) {
                binding.groupInfoBlock.setVisibility(View.VISIBLE);
                binding.groupInfo.setText(name + " " + item.getMessage());

                int resourceId = -1;
                switch (item.getMessageType()) {
                    case Constants.MessageType.GROUP_CREATE:
                        resourceId = R.mipmap.gr_create;
                        break;

                    case Constants.MessageType.GROUP_JOIN:
                        resourceId = R.mipmap.gr_join_color;
                        break;

                    case Constants.MessageType.GROUP_MEMBER_ADD: //Todo May be we can change icon for invite or add
                        resourceId = R.mipmap.gr_invite_color;
                        break;

                    case Constants.MessageType.GROUP_LEAVE:
                        resourceId = R.mipmap.gr_leave_color;
                        break;

                    case Constants.MessageType.GROUP_MEMBER_REMOVE:
                        resourceId = R.mipmap.gr_remove_color;
                        break;

                    case Constants.MessageType.GROUP_RENAMED:
                        resourceId = R.mipmap.gr_edit;
                        break;
                }

                Glide.with(TeleMeshApplication.getContext()).load(resourceId).into(binding.infoIcon);
            }
        }

        @Override
        protected void clearView() {
            binding.groupInfo.invalidate();
        }
    }

    /*************************** GROUP ****************************************/


    private class GroupTextInHolder extends GenericViewHolder {
        private ItemGroupTextMessageInBinding binding;

        public GroupTextInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemGroupTextMessageInBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            GroupMessageEntity item = (GroupMessageEntity) chatEntity;
            binding.setTextMessage(item);
            binding.setAvatarIndex(21);
            ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.white));

            UserEntity entity = getUserName(item);

            if (entity == null) {
                binding.userName.setText("");
                UIHelper.updateImageNameField(binding.textViewImageName, "", "");
            } else {
                String name = entity.getUserName();
                binding.userName.setText("" + name);
                UIHelper.updateImageNameField(binding.textViewImageName, entity.getUserName(), entity.getUserLastName());
            }


        }

        @Override
        protected void clearView() {
            binding.textViewMessage.invalidate();
        }
    }

    private class GroupTextOutHolder extends GenericViewHolder {
        private ItemGroupTextMessageOutBinding binding;

        public GroupTextOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemGroupTextMessageOutBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            GroupMessageEntity item = (GroupMessageEntity) chatEntity;
            binding.setTextMessage(item);
            //binding.setAvatarIndex(21);
            ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.white));
            //String name = getUserName(item);
            //binding.userName.setText("" + name);
        }

        @Override
        protected void clearView() {
            binding.textViewMessage.invalidate();
        }
    }

    private class GroupImageInHolder extends GenericViewHolder {
        private ItemGroupImageInBinding binding;

        public GroupImageInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemGroupImageInBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            GroupMessageEntity messageEntity = (GroupMessageEntity) chatEntity;
            //Log.v("FILE_SPEED_TEST_14 ", Calendar.getInstance().getTime() + "");

            binding.setTextMessage(messageEntity);
            binding.setAvatarIndex(getAvatarIndex(messageEntity));

            incomingShimmerEffect(binding.shimmerIncomingLoading, messageEntity);
            incomingLoadingEffectForGroup(binding.circleView, messageEntity);

            binding.viewFailed.setVisibility(View.GONE);
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED
                    || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED) {
                binding.viewFailed.setVisibility(View.VISIBLE);
            } else {
                binding.viewFailed.setVisibility(View.GONE);
            }

            binding.imageViewMessage.setTag(R.id.image_view_message, messageEntity);
            binding.imageViewMessage.setOnClickListener(clickListener);

            binding.hover.setTag(R.id.image_view_message, messageEntity);
            binding.hover.setOnClickListener(clickListener);

            binding.hoverView.setTag(R.id.image_view_message, messageEntity);
            binding.hoverView.setOnClickListener(clickListener);

            binding.viewFailed.setTag(R.id.image_view_message, messageEntity);
            binding.viewFailed.setOnClickListener(clickListener);
            if (TextUtils.isEmpty(messageEntity.contentThumb)) {
                //binding.imageViewMessage
            } else {
                UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.contentThumb);
            }
        }

        @Override
        protected void clearView() {
            binding.imageViewMessage.invalidate();
        }
    }

    private class GroupImageOutHolder extends GenericViewHolder {
        private ItemGroupImageOutBinding binding;

        public GroupImageOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemGroupImageOutBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            GroupMessageEntity messageEntity = (GroupMessageEntity) chatEntity;
            binding.setTextMessage(messageEntity);
            binding.setChatViewModel(chatViewModel);

            outgoingLoadingEffectForGroup(binding.circleView, messageEntity);

            binding.viewFailed.setVisibility(View.GONE);
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED) {
                binding.viewFailed.setVisibility(View.VISIBLE);
            } else {
                binding.viewFailed.setVisibility(View.GONE);
            }

            binding.imageViewMessage.setTag(R.id.image_view_message, messageEntity);
            binding.imageViewMessage.setOnClickListener(clickListener);

            binding.hover.setTag(R.id.image_view_message, messageEntity);
            binding.hover.setOnClickListener(clickListener);

            binding.hoverView.setTag(R.id.image_view_message, messageEntity);
            binding.hoverView.setOnClickListener(clickListener);

            binding.viewFailed.setTag(R.id.image_view_message, messageEntity);
            binding.viewFailed.setOnClickListener(clickListener);
            if (TextUtils.isEmpty(messageEntity.contentThumb)) {

            } else {
                UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.contentThumb);
            }
        }

        @Override
        protected void clearView() {
            binding.imageViewMessage.invalidate();
        }
    }

    private class GroupVideoInHolder extends GenericViewHolder {
        private ItemGroupVideoInBinding binding;

        public GroupVideoInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemGroupVideoInBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            GroupMessageEntity messageEntity = (GroupMessageEntity) chatEntity;
            binding.setTextMessage(messageEntity);
            binding.setAvatarIndex(getAvatarIndex(messageEntity));

            incomingShimmerEffect(binding.shimmerIncomingLoading, messageEntity);
            incomingLoadingEffectForGroup(binding.circleView, messageEntity);

            binding.viewFailed.setVisibility(View.GONE);
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED
                    || messageEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD_FAILED) {
                binding.viewFailed.setVisibility(View.VISIBLE);
            } else {
                binding.viewFailed.setVisibility(View.GONE);
            }

            binding.imageViewMessage.setTag(R.id.image_view_message, messageEntity);
            binding.imageViewMessage.setOnClickListener(clickListener);

            binding.hover.setTag(R.id.image_view_message, messageEntity);
            binding.hover.setOnClickListener(clickListener);

            binding.hoverView.setTag(R.id.image_view_message, messageEntity);
            binding.hoverView.setOnClickListener(clickListener);

            binding.viewFailed.setTag(R.id.image_view_message, messageEntity);
            binding.viewFailed.setOnClickListener(clickListener);
            if (!TextUtils.isEmpty(messageEntity.contentThumb)) {
                UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.contentThumb);
            }
        }

        @Override
        protected void clearView() {
            binding.imageViewMessage.invalidate();
        }
    }

    private class GroupVideoOutHolder extends GenericViewHolder {
        private ItemGroupVideoOutBinding binding;

        public GroupVideoOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemGroupVideoOutBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull ChatEntity chatEntity) {
            GroupMessageEntity messageEntity = (GroupMessageEntity) chatEntity;
            binding.setTextMessage(messageEntity);
            binding.setChatViewModel(chatViewModel);

            outgoingLoadingEffect(binding.circleView, messageEntity);

            binding.viewFailed.setVisibility(View.GONE);
            if (messageEntity.getStatus() == Constants.MessageStatus.STATUS_FAILED) {
                binding.viewFailed.setVisibility(View.VISIBLE);
            } else {
                binding.viewFailed.setVisibility(View.GONE);
            }

            binding.imageViewMessage.setTag(R.id.image_view_message, messageEntity);
            binding.imageViewMessage.setOnClickListener(clickListener);

            binding.hover.setTag(R.id.image_view_message, messageEntity);
            binding.hover.setOnClickListener(clickListener);

            binding.hoverView.setTag(R.id.image_view_message, messageEntity);
            binding.hoverView.setOnClickListener(clickListener);

            binding.viewFailed.setTag(R.id.image_view_message, messageEntity);
            binding.viewFailed.setOnClickListener(clickListener);

            UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.getContentThumb());
        }

        @Override
        protected void clearView() {
            binding.imageViewMessage.invalidate();
        }
    }
}
