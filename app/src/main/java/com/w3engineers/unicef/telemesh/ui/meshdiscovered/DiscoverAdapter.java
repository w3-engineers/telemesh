package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import android.databinding.BindingAdapter;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemDiscoveredBinding;

import java.util.List;

public class DiscoverAdapter extends BaseAdapter<UserEntity> {

    @NonNull
    public DiscoverViewModel discoverViewModel;

    DiscoverAdapter(@NonNull DiscoverViewModel discoverViewModel) {
        this.discoverViewModel = discoverViewModel;
    }

    @Override
    public boolean isEqual(@NonNull UserEntity left, @NonNull UserEntity right) {
        String leftUserId = left.getMeshId();
        String rightUserId = right.getMeshId();


        return !TextUtils.isEmpty(leftUserId)
                && !TextUtils.isEmpty(rightUserId)
                && leftUserId.equals(rightUserId);

    }

    @NonNull
    @Override
    public BaseAdapterViewHolder<UserEntity> newViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiscoverViewHolder(inflate(parent, R.layout.item_discovered));
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(@NonNull ImageView imageView, int imageResourceId) {
        imageView.setImageResource(imageResourceId);
    }

    public void resetWithList(@NonNull List<UserEntity> items) {

        List<UserEntity> userEntities = getItems();
        userEntities.clear();
        notifyDataSetChanged();
        addItem(items);
    }

    private class DiscoverViewHolder extends BaseAdapterViewHolder<UserEntity> {
        private ItemDiscoveredBinding itemDiscoveredBinding;

        DiscoverViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            this.itemDiscoveredBinding = (ItemDiscoveredBinding) viewDataBinding;
        }

        @Override
        public void bind(@NonNull UserEntity item) {

            itemDiscoveredBinding.userMeshStatus.setBackgroundResource(activeStatusResource(item.getOnlineStatus()));
            itemDiscoveredBinding.userName.setText(item.userName + getHopIndicator(item.getOnlineStatus()));

            itemDiscoveredBinding.setUser(item);
            itemDiscoveredBinding.setDiscoverViewModel(discoverViewModel);
        }

        private String getHopIndicator(int userActiveStatus) {
            if (userActiveStatus == Constants.UserStatus.BLE_MESH_ONLINE
                    || userActiveStatus == Constants.UserStatus.WIFI_MESH_ONLINE)
                return "";
            else
                return "";
        }

        private int activeStatusResource(int userActiveStatus) {

            if (userActiveStatus == Constants.UserStatus.WIFI_ONLINE || userActiveStatus == Constants.UserStatus.WIFI_MESH_ONLINE) {
                return R.mipmap.ic_mesh_online;
            } else if (userActiveStatus == Constants.UserStatus.BLE_MESH_ONLINE || userActiveStatus == Constants.UserStatus.BLE_ONLINE) {
                return R.mipmap.ic_mesh_online;
            } else if (userActiveStatus == Constants.UserStatus.INTERNET_ONLINE) {
                return R.mipmap.ic_internet;
            } else {
                return R.mipmap.ic_offline;
            }

            /*if (userActiveStatus == Constants.UserStatus.WIFI_ONLINE || userActiveStatus == Constants.UserStatus.BLE_ONLINE) {
                return R.drawable.circle_online;
            } else if (userActiveStatus == Constants.UserStatus.INTERNET_ONLINE) {
                return R.drawable.circle_internet;
            } else {
                return R.drawable.circle_offline;
            }*/
        }
    }

}
