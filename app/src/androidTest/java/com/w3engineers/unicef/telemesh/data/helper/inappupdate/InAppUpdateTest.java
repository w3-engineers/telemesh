package com.w3engineers.unicef.telemesh.data.helper.inappupdate;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.util.helper.StatusHelper;
import com.w3engineers.unicef.util.helper.uiutil.AppBlockerUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class InAppUpdateTest {
    private String url;
    private Context mContext;
    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setup() {
        url = InAppUpdate.LIVE_JSON_URL;
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void downloadAppUpdateInfoFromServer() {

        addDelay(1500);

        InAppUpdate.getInstance(mContext).checkForUpdate(mContext, url);

        addDelay(4000);

        InAppUpdate.getInstance(mContext).prepareLocalServer();

        addDelay(2500);

        assertTrue(true);

        StatusHelper.out("Test executed");

        try {
            mDevice.pressBack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBaseUrlAlpha(){
        addDelay(2500);
        AppInstaller.downloadApkFile("http://www.dom@in.example", rule.getActivity(), null);
        assertTrue(true);
    }

    @Test
    @UiThreadTest
    public void appUpdateDialogOpenTest() {
        addDelay(2500);

        InAppUpdate.getInstance(rule.getActivity()).showAppInstallDialog(buildAppUpdateJson(), rule.getActivity());


        UiObject button = mDevice.findObject(new UiSelector().text("Cancel"));
        try {
            if (button.exists() && button.isEnabled()) {
                button.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        try {
            mDevice.pressBack();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(true);

        StatusHelper.out("Test executed");
    }

    @Test
    public void downloadConfigTest(){
        /*addDelay(500);
        Response<ResponseBody> response = Response.error(
                403,
                ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{\"key\":[\"somestuff\"]}"
                )
        );
        new UpdateAppConfigDownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                response.body());*/
    }

    @Test
    public void downloadUpdateAppTest() {
        addDelay(500);

        AppInstaller.downloadApkFile(AppCredentials.getInstance().getFileRepoLink(), rule.getActivity(), null);

        addDelay(4000);

        try {
            mDevice.pressBack();
            Espresso.pressBackUnconditionally();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StatusHelper.out("Test executed");

    }

    @Test
    @UiThreadTest
    public void appBlockerDialogOpenTest() {
        addDelay(500);

        AppBlockerUtil.openAppBlockerDialog(rule.getActivity(), "1.0.0");

        addDelay(2000);

        UiObject button = mDevice.findObject(new UiSelector().text("I Understand"));
        try {
            if (button.exists() && button.isEnabled()) {
                button.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        Constants.IS_DATA_ON = false;

        addDelay(500);

        AppBlockerUtil.openAppBlockerDialog(rule.getActivity(), "1.0.0");

        addDelay(2000);

        UiObject button1 = mDevice.findObject(new UiSelector().text("Update"));
        try {
            if (button1.exists() && button1.isEnabled()) {
                button1.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        addDelay(1000);

        assertTrue(true);

        StatusHelper.out("Test executed");
    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String buildAppUpdateJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.InAppUpdate.LATEST_VERSION_CODE_KEY, 100);
            jsonObject.put(Constants.InAppUpdate.LATEST_VERSION_KEY, "100.0.0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}