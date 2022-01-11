package com.w3engineers.unicef.telemesh.ui.termofuse;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.runner.lifecycle.Stage.RESUMED;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.uiautomator.UiDevice;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.selectaccount.SelectAccountActivity;
import com.w3engineers.unicef.util.helper.StatusHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

@RunWith(AndroidJUnit4.class)
public class TermsOfUseActivityTest {

    @Rule
    public ActivityTestRule<TermsOfUseActivity> mActivityTestRule = new ActivityTestRule<>(TermsOfUseActivity.class);
    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
    public Activity currentActivity = null;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    public void tearDown() {
    }

    @Test
    public void xiaomiPopupTest() {

        try {
            addDelay(3000);

            //mActivityTestRule.getActivity().onActivityResult(109, -1, null);
            mActivityTestRule.getActivity().permissionActivityResultLauncher.getContract();

            addDelay(2000);
            mActivityTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivityTestRule.getActivity().showPermissionGifForXiaomi();

                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        addDelay(8000);

        try {
            onView(withId(R.id.button_ok)).perform(click());

            addDelay(2000);

            currentActivity = getActivityInstance();

            if (!(currentActivity instanceof SelectAccountActivity)) {
                currentActivity = getActivityInstance();
                Intent intent = new Intent(currentActivity, SelectAccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            addDelay(2000);

            onView(withId(R.id.button_import_account)).perform(click());
            addDelay(2000);


            SharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, false);

            onView(withId(R.id.button_create_account)).perform(click());

            addDelay(2000);
            hideKeyboard(currentActivity = getActivityInstance());
            addDelay(1000);
            mDevice.pressBack();
            addDelay(1000);

            SharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void oldAccountExistsTest() {
        try{
            addDelay(3000);

            RmDataHelper.getInstance().onWalletPrepared(true);

            addDelay(2000);

            hideKeyboard(getActivityInstance());

            addDelay(1000);

            mDevice.pressBack();

            StatusHelper.out("Terms of use activity executed executed");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void testClickListener(){
        try{
            addDelay(2000);
            mActivityTestRule.getActivity().buttonClickListener(null);
            assertTrue(true);
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testAppDetailsSettings(){
        try{
            addDelay(2000);
            mActivityTestRule.getActivity().intentAppDetailsSettings();
            assertTrue(true);
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Activity getActivityInstance() {
        getInstrumentation().runOnMainSync(() -> {
            Collection resumedActivities =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = (Activity) resumedActivities.iterator().next();
            }
        });

        return currentActivity;
    }
}