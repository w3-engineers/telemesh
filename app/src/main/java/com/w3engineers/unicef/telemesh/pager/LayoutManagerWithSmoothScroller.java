package com.w3engineers.unicef.telemesh.pager;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Anjan Debnath on 12/4/2018.
 * Copyright (c) 2018, W3 Engineers Ltd. All rights reserved.
 */

/**
 * This will scroll to the RecyclerView item of specified position.
 */
public class LayoutManagerWithSmoothScroller extends LinearLayoutManager {

    public LayoutManagerWithSmoothScroller(@NonNull Context context) {
        super(context, VERTICAL, false);
    }

    /*public LayoutManagerWithSmoothScroller(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }*/

    @Override
    public void smoothScrollToPosition(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state,
                                       int position) {
        RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }


    private class TopSnappedSmoothScroller extends LinearSmoothScroller {
        public TopSnappedSmoothScroller(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return LayoutManagerWithSmoothScroller.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }
}
