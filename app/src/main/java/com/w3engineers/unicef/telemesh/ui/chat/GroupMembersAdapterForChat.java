package com.w3engineers.unicef.telemesh.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupMemberForChatBinding;
import com.w3engineers.unicef.util.base.ui.BaseAdapter;
import com.w3engineers.unicef.util.base.ui.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GroupMembersAdapterForChat extends BaseAdapter<UserEntity> {

    private List<GroupMembersInfo> memberList;

    public GroupMembersAdapterForChat() {
        memberList = new ArrayList<>();
    }

    @Override
    public boolean isEqual(UserEntity left, UserEntity right) {
        return false;
    }

    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemGroupMemberForChatBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_group_member_for_chat, parent, false);
        return new GroupMembersAdapterForChat.GroupMembersVH(binding);
    }


    void submitAdminInfoList(List<GroupMembersInfo> memberList) {
        this.memberList = memberList;
    }

    List<GroupMembersInfo> getAdminInfoList() {
        return this.memberList;
    }

    class GroupMembersVH extends BaseViewHolder<UserEntity> {

        public GroupMembersVH(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
        }

        @Override
        public void bind(UserEntity item, ViewDataBinding viewDataBinding) {

            ItemGroupMemberForChatBinding binding = (ItemGroupMemberForChatBinding) viewDataBinding;
            binding.userMeshStatus.setBackgroundResource(activeStatusResource(item.getOnlineStatus()));
            binding.userName.setText(item.userName);
            binding.userAvatar.setImageResource(TeleMeshDataHelper.getInstance().getAvatarImage(item.avatarIndex));
        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onItemClick(view, getItem(getAdapterPosition()));
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
