package com.w3engineers.unicef.telemesh.ui.chooseprofileimage;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.databinding.ItemProfileImageBinding;
import com.w3engineers.unicef.util.lib.circle_checkbox.SmoothCheckBox;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [14-Sep-2018 at 11:58 AM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [14-Sep-2018 at 11:58 AM].
 * * --> <Second Editor> on [14-Sep-2018 at 11:58 AM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [14-Sep-2018 at 11:58 AM].
 * * --> <Second Reviewer> on [14-Sep-2018 at 11:58 AM].
 * * ============================================================================
 **/
public class ProfileImageAdapter extends BaseAdapter<Integer> {

    private Context mContext;
    private SmoothCheckBox previousSelectedItem;
    public int selectedPosition;


    public ProfileImageAdapter(Context context, int selectedPosition) {
        this.mContext = context;
        this.selectedPosition = selectedPosition;
    }

    @Override
    public boolean isEqual(Integer left, Integer right) {
        return left.equals(right);
    }

    @Override
    public Integer getItem(int position) {
        return position;
    }

    @Override
    public BaseAdapterViewHolder<Integer> newViewHolder(ViewGroup parent, int viewType) {
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
