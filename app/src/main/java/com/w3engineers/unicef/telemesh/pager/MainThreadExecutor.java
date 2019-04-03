package com.w3engineers.unicef.telemesh.pager;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Anjan Debnath on 1/4/2019.
 * Copyright (c) 2019, W3 Engineers Ltd. All rights reserved.
 */
public class MainThreadExecutor implements Executor {

    /**
     * On Handler if looper isn't provided, it'll by default operate on the current looper
     * from which the Handler is initialized, i.e Looper.myLooper().
     *
     * If you are in background thread and want to run something on main thread,
     * you won't be simply able to do it with new Handler() as that handler will
     * operate in the same thread.
     * But the Handler(Looper looper) constructor can be used in situations where
     * you want to change the thread other than the one from which the handle object is created.
     */
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable r) {
        handler.post(r);
    }
}