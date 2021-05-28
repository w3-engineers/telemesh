package com.w3engineers.unicef.telemesh.ui.groupdetails;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemGroupMemberBinding;
import com.w3engineers.unicef.util.base.ui.BaseAdapter;
import com.w3engineers.unicef.util.base.ui.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailsAdapter extends BaseAdapter<UserEntity> {

    private List<GroupMembersInfo> adminInfoList;
    private String myId;
    private boolean amIAdmin;

    public GroupDetailsAdapter(String myUserId) {
        adminInfoList = new ArrayList<>();
        this.myId = myUserId;
    }

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

    void submitAdminInfoList(List<GroupMembersInfo> adminList, boolean amIAdmin) {
        this.adminInfoList = adminList;
        this.amIAdmin = amIAdmin;
    }

    List<GroupMembersInfo> getAdminInfoList() {
        return adminInfoList;
    }

    class GroupDetailsVH extends BaseViewHolder<UserEntity> {

        public GroupDetailsVH(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
        }

        @Override
        public void bind(UserEntity item, ViewDataBinding viewDataBinding) {

            ItemGroupMemberBinding binding = (ItemGroupMemberBinding) viewDataBinding;

            setClickListener(binding.getRoot(), binding.imageViewRemove);
            if (item.getMeshId().equals(myId)) {
                binding.userMeshStatus.setBackgroundResource(activeStatusResource(Constants.UserStatus.WIFI_ONLINE));
            } else {
                binding.userMeshStatus.setBackgroundResource(activeStatusResource(item.getOnlineStatus()));
            }
            binding.userName.setText(item.userName);

            binding.userAvatar.setImageResource(TeleMeshDataHelper.getInstance()
                    .getAvatarImage(item.avatarIndex));

            if (item.getMeshId().equals(myId)) {
                binding.imageViewRemove.setVisibility(View.INVISIBLE);

                if (isAdmin(item.getMeshId())) {
                    binding.textViewAdminInfo.setVisibility(View.VISIBLE);
                } else {
                    binding.textViewAdminInfo.setVisibility(View.INVISIBLE);
                }

            } else {
                binding.imageViewRemove.setVisibility(View.INVISIBLE);
                binding.textViewAdminInfo.setVisibility(View.INVISIBLE);
                if (isAdmin(item.getMeshId())) {
                    binding.textViewAdminInfo.setVisibility(View.VISIBLE);
                } else {
                    if (amIAdmin) {
                        binding.imageViewRemove.setVisibility(View.VISIBLE);
                    }
                }
            }

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

        private boolean isAdmin(String userId) {
            for (GroupMembersInfo groupMembersInfo : adminInfoList) {
                if (groupMembersInfo.getMemberId().equals(userId) && groupMembersInfo.isAdmin()) {
                    return true;
                }
            }
            return false;
        }
    }
}
