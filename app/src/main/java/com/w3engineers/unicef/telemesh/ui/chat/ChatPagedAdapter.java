package com.w3engineers.unicef.telemesh.ui.chat;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.ext.strom.application.ui.base.BaseViewHolder;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemMessageSeparatorBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageOutBinding;

public class ChatPagedAdapter extends PagedListAdapter<ChatEntity, BaseViewHolder> {

    private final int TEXT_MESSAGE_IN = 0;
    private final int TEXT_MESSAGE_OUT = 1;
    private final int MESSAGE_SEPARATOR = 2;
    private Context mContext;

    private final int NO_ITEM = 0;
    private final int HAS_ITEM = 1;


    private static final DiffUtil.ItemCallback<ChatEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChatEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ChatEntity oldItem, @NonNull ChatEntity newItem) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldItem.messageId == newItem.messageId;
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull ChatEntity oldItem, @NonNull ChatEntity newItem) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldItem.equals(newItem);
                }
            };

    public ChatPagedAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();

    }


    @Override
    public int getItemViewType(int position) {
        ChatEntity chatEntity = getItem(position);
        if (chatEntity.getMessageType() == Constants.MessageType.DATE_MESSAGE) {
            return MESSAGE_SEPARATOR;
        } else if (chatEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
            return chatEntity.isIncoming ? TEXT_MESSAGE_IN : TEXT_MESSAGE_OUT;
        }
        return -1;
    }



    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        BaseViewHolder baseViewHolder = null;
        switch (viewType) {
            case TEXT_MESSAGE_IN:
                ViewDataBinding view_in = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_text_message_in, viewGroup, false);
                baseViewHolder = new TextMessageInHolder(view_in);
                break;
            case TEXT_MESSAGE_OUT:
                ViewDataBinding view_out = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_text_message_out, viewGroup, false);
                baseViewHolder = new TextMessageOutHolder(view_out);
                break;
            case MESSAGE_SEPARATOR:
                ViewDataBinding view_date_separator = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_message_separator, viewGroup, false);
                baseViewHolder = new SeparatorViewHolder(view_date_separator);
                break;
        }
        return baseViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int position) {

        ChatEntity chatEntity = getItem(position);
        baseViewHolder.bind(chatEntity);
    }


    private class TextMessageInHolder extends BaseViewHolder<MessageEntity> {
        private ItemTextMessageInBinding binding;

        public TextMessageInHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            binding = (ItemTextMessageInBinding) viewDataBinding;
            ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.incoming_message_color));
        }

        @Override
        public void bind(MessageEntity item) {
            binding.setTextMessage(item);
        }



        @Override
        public void onClick(View view) {

        }
    }

    private class TextMessageOutHolder extends BaseViewHolder<MessageEntity> {
        private ItemTextMessageOutBinding binding;

        public TextMessageOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            binding = (ItemTextMessageOutBinding) viewDataBinding;
            ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.outcoming_message_color));
        }

        @Override
        public void bind(MessageEntity item) {
            binding.setTextMessage(item);
        }

        @Override
        public void onClick(View view) {

        }
    }

    private class SeparatorViewHolder extends BaseViewHolder<MessageEntity> {
        private ItemMessageSeparatorBinding binding;

        public SeparatorViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            binding = (ItemMessageSeparatorBinding) viewDataBinding;
        }

        @Override
        public void bind(MessageEntity item) {
            binding.setSeparatorMessage(item);
        }

        @Override
        public void onClick(View view) {

        }
    }


}
