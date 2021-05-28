package com.w3engineers.unicef.telemesh.ui.chooseprofileimage;

import android.content.Context;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.databinding.ItemProfileImageBinding;
import com.w3engineers.unicef.util.base.ui.BaseAdapter;
import com.w3engineers.unicef.util.base.ui.BaseViewHolder;
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
    private Context mContext;


    public ProfileImageAdapter(int selectedPosition, Context context) {
        this.selectedPosition = selectedPosition;
        this.mContext = context;
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
    public BaseViewHolder<Integer> newViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileImageHolder(inflate(parent, R.layout.item_profile_image));
    }


    private class ProfileImageHolder extends BaseViewHolder<Integer> {

        private ItemProfileImageBinding mProfileImageBinding;

        public ProfileImageHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mProfileImageBinding = (ItemProfileImageBinding) viewDataBinding;
        }

        @Override
        public void bind(Integer item, ViewDataBinding viewDataBinding) {
            mProfileImageBinding.setItemIndex(item);

            int id = mContext.getResources().getIdentifier(Constants.drawables.AVATAR_IMAGE + item, Constants.drawables.AVATAR_DRAWABLE_DIRECTORY, mContext.getPackageName());
            mProfileImageBinding.imageViewProfile.setImageResource(id);

            if (item == selectedPosition) {
                previousSelectedItem = mProfileImageBinding.checkbox;
                previousSelectedItem.setChecked(true, false);
            }

            mProfileImageBinding.getRoot().setOnClickListener(v -> {
                if (previousSelectedItem != null)
                    previousSelectedItem.setChecked(false);

                mProfileImageBinding.checkbox.setChecked(true, true);

                previousSelectedItem = mProfileImageBinding.checkbox;
                selectedPosition = getItem(getAdapterPosition());

                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, getItem(getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

}
