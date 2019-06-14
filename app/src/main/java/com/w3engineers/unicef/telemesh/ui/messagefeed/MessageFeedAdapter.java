package com.w3engineers.unicef.telemesh.ui.messagefeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.w3engineers.ext.strom.application.ui.base.BaseAdapter;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.databinding.ItemMessageFeedBinding;

import java.util.List;

/**
 * Adapter that fetches the {@link FeedEntity} from database and show the converted view.
 */

public class MessageFeedAdapter extends BaseAdapter<FeedEntity> {
    protected MessageFeedViewModel mMessageFeedViewModel;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public MessageFeedAdapter(Context context, MessageFeedViewModel messageFeedViewModel) {
        this.mMessageFeedViewModel = messageFeedViewModel;
        mContext = context;
    }

    @Override
    public boolean isEqual(FeedEntity left, FeedEntity right) {
        String leftFeedId = left.getFeedId();
        String rightFeedId = right.getFeedId();

        return !TextUtils.isEmpty(leftFeedId) && !TextUtils.isEmpty(rightFeedId) && leftFeedId.equals(rightFeedId);
    }

    /**
     * Update the adapter with the new data
     * <p>
     * Every time clear the data source and calling notifyDataSetChanged() is a costly operation in future          we will update this
     * </p>
     *
     * @param feedEntities the data source which will be converted to view
     */
    public void resetWithList(List<FeedEntity> feedEntities) {
        List<FeedEntity> feedEntityList = getItems();
        feedEntityList.clear();
        notifyDataSetChanged();
        addItem(feedEntities);
    }

    @Override
    public BaseAdapterViewHolder<FeedEntity> newViewHolder(ViewGroup parent, int viewType) {
        return new MessageFeedViewHolder(inflate(parent, R.layout.item_message_feed));
    }

    /**
     * Show the feed provider logo or default place holder
     *
     * @param imageView   the view location
     * @param url         the url of the required logo
     * @param placeHolder will show if the url is null
     */
    @BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String url, Drawable placeHolder) {
        if (url == null || url.isEmpty()) {
            imageView.setImageDrawable(placeHolder);
        } else {
            Glide.with(mContext)
                    .load(url)
                    .into(imageView);
        }
    }

    protected class MessageFeedViewHolder extends BaseAdapterViewHolder<FeedEntity> {
        private ItemMessageFeedBinding mItemMessageFeedBinding;

        private MessageFeedViewHolder(ViewDataBinding viewDataBinding) {
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
            mItemMessageFeedBinding.setMessageFeedViewModel(mMessageFeedViewModel);
            setImageUrl(mItemMessageFeedBinding.senderIcon, feedEntity.getFeedProviderLogo(), mContext.getResources().getDrawable(R.mipmap.ic_unicef));
        }


        @Override
        public void onClick(View v) {
            //Onclick event is managed using the data-binding from the XML
        }
    }
}
