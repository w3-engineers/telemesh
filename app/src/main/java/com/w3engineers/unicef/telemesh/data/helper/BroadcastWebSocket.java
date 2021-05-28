package com.w3engineers.unicef.telemesh.data.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastCommand;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class BroadcastWebSocket extends WebSocketListener {

    private BroadcastCommand broadcastCommand;
    private int closeCode = 1001;
    private String closeMessage = "Goodbye !";

    public void setBroadcastCommand(@NonNull BroadcastCommand broadcastCommand) {
        this.broadcastCommand = broadcastCommand;
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        if (broadcastCommand != null) {
            String broadcastString = new Gson().toJson(broadcastCommand);
            webSocket.send(broadcastString);
        }
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);
            if (jsonObject.has("status")) {
                BroadcastDataHelper.getInstance().responseBroadcastAck(text);
            } else {
                BroadcastDataHelper.getInstance().responseBroadcastMsg(text);
            }
        } catch (JSONException e) { e.printStackTrace(); }

        webSocket.close(closeCode, closeMessage);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull okio.ByteString bytes) {
        //Timber.tag("MIMO_SAHA:").v("Message: %s", bytes);
    }
    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @Nullable String reason) {
//        Timber.tag("MIMO_SAHA:").v("Close: %s", code);
        webSocket.close(closeCode, null);
    }
    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        Timber.tag("MIMO_SAHA:").v("Fail: %s", t.getMessage());
    }
}
