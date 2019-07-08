package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.databinding.BindingAdapter;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemMeshContactBinding;

import java.util.List;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MeshContactAdapter extends BaseAdapter<UserEntity> {

    @NonNull
    public MeshContactViewModel meshContactViewModel;

    MeshContactAdapter(@NonNull MeshContactViewModel meshContactViewModel) {
        this.meshContactViewModel = meshContactViewModel;
    }

    @Override
    public boolean isEqual(@NonNull UserEntity left, @NonNull UserEntity right) {
        String leftUserId = left.getMeshId();
        String rightUserId = right.getMeshId();


        return !TextUtils.isEmpty(leftUserId) && leftUserId != null
                && !TextUtils.isEmpty(rightUserId)
                && leftUserId.equals(rightUserId);

    }

    @NonNull
    @Override
    public BaseAdapterViewHolder<UserEntity> newViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MeshContactViewHolder(inflate(parent, R.layout.item_mesh_contact));
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(@NonNull ImageView imageView, int imageResourceId) {
        imageView.setImageResource(imageResourceId);
    }

    public void resetWithList(@NonNull List<UserEntity> items) {

        List<UserEntity> userEntities = getItems();
        userEntities.clear();
        notifyDataSetChanged();
        addItem(items);
    }

    private class MeshContactViewHolder extends BaseAdapterViewHolder<UserEntity> {
        private ItemMeshContactBinding itemMeshContactBinding;

        MeshContactViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            this.itemMeshContactBinding = (ItemMeshContactBinding) viewDataBinding;
        }

        @Override
        public void bind(@NonNull UserEntity item) {

            itemMeshContactBinding.userMeshStatus.setBackgroundResource(item.isOnline() ?
                    R.drawable.circle_online : R.drawable.circle_offline);

            itemMeshContactBinding.setUser(item);
            itemMeshContactBinding.setContactViewModel(meshContactViewModel);
        }
    }
}
