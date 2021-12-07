package com.w3engineers.unicef.util.helper;


import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class GlideEngineTest {

    private Context context;
    private Activity mActivity;
    private ImageView imageView;

    GlideEngine glideEngine;

    @Rule
    public ActivityTestRule<ChatActivity> mActivityRule =
            new ActivityTestRule<>(ChatActivity.class);

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
        mActivity = mActivityRule.getActivity();
        glideEngine = GlideEngine.createGlideEngine();
        imageView = (ImageView) mActivity.findViewById(R.id.imageView);
    }

    @Test
    public void glideEngineTest(){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                glideEngine.loadImage(context,
                        "https://github.com/w3-engineers/telemesh/blob/master/images/discovery.png",imageView);
            }
        });

    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
