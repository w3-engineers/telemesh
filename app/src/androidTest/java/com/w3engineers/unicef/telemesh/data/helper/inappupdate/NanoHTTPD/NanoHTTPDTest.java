package com.w3engineers.unicef.telemesh.data.helper.inappupdate.NanoHTTPD;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class NanoHTTPDTest {
    private String host;
    private int port;
    private SimpleWebServer webServer;
    private static File rootFile;
    private Context mContext;

    @Before
    public void setup() {
        host = "192.168.43.1";
        port = 8990;
        mContext = TeleMeshApplication.getContext();
        rootFile = mContext.getApplicationContext().getFilesDir();
        webServer = new SimpleWebServer(host, port, rootFile, false);
    }

    @Test
    public void serverRunningResponseTest() {
        if (!webServer.isAlive()) {
            try {
                webServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NanoHTTPD.DefaultTempFileManager defaultTempFileManager = new NanoHTTPD.DefaultTempFileManager();

        NanoHTTPD.TempFileManager tempFileManager = defaultTempFileManager;

        String sampleText = "This is Telemesh App";

        InputStream inputStream = new ByteArrayInputStream(sampleText.getBytes(StandardCharsets.UTF_8));

        OutputStream outputStream = new OutputStream() {
            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                this.string.append((char) b);
            }

            //Netbeans IDE automatically overrides this toString()
            public String toString() {
                return this.string.toString();
            }
        };


        NanoHTTPD.HTTPSession session = webServer.createHttpSession(tempFileManager, inputStream, outputStream);

        try {
            session.execute();

            addDelay(2000);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}