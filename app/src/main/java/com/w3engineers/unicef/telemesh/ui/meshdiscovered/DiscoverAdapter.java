package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemDiscoveredBinding;

import java.util.ArrayList;
import java.util.List;

public class DiscoverAdapter extends PagedListAdapter<UserEntity, DiscoverAdapter.GenericViewHolder> {

    @NonNull
    public DiscoverViewModel discoverViewModel;

    DiscoverAdapter(@NonNull DiscoverViewModel discoverViewModel) {
        super(DIFF_CALLBACK);
        this.discoverViewModel = discoverViewModel;
    }

    public static final DiffUtil.ItemCallback<UserEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UserEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
                    return oldItem.getMeshId().equalsIgnoreCase(newItem.getMeshId()) ;
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldItem.equals(newItem);
                }
            };


    @BindingAdapter({"android:src"})
    public static void setImageViewResource(@NonNull ImageView imageView, int imageResourceId) {
        imageView.setImageResource(imageResourceId);
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //   LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //  ItemMeshContactBinding itemMeshContactBinding = ItemMeshContactBinding.inflate(inflater, viewGroup, false);

        return new DiscoverViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_discovered, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder baseViewHolder, int position) {
        UserEntity userEntity = getItem(position);
        if (userEntity != null) {
            baseViewHolder.bindView(userEntity);
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            baseViewHolder.clearView();
        }
    }

    public abstract class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bindView(@NonNull UserEntity item);

        protected abstract void clearView();
    }

    private class DiscoverViewHolder extends GenericViewHolder {
        private ItemDiscoveredBinding itemDiscoveredBinding;

        DiscoverViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.itemDiscoveredBinding = (ItemDiscoveredBinding) viewDataBinding;
        }


        @Override
        protected void bindView(@NonNull UserEntity item) {
            itemDiscoveredBinding.userMeshStatus.setBackgroundResource(activeStatusResource(item.getOnlineStatus()));
            itemDiscoveredBinding.userName.setText(item.userName + getHopIndicator(item.getOnlineStatus()));

            itemDiscoveredBinding.textViewUnreadMessageCount.setVisibility(View.GONE);

            if (item.hasUnreadMessage > 0) {
                itemDiscoveredBinding.textViewUnreadMessageCount.setVisibility(View.VISIBLE);
            } else {
                itemDiscoveredBinding.textViewUnreadMessageCount.setVisibility(View.GONE);
            }

            itemDiscoveredBinding.setUser(item);
            itemDiscoveredBinding.setDiscoverViewModel(discoverViewModel);
        }

        @Override
        protected void clearView() {
            itemDiscoveredBinding.invalidateAll();
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