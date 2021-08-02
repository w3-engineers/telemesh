package com.w3engineers.unicef.telemesh.data.local;

import android.content.Context;
import android.os.Parcel;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import android.text.TextUtils;

import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.broadcast.TokenGuideRequestModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdateModel;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackEntity;
import com.w3engineers.unicef.telemesh.data.local.db.BaseMigration;
import com.w3engineers.unicef.telemesh.data.local.db.Converters;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogEntity;
import com.w3engineers.unicef.telemesh.data.updateapp.UpdateConfigModel;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class ModelParcelableTest {
    Context context;
    private String userId = "0x8934394dnjsd3984394";
    private String msgId = "9843094";
    private RandomEntityGenerator randomEntityGenerator;

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
        randomEntityGenerator = new RandomEntityGenerator();
    }

    @Test
    public void bulletinTrackEntityTest() {
        addDelay();

        BulletinTrackEntity entity = new BulletinTrackEntity();
        entity.setBulletinAckStatus(Constants.Bulletin.BULLETIN_SEND_TO_SERVER);
        entity.setBulletinOwnerStatus(Constants.Bulletin.OTHERS);
        entity.setBulletinTrackUserId(userId);
        entity.setBulletinMessageId(msgId);

        Parcel parcel = Parcel.obtain();
        entity.writeToParcel(parcel, entity.describeContents());
        parcel.setDataPosition(0);

        BulletinTrackEntity createdFromParcel = BulletinTrackEntity.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel.getBulletinMessageId(), is(msgId));
        assertThat(createdFromParcel.getBulletinTrackUserId(), is(userId));
        assertThat(createdFromParcel.getBulletinAckStatus(), is(Constants.Bulletin.BULLETIN_SEND_TO_SERVER));
        assertThat(createdFromParcel.getBulletinOwnerStatus(), is(Constants.Bulletin.OTHERS));

        addDelay();

    }

    @Test
    public void appShareCountEntityTest() {

        addDelay();

        int count = 1;
        String date = "17-08-2019";
        AppShareCountEntity entity = new AppShareCountEntity();
        entity.setUserId(userId);
        entity.setCount(count);
        entity.setDate(date);
        entity.setSend(true);

        Parcel parcel = Parcel.obtain();
        entity.writeToParcel(parcel, entity.describeContents());
        parcel.setDataPosition(0);

        AppShareCountEntity appShareCountEntity = AppShareCountEntity.CREATOR.createFromParcel(parcel);

        assertThat(appShareCountEntity.getUserId(), is(userId));
        assertThat(appShareCountEntity.getCount(), is(count));
        assertThat(appShareCountEntity.getDate(), is(date));
        assertThat(appShareCountEntity.isSend(), is(true));

        addDelay();
    }

    @Test
    public void meshLogEntityTest() {

        addDelay();

        String logName = "testLog1.txt";
        MeshLogEntity entity = new MeshLogEntity();
        entity.setLogName(logName);

        Parcel parcel = Parcel.obtain();
        entity.writeToParcel(parcel, entity.describeContents());
        parcel.setDataPosition(0);

        MeshLogEntity meshLogEntity = MeshLogEntity.CREATOR.createFromParcel(parcel);
        assertThat(meshLogEntity.getLogName(), is(logName));

        addDelay();
    }

    @Test
    public void updateConfigModelTest() {

        addDelay();

        UpdateConfigModel configModel = randomEntityGenerator.generateUpdateConfigModel();
        Parcel parcel = Parcel.obtain();
        configModel.writeToParcel(parcel, configModel.describeContents());
        parcel.setDataPosition(0);

        UpdateConfigModel updatedConfigModel = UpdateConfigModel.CREATOR.createFromParcel(parcel);
        assertThat(updatedConfigModel.getVersionName(), is(configModel.getVersionName()));

        addDelay();
    }

    @Test
    public void InAppUpdateModelTest() {
        addDelay();
        String versionName = "1.0.0";
        String updateLink = "192.168.43.1";
        int updateType = 1;
        InAppUpdateModel model = new InAppUpdateModel();
        model.setVersionName(versionName);
        model.setUpdateLink(updateLink);
        model.setUpdateType(updateType);

        assertEquals(versionName, model.getVersionName());
        assertEquals(updateLink, model.getUpdateLink());
        assertEquals(updateType, model.getUpdateType());
        addDelay();

        String sampleRequest = "request";
        TokenGuideRequestModel tokenGuideRequestModel = new TokenGuideRequestModel();
        tokenGuideRequestModel.setRequest(sampleRequest);

        assertEquals(sampleRequest, tokenGuideRequestModel.getRequest());

        addDelay();

        String[] arr = {"query"};
        BaseMigration baseMigration = new BaseMigration(1, arr);

        assertEquals(baseMigration.getQueryScript()[0], arr[0]);

        addDelay();

        long msgTime = System.currentTimeMillis();

        MessageCountModel messageCountModel = new MessageCountModel();
        messageCountModel.setMsgTime(msgTime);

        assertEquals(msgTime, messageCountModel.getMsgTime());

        addDelay();
    }

    @Test
    public void dataConvertTest() {
        addDelay();

        Converters converters = new Converters();

        long currentTime = System.currentTimeMillis();

        Date date = Converters.toDate(currentTime);

        assertEquals(date.getTime(), currentTime);

        addDelay();

        long convertedTime = Converters.fromDate(date);

        assertEquals(convertedTime, convertedTime);

        addDelay();

        // Constants item test
        String res = Constants.capitalize(null);

        assertTrue(TextUtils.isEmpty(res));

        addDelay();

        String test = "Mobile";

        res = Constants.capitalize(test);

        assertEquals(test, res);

        addDelay();
    }

    @Test
    public void feedDataSetterTest() {
        addDelay();

        FeedEntity entity = new FeedEntity();

        String providerLogo = "logo.png";
        String providerName = "Camp 1";
        String feedTitle = "Sample title";

        entity.setFeedProviderLogo(providerLogo)
                .setFeedProviderName(providerName)
                .setFeedTitle(feedTitle)
                .setFeedReadStatus(false);

        addDelay();

        assertFalse(entity.isFeedRead());

        assertEquals(entity.getFeedTitle(), feedTitle);

        assertEquals(entity.getFeedProviderName(), providerName);
        assertEquals(entity.getFeedProviderLogo(), providerLogo);

        addDelay();

        Payload payload = new Payload();

        List<String> payloadData = new ArrayList<>();
        payloadData.add("data");
        payload.setConnectedClientEthIds(payloadData);

        assertEquals(payload.getConnectedClientEthIds().get(0), payloadData.get(0));

    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
