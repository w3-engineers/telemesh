package com.w3engineers.unicef.telemesh.data.broadcast;

import android.os.Message;

/**
 * Created by Anjan Debnath on 6/28/2018.
 * Copyright (c) 2018, W3 Engineers Ltd. All rights reserved.
 *
 * An interface for worker threads to send messages to the UI thread.
 * MainActivity implemented this Interface in this app.
 */

public interface UiThreadCallback {
    void publishToUiThread(Message message);
}
