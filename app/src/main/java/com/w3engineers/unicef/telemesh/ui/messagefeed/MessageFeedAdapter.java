package com.w3engineers.unicef.telemesh.ui.messagefeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemMessageFeedBinding;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.util.HashMap;
import java.util.List;

/**
 * Adapter that fetches the {@link FeedEntity} from database and show the converted view.
 */

public class MessageFeedAdapter extends BaseAdapter<FeedEntity> {
    @Nullable
    protected MessageFeedViewModel mMessageFeedViewModel;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private HashMap<String, Integer> broadcastMap = new HashMap<>();

    public MessageFeedAdapter(@NonNull Context context, @Nullable MessageFeedViewModel messageFeedViewModel) {
        this.mMessageFeedViewModel = messageFeedViewModel;
        mContext = context;
    }

    @Override
    public boolean isEqual(@NonNull FeedEntity left, @NonNull FeedEntity right) {
        return !TextUtils.isEmpty(left.getFeedId()) && !TextUtils.isEmpty(right.getFeedId()) && left.getFeedId().equals(right.getFeedId());
    }

    /**
     * Update the adapter with the new data
     * <p>
     * Every time clear the data source and calling notifyDataSetChanged() is a costly operation in future          we will update this
     * </p>
     *
     * @param feedEntities the data source which will be converted to view
     */
    public void resetWithList(@NonNull List<FeedEntity> feedEntities) {
        List<FeedEntity> feedEntityList = getItems();
        feedEntityList.clear();
        notifyDataSetChanged();
        addItem(feedEntities);
    }

    @Override
    @NonNull
    public BaseAdapterViewHolder<FeedEntity> newViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageFeedViewHolder(inflate(parent, R.layout.item_message_feed));
    }

    protected class MessageFeedViewHolder extends BaseAdapterViewHolder<FeedEntity> {
        private ItemMessageFeedBinding mItemMessageFeedBinding;

        protected MessageFeedViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            this.mItemMessageFeedBinding = (ItemMessageFeedBinding) viewDataBinding;
        }

        /**
         * Bind the feed entities
         *
         * @param feedEntity the required feed entity
         */
        @Override
        public void bind(FeedEntity feedEntity) {
            mItemMessageFeedBinding.setFeedEntity(feedEntity);

            String details = feedEntity.getFeedDetail();

            mItemMessageFeedBinding.senderName.setText(UIHelper.getBroadcastTitle(details));
            mItemMessageFeedBinding.senderIcon.setImageResource(UIHelper.getBroadcastLogo(details));
            mItemMessageFeedBinding.senderMessage.setText(UIHelper.getBroadcastMessage(details));

            mItemMessageFeedBinding.setMessageFeedViewModel(mMessageFeedViewModel);

            if (feedEntity.isFeedRead()) {
                mItemMessageFeedBinding.senderMessage.setTypeface(null, Typeface.NORMAL);
            } else {
                mItemMessageFeedBinding.senderMessage.setTypeface(null, Typeface.BOLD);
            }

           // setImageUrl(mItemMessageFeedBinding.senderIcon, feedEntity.getFeedProviderLogo(), mContext.getResources().getDrawable(R.mipmap.ic_unicef));
        }


        @Override
        public void onClick(View v) {
            //Onclick event is managed using the data-binding from the XML
        }
    }
}
