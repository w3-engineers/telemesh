/*
package com.w3engineers.unicef.telemesh.ui.chat;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
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

public class ChatPagedAdapter extends PagedListAdapter<ChatEntity, ChatPagedAdapter.GenericViewHolder> {

    private final int TEXT_MESSAGE_IN = 0;
    private final int TEXT_MESSAGE_OUT = 1;
    private final int MESSAGE_SEPARATOR = 2;
    private final int MESSAGE_PREVIEW_NULL = 3;

    private Context mContext;


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

        if(chatEntity!= null){

            if (chatEntity.getMessageType() == Constants.MessageType.DATE_MESSAGE) {
                return MESSAGE_SEPARATOR;
            } else if (chatEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
                return chatEntity.isIncoming ? TEXT_MESSAGE_IN : TEXT_MESSAGE_OUT;
            }
        }



        return MESSAGE_PREVIEW_NULL;
    }




    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        GenericViewHolder baseViewHolder = null;
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
            case MESSAGE_PREVIEW_NULL:
                ViewDataBinding view_preview_null = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_message_preview_null, viewGroup, false);
                baseViewHolder = new PreviewNullHolder(view_preview_null);
                break;

            default:
                break;

        }

        return baseViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder baseViewHolder, int position) {


            MessageEntity messageEntity = (MessageEntity) getItem(position);
            if(messageEntity != null){
                baseViewHolder.bindView(messageEntity);
            } else {
                baseViewHolder.clearView();
            }


    }




    public abstract class GenericViewHolder extends RecyclerView.ViewHolder
    {
        public GenericViewHolder(View itemView) {
            super(itemView);
        }

        public abstract  void bindView(MessageEntity item);
         public abstract void clearView();
    }


    private class PreviewNullHolder extends GenericViewHolder{

        public PreviewNullHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
        }

        @Override
        public void bindView(MessageEntity item) {

        }

        @Override
        public void clearView() {

        }
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
        public void bindView(MessageEntity item) {
            binding.setTextMessage(item);
        }

        @Override
        public void clearView() {
            binding.invalidateAll();
        }
    }

    private class TextMessageOutHolder extends GenericViewHolder {
        private ItemTextMessageOutBinding binding;

        public TextMessageOutHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemTextMessageOutBinding) viewDataBinding;
            ((GradientDrawable) binding.textViewMessage.getBackground()).setColor(
                    ContextCompat.getColor(mContext, R.color.outcoming_message_color));
        }

        @Override
        public void bindView(MessageEntity item) {
            binding.setTextMessage(item);
        }

        @Override
        public void clearView() {
            binding.invalidateAll();
        }
    }

    private class SeparatorViewHolder extends GenericViewHolder {
        private ItemMessageSeparatorBinding binding;

        public SeparatorViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            binding = (ItemMessageSeparatorBinding) viewDataBinding;
        }

        @Override
        public void bindView(MessageEntity item) {
            binding.setSeparatorMessage(item);
        }

        @Override
        public void clearView() {
            binding.invalidateAll();
        }
    }


}
*/
