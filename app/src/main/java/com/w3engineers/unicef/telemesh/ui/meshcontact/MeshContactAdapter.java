package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.paging.PagedListAdapter;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemMeshContactBinding;



/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MeshContactAdapter extends PagedListAdapter<UserEntity, MeshContactAdapter.GenericViewHolder> {

    @NonNull
    public MeshContactViewModel meshContactViewModel;

    MeshContactAdapter(@NonNull MeshContactViewModel meshContactViewModel) {
        super(DIFF_CALLBACK);
        this.meshContactViewModel = meshContactViewModel;
    }

    public static final DiffUtil.ItemCallback<UserEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UserEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
                    return oldItem.getMeshId().equalsIgnoreCase(newItem.getMeshId()) ;
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
    public MeshContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //   LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //  ItemMeshContactBinding itemMeshContactBinding = ItemMeshContactBinding.inflate(inflater, viewGroup, false);

        return new MeshContactViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_mesh_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder baseViewHolder, int position) {
        UserEntity userEntity = getItem(position);
        if (userEntity != null) {
            baseViewHolder.bindView(userEntity);
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            baseViewHolder.clearView();
        }
    }

    public abstract class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bindView(@NonNull UserEntity item);

        protected abstract void clearView();
    }

    private class MeshContactViewHolder extends GenericViewHolder {
        private ItemMeshContactBinding itemMeshContactBinding;

        MeshContactViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.itemMeshContactBinding = (ItemMeshContactBinding) viewDataBinding;
        }


        @Override
        protected void bindView(@NonNull UserEntity item) {
            itemMeshContactBinding.userMeshStatus.setBackgroundResource(activeStatusResource(item.getOnlineStatus()));
            itemMeshContactBinding.userName.setText(item.userName + getHopIndicator(item.getOnlineStatus()));

            itemMeshContactBinding.textViewUnreadMessageCount.setVisibility(View.GONE);

            if (item.hasUnreadMessage > 0) {
                itemMeshContactBinding.textViewUnreadMessageCount.setVisibility(View.VISIBLE);
            } else {
                itemMeshContactBinding.textViewUnreadMessageCount.setVisibility(View.GONE);
            }

            itemMeshContactBinding.setUser(item);
            itemMeshContactBinding.setContactViewModel(meshContactViewModel);
        }

        @Override
        protected void clearView() {
            itemMeshContactBinding.invalidateAll();
        }


        private String getHopIndicator(int userActiveStatus) {
            if (userActiveStatus == Constants.UserStatus.BLE_MESH_ONLINE
                    || userActiveStatus == Constants.UserStatus.WIFI_MESH_ONLINE)
                return "";
            else
                return "";
        }

        private int activeStatusResource(int userActiveStatus) {

            if (userActiveStatus == Constants.UserStatus.WIFI_ONLINE || userActiveStatus == Constants.UserStatus.WIFI_MESH_ONLINE) {
                return R.mipmap.ic_mesh_online;
            } else if (userActiveStatus == Constants.UserStatus.BLE_MESH_ONLINE || userActiveStatus == Constants.UserStatus.BLE_ONLINE) {
                return R.mipmap.ic_mesh_online;
            } else if (userActiveStatus == Constants.UserStatus.INTERNET_ONLINE) {
                return R.mipmap.ic_internet;
            } else {
                return R.mipmap.ic_offline;
            }

            /*if (userActiveStatus == Constants.UserStatus.WIFI_ONLINE || userActiveStatus == Constants.UserStatus.BLE_ONLINE) {
                return R.drawable.circle_online;
            } else if (userActiveStatus == Constants.UserStatus.INTERNET_ONLINE) {
                return R.drawable.circle_internet;
            } else {
                return R.drawable.circle_offline;
            }*/
        }


    }
}