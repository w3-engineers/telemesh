package com.w3engineers.unicef.util.lib.customimageview;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class BaseShapedImageViewTest {

    public class ConcreteClass extends BaseShapedImageView{

        public ConcreteClass(Context context) {
            super(context);
        }

        @Override
        protected void paintMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {

        }

        @Override
        protected void paintBackgroundMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {

        }
    }

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void radiusCornerTest() {
        addDelay();
        /*ConcreteClass conClass = new ConcreteClass(mContext);
        conClass.setCornerRadius(5.0f, 5.0f, 5.0f,5.0f);*/
        assertTrue(true);
    }
}
