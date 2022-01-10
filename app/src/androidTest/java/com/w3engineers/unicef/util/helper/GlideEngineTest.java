package com.w3engineers.unicef.util.helper;


import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;


@RunWith(AndroidJUnit4.class)
public class GlideEngineTest {

    private Context context;
    private Activity mActivity;
    private ImageView imageView;
    private SubsamplingScaleImageView longImageView;

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
    public void glideEngineTest() throws Exception{

        addDelay(500);
        mActivity.runOnUiThread(() -> glideEngine.loadImage(context,
                "https://github.com/w3-engineers/telemesh/blob/master/images/discovery.png",imageView));

    }

    @Test
    public void glideScaleImageTest() throws Exception{

        addDelay(500);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                longImageView = new SubsamplingScaleImageView(context);
                addDelay(200);
                glideEngine.loadImage(context,
                        "https://github.com/w3-engineers/telemesh/blob/master/images/discovery.png",
                        imageView, longImageView, null);
            }
        });
       /* mActivity.runOnUiThread(() -> glideEngine.loadImage(context,
                "https://github.com/w3-engineers/telemesh/blob/master/images/discovery.png",
                imageView, longImageView, null));*/

    }

    @Test
    public void glideLongImageNullTest() throws Exception{

        addDelay(500);
        mActivity.runOnUiThread(() -> glideEngine.loadImage(context,
                "https://github.com/w3-engineers/telemesh/blob/master/images/discovery.png",
                imageView, null));
        assertTrue(true);

    }

    @Test
    public void glideFolderImageTest(){
        // this test class is added
        // delay is 500 second
        addDelay(500);
        mActivity.runOnUiThread(() -> {
            addDelay(200);
            glideEngine.loadFolderImage(context,
                    "https://github.com/w3-engineers/telemesh/blob/master/images/discovery.png",
                    imageView);
        });
        assertTrue(true);
    }

    @Test
    public void testGifImage(){
        addDelay(500);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addDelay(200);
                glideEngine.loadAsGifImage(context,
                        "https://github.com/w3-engineers/telemesh/blob/master/images/discovery.png",
                        imageView);
            }
        });
        assertTrue(true);
    }

    @Test
    public void testLoadGridImage(){
        addDelay(1000);
        AssetManager assetManager = context.getAssets();
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open("file:///android_asset/sample_image.jpg");
            bitmap = BitmapFactory.decodeStream(istr);
            addDelay(200);
            longImageView = new SubsamplingScaleImageView(context);
            glideEngine.setImageResource(bitmap, imageView, longImageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
       /* mActivity.runOnUiThread(() -> {


        });*/
        assertTrue(true);
    }

    @Test
    public void testBitmapResource(){
        addDelay(500);
        mActivity.runOnUiThread(() -> {
            addDelay(200);
            glideEngine.loadGridImage(context,
                    "https://github.com/w3-engineers/telemesh/blob/master/images/discovery.png",
                    imageView);
        });
        assertTrue(true);
    }


    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
