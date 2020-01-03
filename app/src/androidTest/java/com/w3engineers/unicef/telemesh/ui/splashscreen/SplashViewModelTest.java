package com.w3engineers.unicef.telemesh.ui.splashscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SplashViewModelTest {

    private SharedPreferences sharedPreferences;
    private String REGISTRATION_STATUS = "REGISTRATION_STATUS";
    private boolean isRegistered = true;

    private SplashViewModel SUT;

    @Rule
    public ActivityTestRule<SplashActivity> rule  = new ActivityTestRule<>(SplashActivity.class);

    @Before
    public void setUp() {

        Context context = InstrumentationRegistry.getTargetContext();
        SUT = new SplashViewModel(rule.getActivity().getApplication());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        setRegistrationStatus(isRegistered);
    }

    @After
    public void tearDown() {

    }

    private void setRegistrationStatus(boolean registrationStatus) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(REGISTRATION_STATUS, registrationStatus);
        editor.commit();
    }

    @Test
    public void getUserRegistrationStatus_afterTime_checkRegister() {

        SUT.getUserRegistrationStatus();

        boolean registrationStatus = sharedPreferences.getBoolean(REGISTRATION_STATUS, false);
        assertEquals(registrationStatus, isRegistered);
    }
}
