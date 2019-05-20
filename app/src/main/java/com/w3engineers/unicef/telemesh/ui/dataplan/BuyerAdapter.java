package com.w3engineers.unicef.telemesh.ui.dataplan;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemDataBuyerBinding;
import com.w3engineers.unicef.telemesh.databinding.ItemMeshContactBinding;
import com.w3engineers.unicef.telemesh.ui.dataplan.model.BuyerUser;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactViewModel;

import java.util.List;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class BuyerAdapter extends BaseAdapter<BuyerUser> {

    @Override
    public boolean isEqual(@NonNull BuyerUser left, @NonNull BuyerUser right) {
        return false;

    }

    @NonNull
    @Override
    public BaseAdapterViewHolder<BuyerUser> newViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BuyerViewModel(inflate(parent, R.layout.item_data_buyer));
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(@NonNull ImageView imageView, int imageResourceId) {
        imageView.setImageResource(imageResourceId);
    }

    private class BuyerViewModel extends BaseAdapterViewHolder<BuyerUser> {
        private ItemDataBuyerBinding itemDataBuyerBinding;

        BuyerViewModel(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            this.itemDataBuyerBinding = (ItemDataBuyerBinding) viewDataBinding;
        }

        @Override
        public void bind(@NonNull BuyerUser item) {

            Context context = itemDataBuyerBinding.userName.getContext();

            itemDataBuyerBinding.userName.setText(item.getUserName());
            itemDataBuyerBinding.userUseAmount.setText(String.format(context.getResources().getString(R.string.used_s), item.getUsageData()));

            if (item.getActiveMode() == Constants.BuyerStatus.DEFAULT) {
                itemDataBuyerBinding.status.setVisibility(View.GONE);
                itemDataBuyerBinding.statusLayout.setVisibility(View.GONE);
            } else if (item.getActiveMode() == Constants.BuyerStatus.ACTIVE) {
                itemDataBuyerBinding.status.setVisibility(View.VISIBLE);
                itemDataBuyerBinding.status.setText(context.getResources().getString(R.string.active));
                itemDataBuyerBinding.statusLayout.setBackgroundResource(R.drawable.ractangular_green_small);
            } else if (item.getActiveMode() == Constants.BuyerStatus.IN_USE) {
                itemDataBuyerBinding.status.setVisibility(View.VISIBLE);
                itemDataBuyerBinding.status.setText(context.getResources().getString(R.string.in_use));
                itemDataBuyerBinding.status.setTextColor(context.getResources().getColor(R.color.in_use_color));
                itemDataBuyerBinding.statusLayout.setBackgroundResource(R.drawable.ractangular_orange_small);
            }
        }
    }
}
