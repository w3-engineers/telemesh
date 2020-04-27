package com.w3engineers.unicef.telemesh.ui.chat;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemImageMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemImageMessageOutBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemMessageSeparatorBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageOutBinding;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import static com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity.DIFF_CALLBACK;

public class ChatPagedAdapterRevised extends PagedListAdapter<ChatEntity, ChatPagedAdapterRevised.GenericViewHolder> {

    private int avatarIndex;

    @NonNull
    public Context mContext;
    @Nullable
    protected ChatViewModel chatViewModel;


    public ChatPagedAdapterRevised(@NonNull Context context, @Nullable ChatViewModel chatViewModel) {
        super(DIFF_CALLBACK);
        mContext = context;
        this.chatViewModel = chatViewModel;
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();

    }


    @Override
    public int getItemViewType(int position) {
        ChatEntity chatEntity = getItem(position);

        if (chatEntity != null && chatEntity.getMessageType() != Constants.MessageType.DATE_MESSAGE) {

            if (chatEntity.isIncoming()) {
                switch (chatEntity.getMessageType()) {
                    case Constants.MessageType.TEXT_MESSAGE:
                        return Constants.ViewHolderType.TEXT_INCOMING;
                    case Constants.MessageType.IMAGE_MESSAGE:
                        return Constants.ViewHolderType.IMG_INCOMING;
                }
            } else {
                switch (chatEntity.getMessageType()) {
                    case Constants.MessageType.TEXT_MESSAGE:
                        return Constants.ViewHolderType.TEXT_OUTGOING;
                    case Constants.MessageType.IMAGE_MESSAGE:
                        return Constants.ViewHolderType.IMG_OUTGOING;
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
        } else {
            ItemMessageSeparatorBinding itemMessageSeparatorBinding = ItemMessageSeparatorBinding.inflate(inflater, viewGroup, false);
            return new SeparatorViewHolder(itemMessageSeparatorBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder baseViewHolder, int position) {

        MessageEntity messageEntity = (MessageEntity) getItem(position);
        if (messageEntity != null) {
            baseViewHolder.bindView(messageEntity);
        } else {
            baseViewHolder.clearView();
        }

    }

    void addAvatarIndex(int index) {
        this.avatarIndex = index;
        notifyDataSetChanged();
    }

    public abstract class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bindView(@NonNull MessageEntity item);

        protected abstract void clearView();
    }


    private class TextMessageInHolder extends GenericViewHolder {
        private ItemTextMessageInBinding binding;

        public TextMessageInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemTextMessageInBinding) viewDataBinding;
            ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.white));

            binding.imageProfile.setImageResource(TeleMeshDataHelper.getInstance().getAvatarImage(avatarIndex));

        }

        @Override
        protected void bindView(@NonNull MessageEntity item) {
            binding.setTextMessage(item);
        }

        @Override
        protected void clearView() { binding.textViewMessage.invalidate(); }
    }

    private class TextMessageOutHolder extends GenericViewHolder {
        private ItemTextMessageOutBinding binding;

        public TextMessageOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemTextMessageOutBinding) viewDataBinding;
           /* ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.white));*/
        }

        @Override
        protected void bindView(@NonNull MessageEntity item) {
            binding.setTextMessage(item);
            binding.setChatViewModel(chatViewModel);
        }

        @Override
        protected void clearView() { binding.textViewMessage.invalidate(); }
    }

    private class ImageMessageInHolder extends GenericViewHolder {
        private ItemImageMessageInBinding binding;

        public ImageMessageInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemImageMessageInBinding) viewDataBinding;
            binding.imageProfile.setImageResource(TeleMeshDataHelper.getInstance().getAvatarImage(avatarIndex));
        }

        @Override
        protected void bindView(@NonNull MessageEntity messageEntity) {
            binding.setTextMessage(messageEntity);

            binding.shimmerUploadingImage.shimmerLoadingCellContent.setVisibility(View.GONE);

            if (messageEntity.getContentStatus() == Constants.MessageStatus.STATUS_CONTENT_RECEIVING) {
                binding.shimmerUploadingImage.shimmerLoadingCellContent.setVisibility(View.VISIBLE);
                binding.shimmerUploadingImage.shimmerLoadingCellContent.startShimmerAnimation();
            } else {
                binding.shimmerUploadingImage.shimmerLoadingCellContent.setVisibility(View.GONE);
                binding.shimmerUploadingImage.shimmerLoadingCellContent.stopShimmerAnimation();
            }
            UIHelper.setImageInGlide(binding.imageViewMessage, messageEntity.contentThumbPath);
        }

        @Override
        protected void clearView() { binding.imageViewMessage.invalidate(); }
    }

    private class ImageMessageOutHolder extends GenericViewHolder {
        private ItemImageMessageOutBinding binding;

        public ImageMessageOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemImageMessageOutBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull MessageEntity item) {
            binding.setTextMessage(item);
            binding.setChatViewModel(chatViewModel);
            UIHelper.setImageInGlide(binding.imageViewMessage, item.contentThumbPath);
        }

        @Override
        protected void clearView() { binding.imageViewMessage.invalidate(); }
    }

    private class SeparatorViewHolder extends GenericViewHolder {
        private ItemMessageSeparatorBinding binding;

        protected SeparatorViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemMessageSeparatorBinding) viewDataBinding;
        }

        @Override
        protected void bindView(@NonNull MessageEntity item) {
            binding.setSeparatorMessage(item);
        }

        @Override
        protected void clearView() { binding.textViewSeprator.invalidate(); }
    }
}
