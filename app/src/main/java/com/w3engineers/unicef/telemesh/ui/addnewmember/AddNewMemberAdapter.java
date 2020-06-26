package com.w3engineers.unicef.telemesh.ui.addnewmember;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupCreateUserBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddNewMemberAdapter extends PagedListAdapter<UserEntity, AddNewMemberAdapter.GenericViewHolder> {

    private List<UserEntity> selectedUserList = new ArrayList<>();
    private Context mContext;
    private AddNewMemberAdapter.ItemChangeListener mListener;

    AddNewMemberAdapter(Context context, AddNewMemberAdapter.ItemChangeListener listener) {
        super(DIFF_CALLBACK);
        this.mContext = context;
        this.mListener = listener;
    }

    public static final DiffUtil.ItemCallback<UserEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UserEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
                    return oldItem.getMeshId().equalsIgnoreCase(newItem.getMeshId());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldItem.equals(newItem);
                }
            };


    @BindingAdapter({"android:src"})
    public static void setImageViewResource(@NonNull ImageView imageView, int imageResourceId) {
        imageView.setImageResource(imageResourceId);
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public AddNewMemberAdapter.GenericViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //   LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //  ItemMeshContactBinding itemMeshContactBinding = ItemMeshContactBinding.inflate(inflater, viewGroup, false);

        return new AddNewMemberViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_group_create_user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddNewMemberAdapter.GenericViewHolder baseViewHolder, int position) {
        UserEntity userEntity = getItem(position);
        if (userEntity != null) {
            baseViewHolder.bindView(userEntity, position);
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            baseViewHolder.clearView();
        }
    }

    List<UserEntity> getSelectedUserList() {
        return selectedUserList;
    }

    void deselectUser(UserEntity deselectedUser) {
        selectedUserList.remove(deselectedUser);
        notifyDataSetChanged();

        //Todo we have to optimize notify management here
    }


    public abstract class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bindView(@NonNull UserEntity item, int position);

        protected abstract void clearView();

    }

    private class AddNewMemberViewHolder extends AddNewMemberAdapter.GenericViewHolder {
        private ItemGroupCreateUserBinding itemGroupCreateUserBinding;

        AddNewMemberViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.itemGroupCreateUserBinding = (ItemGroupCreateUserBinding) viewDataBinding;
        }


        @Override
        protected void bindView(@NonNull UserEntity item, int position) {
            itemGroupCreateUserBinding.userMeshStatus.setBackgroundResource(activeStatusResource(item.getOnlineStatus()));
            itemGroupCreateUserBinding.userName.setText(item.userName);

            itemGroupCreateUserBinding.userAvatar.setImageResource(TeleMeshDataHelper.getInstance()
                    .getAvatarImage(item.avatarIndex));

            itemGroupCreateUserBinding.checkBox.setVisibility(View.VISIBLE);

            if (isSelected(item.meshId)) {
                itemGroupCreateUserBinding.mainItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.selected_item_bg));
                itemGroupCreateUserBinding.checkBox.setChecked(true, false);
            } else {
                itemGroupCreateUserBinding.mainItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                itemGroupCreateUserBinding.checkBox.setChecked(false, false);
            }

            // Todo We have to replace the listener in separate section
            itemGroupCreateUserBinding.getRoot().setOnClickListener(view -> {

                if (isSelected(item.getMeshId())) {
                    removeSelectedUser(item.getMeshId());
                    itemGroupCreateUserBinding.mainItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    itemGroupCreateUserBinding.checkBox.setChecked(false, true);
                } else {
                    selectedUserList.add(item);
                    itemGroupCreateUserBinding.mainItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.selected_item_bg));
                    itemGroupCreateUserBinding.checkBox.setChecked(true, true);
                }

                if (mListener != null) {
                    mListener.onGetChangedItem(itemGroupCreateUserBinding.checkBox.isChecked(), item);
                }
            });

        }

        @Override
        protected void clearView() {
            itemGroupCreateUserBinding.invalidateAll();
        }

        private int activeStatusResource(int userActiveStatus) {

            if (userActiveStatus == Constants.UserStatus.WIFI_ONLINE
                    || userActiveStatus == Constants.UserStatus.WIFI_MESH_ONLINE
                    || userActiveStatus == Constants.UserStatus.BLE_MESH_ONLINE
                    || userActiveStatus == Constants.UserStatus.BLE_ONLINE) {
                return R.mipmap.ic_mesh_online;
            } else if (userActiveStatus == Constants.UserStatus.HB_ONLINE || userActiveStatus == Constants.UserStatus.HB_MESH_ONLINE) {
                return R.mipmap.ic_hb_online;
            } else if (userActiveStatus == Constants.UserStatus.INTERNET_ONLINE) {
                return R.mipmap.ic_internet;
            } else {
                return R.mipmap.ic_offline;
            }
        }

        //Todo we can optimize this section
        private boolean isSelected(String userId) {
            for (UserEntity entity : selectedUserList) {
                if (entity.getMeshId() != null && entity.getMeshId().equals(userId)) {
                    return true;
                }
            }
            return false;
        }

        private void removeSelectedUser(String userId) {
            UserEntity removedUser = null;
            for (UserEntity entity : selectedUserList) {
                if (entity.getMeshId() != null && entity.getMeshId().equals(userId)) {
                    removedUser = entity;
                    break;
                }
            }
            if (removedUser != null) {
                selectedUserList.remove(removedUser);
            }
        }

    }

    interface ItemChangeListener {
        void onGetChangedItem(boolean isAdd, UserEntity userEntity);
    }


}