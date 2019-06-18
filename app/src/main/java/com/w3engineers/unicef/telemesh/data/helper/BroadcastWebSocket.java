package com.w3engineers.unicef.telemesh.data.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.util.Log;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastCommand;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class BroadcastWebSocket extends WebSocketListener {

    private BroadcastCommand broadcastCommand;
    private int closeCode = 1001;
    private String closeMessage = "Goodbye !";

    public void setBroadcastCommand(BroadcastCommand broadcastCommand) {
        this.broadcastCommand = broadcastCommand;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        if (broadcastCommand != null) {
            String broadcastString = new Gson().toJson(broadcastCommand);
            webSocket.send(broadcastString);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);
            if (jsonObject.has("status")) {
                RmDataHelper.getInstance().processBroadcastAck(text);
            } else {
                RmDataHelper.getInstance().processBroadcastMessage(text);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        webSocket.close(closeCode, closeMessage);
    }

    @Override
    public void onMessage(WebSocket webSocket, okio.ByteString bytes) {
        Log.v("MIMO_SAHA:", "Message: " + bytes);
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.v("MIMO_SAHA:", "Close: " + code);
        webSocket.close(closeCode, null);
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.v("MIMO_SAHA:", "Fail: " + t.getMessage());
    }
}
