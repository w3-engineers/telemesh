package com.w3engineers.unicef.telemesh.ui.chat;


import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemMessageSeparatorBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageOutBinding;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/11/2018 at 10:43 AM.
 *  *
 *  * Purpose: Render message data
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/11/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */
public class ChatAdapter extends BaseAdapter<ChatEntity> {
    /**
     * <h1>Instance variable scope</h1>
     */
    private Context mContext;
    private final int TEXT_MESSAGE_IN = 0;
    private final int TEXT_MESSAGE_OUT = 1;
    private final int MESSAGE_SEPARATOR = 2;

    /**
     * <p>Constructor</p>
     *
     * @param context
     */
    public ChatAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        ChatEntity item = getItem(position);
        if (item.getMessageType() == Constants.MessageType.DATE_MESSAGE) {
            return MESSAGE_SEPARATOR;
        } else if (item.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
            return item.isIncoming ? TEXT_MESSAGE_IN : TEXT_MESSAGE_OUT;
        }
        return -1;
    }

    @Override
    public boolean isEqual(ChatEntity left, ChatEntity right) {
        return !(TextUtils.isEmpty(left.getMessageId())
                || TextUtils.isEmpty(right.getMessageId()))
                && left.getMessageId().equals(right.getMessageId());
    }

    @Override
    public BaseAdapterViewHolder<ChatEntity> newViewHolder(ViewGroup parent, int viewType) {
        BaseAdapterViewHolder baseViewHolder = null;
        switch (viewType) {
            case TEXT_MESSAGE_IN:
                baseViewHolder = new TextMessageInHolder(inflate(parent, R.layout.item_text_message_in));
                break;
            case TEXT_MESSAGE_OUT:
                baseViewHolder = new TextMessageOutHolder(inflate(parent, R.layout.item_text_message_out));
                break;
            case MESSAGE_SEPARATOR:
                baseViewHolder = new SeparatorViewHolder(inflate(parent, R.layout.item_message_separator));
                break;
        }
        return baseViewHolder;
    }

    private class TextMessageInHolder extends BaseAdapterViewHolder<MessageEntity> {
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

    }

    private class TextMessageOutHolder extends BaseAdapterViewHolder<MessageEntity> {
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
    }

    private class SeparatorViewHolder extends BaseAdapterViewHolder<MessageEntity> {
        private ItemMessageSeparatorBinding binding;

        public SeparatorViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            binding = (ItemMessageSeparatorBinding) viewDataBinding;
        }

        @Override
        public void bind(MessageEntity item) {
            binding.setSeparatorMessage(item);
        }
    }

}
