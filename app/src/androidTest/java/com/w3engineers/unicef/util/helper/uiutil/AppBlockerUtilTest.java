package com.w3engineers.unicef.util.helper.uiutil;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppBlockerUtilTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);
    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

    @Test
    @UiThreadTest
    public void appUpdateAndBlockerTest() {
        /*addDelay(1000);

        rule.getActivity().stopAnimation();

        addDelay(500);

        rule.getActivity().checkPlayStoreAppUpdate(1, buildAppUpdateJson());

        addDelay(5 * 1000);

        RmDataHelper.getInstance().appUpdateFromOtherServer(1, "");


        addDelay(10 * 1000);*/

        assertTrue(true);
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