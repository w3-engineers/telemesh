package com.w3engineers.unicef.telemesh.ui.conversations;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemConversationBinding;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactViewModel;

public class ConversationAdapter extends PagedListAdapter<GroupEntity, ConversationAdapter.GenericViewHolder> {

    @NonNull
    public MeshContactViewModel meshContactViewModel;

    ConversationAdapter(@NonNull MeshContactViewModel discoverViewModel) {
        super(DIFF_CALLBACK);
        this.meshContactViewModel = discoverViewModel;
    }

    public static final DiffUtil.ItemCallback<GroupEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<GroupEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull GroupEntity oldItem, @NonNull GroupEntity newItem) {
                    return oldItem.getGroupId().equalsIgnoreCase(newItem.getGroupId());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull GroupEntity oldItem, @NonNull GroupEntity newItem) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldItem.equals(newItem);
                }
            };


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ConversationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_conversation, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder baseViewHolder, int position) {
        GroupEntity userEntity = getItem(position);
        if (userEntity != null) {
            baseViewHolder.bindView(userEntity);
        } else {
            baseViewHolder.clearView();
        }
    }

    public abstract class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bindView(@NonNull GroupEntity item);

        protected abstract void clearView();
    }

    private class ConversationViewHolder extends GenericViewHolder {
        private ItemConversationBinding itemConversationBinding;

        ConversationViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.itemConversationBinding = (ItemConversationBinding) viewDataBinding;
        }


        @Override
        protected void bindView(@NonNull GroupEntity groupEntity) {
            itemConversationBinding.setGroupItem(groupEntity);
            itemConversationBinding.setViewModel(meshContactViewModel);
        }

        @Override
        protected void clearView() {
            itemConversationBinding.invalidateAll();
        }
    }
}