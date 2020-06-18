package com.w3engineers.unicef.telemesh.ui.groupdetails;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.mesh.application.ui.base.BaseAdapter;
import com.w3engineers.mesh.application.ui.base.BaseViewHolder;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupMemberBinding;

public class GroupDetailsAdapter extends BaseAdapter<UserEntity> {

    @Override
    public boolean isEqual(UserEntity left, UserEntity right) {
        return false;
    }

    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemGroupMemberBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_group_member, parent, false);
        return new GroupDetailsVH(binding);
    }

    class GroupDetailsVH extends BaseViewHolder<UserEntity> {

        public GroupDetailsVH(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
        }

        @Override
        public void bind(UserEntity item, ViewDataBinding viewDataBinding) {
            ItemGroupMemberBinding binding = (ItemGroupMemberBinding) viewDataBinding;

            binding.userMeshStatus.setBackgroundResource(activeStatusResource(item.getOnlineStatus()));
            binding.userName.setText(item.userName);

            binding.userAvatar.setImageResource(TeleMeshDataHelper.getInstance()
                    .getAvatarImage(item.avatarIndex));

            //Todo we have to show admin info here
        }

        @Override
        public void onClick(View view) {

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
    }
}
