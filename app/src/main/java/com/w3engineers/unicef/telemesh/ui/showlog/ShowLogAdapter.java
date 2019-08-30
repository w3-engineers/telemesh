package com.w3engineers.unicef.telemesh.ui.showlog;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.databinding.ItemLogBinding;

import java.util.List;

public class ShowLogAdapter extends BaseAdapter<MeshLogModel> {

    private Context mContext;

    ShowLogAdapter(Context context) {
        mContext = context;
    }

    @Override
    public boolean isEqual(MeshLogModel left, MeshLogModel right) {
        return false;
    }

    @NonNull
    @Override
    public BaseAdapterViewHolder<MeshLogModel> newViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShowLogViewHolder(inflate(parent, R.layout.item_log));
    }

    public void resetWithList(@NonNull List<MeshLogModel> items) {
        List<MeshLogModel> userEntities = getItems();
        userEntities.clear();
        notifyDataSetChanged();
        addItem(items);
    }

    private class ShowLogViewHolder extends BaseAdapterViewHolder<MeshLogModel> {
        private ItemLogBinding mLogBinding;

        public ShowLogViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mLogBinding = (ItemLogBinding) viewDataBinding;
        }

        @Override
        public void bind(MeshLogModel item) {
            mLogBinding.textViewLog.setText(item.getLog());

            if (item.getType() == 1) {
                mLogBinding.textViewLog.setTextColor(mContext.getResources().getColor(R.color.color_special));
            } else if (item.getType() == 2) {
                mLogBinding.textViewLog.setTextColor(mContext.getResources().getColor(R.color.warning_color));
            } else if (item.getType() == 3) {
                mLogBinding.textViewLog.setTextColor(mContext.getResources().getColor(R.color.info_color));
            } else if (item.getType() == 4) {
                mLogBinding.textViewLog.setTextColor(mContext.getResources().getColor(R.color.error_color));
            } else if (item.getType() == 5) {
                mLogBinding.textViewLog.setTextColor(mContext.getResources().getColor(R.color.color_date));
            } else {
                mLogBinding.textViewLog.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }
    }
}
