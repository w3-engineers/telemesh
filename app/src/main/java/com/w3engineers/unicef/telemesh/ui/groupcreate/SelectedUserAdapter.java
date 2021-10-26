package com.w3engineers.unicef.telemesh.ui.groupcreate;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemSelectedUserBinding;
import com.w3engineers.unicef.util.base.ui.BaseAdapter;
import com.w3engineers.unicef.util.base.ui.BaseViewHolder;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

public class SelectedUserAdapter extends BaseAdapter<UserEntity> {

    @Override
    public boolean isEqual(UserEntity left, UserEntity right) {
        return false;
    }

    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSelectedUserBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_selected_user, parent, false);
        return new SelectedUserVH(binding);
    }

    class SelectedUserVH extends BaseViewHolder<UserEntity> {

        public SelectedUserVH(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
        }

        @Override
        public void bind(UserEntity item, ViewDataBinding viewDataBinding) {
            ItemSelectedUserBinding binding = (ItemSelectedUserBinding) viewDataBinding;

            UIHelper.updateImageNameField(binding.textViewImageName, item.userName, item.userLastName);

            binding.textViewName.setText(item.userName);
            setClickListener(binding.buttonRemove);
        }


        @Override
        public void onClick(View view) {
            mItemClickListener.onItemClick(view, getItem(getAdapterPosition()));
        }
    }
}
