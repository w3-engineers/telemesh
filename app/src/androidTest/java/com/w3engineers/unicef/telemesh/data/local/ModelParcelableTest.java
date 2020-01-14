package com.w3engineers.unicef.telemesh.data.local;

import android.content.Context;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.broadcast.TokenGuideRequestModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdateModel;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackEntity;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

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

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
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
    public void InAppUpdateModelTest() {
        addDelay();
        String versionName = "1.0.0";
        String updateLink = "192.168.43.1";
        InAppUpdateModel model = new InAppUpdateModel();
        model.setVersionName(versionName);
        model.setUpdateLink(updateLink);

        assertEquals(versionName, model.getVersionName());
        assertEquals(updateLink, model.getUpdateLink());
        addDelay();

        String sampleRequest = "request";
        TokenGuideRequestModel tokenGuideRequestModel = new TokenGuideRequestModel();
        tokenGuideRequestModel.setRequest(sampleRequest);

        assertEquals(sampleRequest, tokenGuideRequestModel.getRequest());

        addDelay();

    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
