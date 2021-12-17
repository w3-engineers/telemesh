package com.w3engineers.unicef.util.lib.customimageview;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BaseShapedImageViewTest {

    @Rule
    public ActivityTestRule<AboutUsActivity> mActivityRule = new ActivityTestRule(AboutUsActivity.class);

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

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void radiusCornerTest() {

        addDelay(200);
        // activity reference is required otherwise 'android.content.res.Resources$NotFoundException' will be occurred
        ConcreteClass conClass = new ConcreteClass(mActivityRule.getActivity());
        conClass.setCornerRadius(BaseShapedImageView.Shape.ROUNDED_RECTANGLE,
                1.0f, 1.0f,
                1.0f,1.0f);
        assertTrue(true);

    }
}
