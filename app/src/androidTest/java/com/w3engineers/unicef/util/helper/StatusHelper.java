package com.w3engineers.unicef.util.helper;

import android.app.Instrumentation;
import android.os.Bundle;

import androidx.test.InstrumentationRegistry;

public class StatusHelper {
    public static void out(String str) {
        Bundle b = new Bundle();
        b.putString(Instrumentation.REPORT_KEY_IDENTIFIER, "\n" + str);
        InstrumentationRegistry.getInstrumentation().sendStatus(0, b);
    }
}
