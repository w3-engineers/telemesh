package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.databinding.BindingAdapter;
import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.ext.strom.application.ui.base.BaseViewHolder;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemMeshContactBinding;

import java.util.List;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [04-Oct-2018 at 6:04 PM].
 * * Email: mimosaha@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [04-Oct-2018 at 6:04 PM].
 * * --> <Second Editor> on [04-Oct-2018 at 6:04 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [04-Oct-2018 at 6:04 PM].
 * * --> <Second Reviewer> on [04-Oct-2018 at 6:04 PM].
 * * ============================================================================
 **/
public class MeshContactAdapter extends BaseAdapter<UserEntity> {

    private MeshContactViewModel meshContactViewModel;

    MeshContactAdapter(MeshContactViewModel meshContactViewModel) {
        this.meshContactViewModel = meshContactViewModel;
    }

    @Override
    public boolean isEqual(UserEntity left, UserEntity right) {
        String leftUserId = left.getMeshId();
        String rightUserId = right.getMeshId();


        return !TextUtils.isEmpty(leftUserId)
                && !TextUtils.isEmpty(rightUserId)
                && leftUserId.equals(rightUserId);

    }

    @Override
    public BaseViewHolder<UserEntity> newViewHolder(ViewGroup parent, int viewType) {
        return new MeshContactViewHolder(inflate(parent, R.layout.item_mesh_contact));
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int imageResourceId) {
        imageView.setImageResource(imageResourceId);
    }

    public void resetWithList(List<UserEntity> items) {

        List<UserEntity> userEntities = getItems();
        userEntities.clear();
        notifyDataSetChanged();
        addItem(items);
    }

    private class MeshContactViewHolder extends BaseViewHolder<UserEntity> {
        private ItemMeshContactBinding itemMeshContactBinding;

        MeshContactViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            this.itemMeshContactBinding = (ItemMeshContactBinding) viewDataBinding;
        }

        @Override
        public void bind(UserEntity item) {
            itemMeshContactBinding.setUser(item);
            itemMeshContactBinding.setContactViewModel(meshContactViewModel);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
