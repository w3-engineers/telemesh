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
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemMessageSeparatorBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageInBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemTextMessageOutBinding;

import static com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity.DIFF_CALLBACK;

public class ChatPagedAdapterRevised extends PagedListAdapter<ChatEntity, ChatPagedAdapterRevised.GenericViewHolder> {

    private final int TEXT_MESSAGE_IN = 0;
    private final int TEXT_MESSAGE_OUT = 1;

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

        if (chatEntity != null && chatEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
            return chatEntity.isIncoming ? TEXT_MESSAGE_IN : TEXT_MESSAGE_OUT;
        }

        return -1;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == TEXT_MESSAGE_IN) {
            ItemTextMessageInBinding itemTextMessageInBinding = ItemTextMessageInBinding.inflate(inflater, viewGroup, false);
            return new TextMessageInHolder(itemTextMessageInBinding);
        } else if (viewType == TEXT_MESSAGE_OUT) {
            ItemTextMessageOutBinding itemTextMessageOutBinding = ItemTextMessageOutBinding.inflate(inflater, viewGroup, false);
            return new TextMessageOutHolder(itemTextMessageOutBinding);
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
                    ContextCompat.getColor(mContext, R.color.incoming_message_color));
        }

        @Override
        protected void bindView(@NonNull MessageEntity item) {
            binding.setTextMessage(item);
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
            ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.out_coming_message_color));
        }

        @Override
        protected void bindView(@NonNull MessageEntity item) {
            binding.setTextMessage(item);
            binding.setChatViewModel(chatViewModel);
        }

        @Override
        protected void clearView() {
            binding.textViewMessage.invalidate();
        }
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
        protected void clearView() {
            binding.textViewSeprator.invalidate();
        }
    }


}
