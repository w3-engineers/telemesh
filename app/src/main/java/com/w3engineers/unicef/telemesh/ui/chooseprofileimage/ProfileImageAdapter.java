package com.w3engineers.unicef.telemesh.ui.chooseprofileimage;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.databinding.ItemProfileImageBinding;
import com.w3engineers.unicef.util.lib.circle_checkbox.SmoothCheckBox;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
class ProfileImageAdapter extends BaseAdapter<Integer> {

    @Nullable
    public SmoothCheckBox previousSelectedItem;
    public int selectedPosition;


    public ProfileImageAdapter(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public boolean isEqual(@NonNull Integer left, @NonNull Integer right) {
        return left.equals(right);
    }

    @NonNull
    @Override
    public Integer getItem(int position) {
        return position;
    }

    @NonNull
    @Override
    public BaseAdapterViewHolder<Integer> newViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileImageHolder(inflate(parent, R.layout.item_profile_image));
    }


    private class ProfileImageHolder extends BaseAdapterViewHolder<Integer> {

        private ItemProfileImageBinding mProfileImageBinding;

        ProfileImageHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mProfileImageBinding = (ItemProfileImageBinding) viewDataBinding;
        }

        @Override
        public void bind(Integer item) {

            mProfileImageBinding.setItemIndex(item);

            if (item == selectedPosition){
                previousSelectedItem = mProfileImageBinding.checkbox;
                previousSelectedItem.setChecked(true, false);
            }
        }


        @Override
        public void onClick(View view) {

            if (previousSelectedItem != null)
                previousSelectedItem.setChecked(false);

            mProfileImageBinding.checkbox.setChecked(true, true);

            previousSelectedItem = mProfileImageBinding.checkbox;
            selectedPosition = getItem(getAdapterPosition());

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getItem(getAdapterPosition()));
            }
        }
    }

}
