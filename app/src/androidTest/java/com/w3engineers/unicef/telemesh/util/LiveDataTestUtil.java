package com.w3engineers.unicef.telemesh.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [29-Jan-2019 at 7:20 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [29-Jan-2019 at 7:20 PM].
 * --> <Second Editor> on [29-Jan-2019 at 7:20 PM].
 * Reviewed by :
 * --> <First Reviewer> on [29-Jan-2019 at 7:20 PM].
 * --> <Second Reviewer> on [29-Jan-2019 at 7:20 PM].
 * ============================================================================
 **/
public class LiveDataTestUtil {

    public static <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);
        //noinspection unchecked
        return (T) data[0];
    }

    public static <T> TestObserver<T> testObserve(LiveData<T> liveData) {
        if(liveData != null) {
            TestObserver<T> testObserver = new TestObserver<T>();
            liveData.observeForever(testObserver);
            return testObserver;
        }

        return null;
    }
}
