package com.w3engineers.unicef.telemesh.ui.groupcreate;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.mesh.application.ui.base.BaseAdapter;
import com.w3engineers.mesh.application.ui.base.BaseViewHolder;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemSelectedUserBinding;

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
            binding.userAvatar.setImageResource(TeleMeshDataHelper.getInstance()
                    .getAvatarImage(item.avatarIndex));
            binding.textViewName.setText(item.userName);
            setClickListener(binding.buttonRemove);
        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onItemClick(view, getItem(getAdapterPosition()));
        }
    }
}
