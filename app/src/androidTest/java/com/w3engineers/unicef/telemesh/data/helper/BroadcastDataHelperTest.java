package com.w3engineers.unicef.telemesh.data.helper;

import static org.junit.Assert.assertTrue;

import androidx.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastMeta;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class BroadcastDataHelperTest {
    private BroadcastDataHelper broadcastDataHelper;
    private RandomEntityGenerator randomEntityGenerator;

    @Before
    public void setUp() throws Exception {
        broadcastDataHelper = BroadcastDataHelper.getInstance();
        randomEntityGenerator = new RandomEntityGenerator();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_local_broadcast_receive() {
        String broadcastId = UUID.randomUUID().toString();

        BroadcastMeta broadcastMeta = prepareBroadcastMetaData();

        String broadcastMetaJson = GsonBuilder.getInstance().getBroadcastMetaJson(broadcastMeta);

        broadcastDataHelper.receiveLocalBroadcast(broadcastId, broadcastMetaJson, randomEntityGenerator.getDummyImageLink(), 0, 0, 0, "");

        addDelay(1000);

        BulletinFeed bulletinFeed = prepareBulletinFeed(broadcastId);

        String bulletinJson = new Gson().toJson(bulletinFeed);

        broadcastDataHelper.responseBroadcastMsg(bulletinJson);

        addDelay(4000);

        broadcastDataHelper.requestForBroadcast();

        addDelay(2000);

        assertTrue(true);
    }

    private BulletinFeed prepareBulletinFeed(String broadcastId) {
        BulletinFeed feed = new BulletinFeed();
        feed.setBroadcastAddress("abcd")
                .setLatitude(22.8456)
                .setLongitude(89.5403)
                .setMessageType(Constants.BroadcastMessageType.IMAGE_BROADCAST)
                .setMessageBody("test message")
                .setMessageTitle("Test title")
                .setFileName("myfile_1624623314123-467515276.jpeg")
                .setMessageId(broadcastId)
                .setUploaderInfo("Unicef");
        return feed;
    }

    private BroadcastMeta prepareBroadcastMetaData() {
        BroadcastMeta meta = new BroadcastMeta();
        meta.setBroadcastAddress("address");
        meta.setMessageBody("Test broadcast");
        meta.setMessageTitle("Unicef");
        meta.setUploaderName("Unicef");
        meta.setCreationTime("2021-05-20T18:00:56Z");
        return meta;
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}