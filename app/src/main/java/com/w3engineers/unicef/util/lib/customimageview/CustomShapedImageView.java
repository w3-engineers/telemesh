package com.w3engineers.unicef.util.lib.customimageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;

import com.w3engineers.unicef.telemesh.R;

public class CustomShapedImageView extends BaseShapedImageView {

    private Uri imageUri;
    private String imageUrl;
    private String phText;
    private float phTextSize;

    private Drawable shapeMuxDrawable;
    private Drawable shapeBackgroundMuxDrawable;

    public CustomShapedImageView(Context context) {
        this(context, null);
    }

    public CustomShapedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomShapedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs, defStyle);
    }

    @Override
    protected void setup(Context context, AttributeSet attrs, int defStyle) {
        super.setup(context, attrs, defStyle);
        shapeMuxDrawable = ContextCompat.getDrawable(context, shape == Shape.CIRCLE ? R.drawable.shape_circle_white : R.drawable.shape_rect_white);
        shapeBackgroundMuxDrawable = ContextCompat.getDrawable(context, shape == Shape.CIRCLE ? R.drawable.shape_circle_white : R.drawable.shape_rect_white);
    }

    private void configureImageBgLayer(Drawable drawable, int width, int height, boolean setBackground) {
        int dimen = Math.max(width, height);
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(shapeColor);
            switch (shape) {
                case Shape.RECTANGLE:
                case Shape.SQUARE:
                    setCornerRadius((GradientDrawable) drawable, dimen * 0.0f, dimen * 0.0f, dimen * 0.0f, dimen * 0.0f);
                    break;
                case Shape.CIRCLE:
                    break;
                case Shape.ROUNDED_RECTANGLE:
                case Shape.ROUNDED_SQUARE:
                default:
                    setCornerRadius((GradientDrawable) drawable, dimen * mTopLeftFraction, dimen * mTopRightFraction, dimen * mBottomRightFraction, dimen * mBottomLeftFraction);
                    break;
            }
            if (setBackground && getBackground() == null) {
                setBackground(drawable);
            }
        }
    }

    @Override
    protected void paintMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {
        if (shapeMuxDrawable != null) {
            shapeMuxDrawable.setBounds(0, 0, width, height);
            configureImageBgLayer(shapeMuxDrawable, width, height, false);
            shapeMuxDrawable.draw(maskCanvas);
        }
    }

    @Override
    protected void paintBackgroundMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {
        if (shapeBackgroundMuxDrawable != null) {
            shapeBackgroundMuxDrawable.setBounds(0, 0, width, height);
            configureImageBgLayer(shapeBackgroundMuxDrawable, width, height, true);
            shapeBackgroundMuxDrawable.draw(maskCanvas);
        }
    }

   /* public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhText() {
        return phText;
    }

    public void setPhText(String phText) {
        this.phText = phText;
    }

    public float getPhTextSize() {
        return phTextSize;
    }

    public void setPhTextSize(float phTextSize) {
        this.phTextSize = phTextSize;
    }*/
}
