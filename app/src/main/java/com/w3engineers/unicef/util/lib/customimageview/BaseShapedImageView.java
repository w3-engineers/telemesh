package com.w3engineers.unicef.util.lib.customimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.w3engineers.unicef.telemesh.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressLint("CustomViewStyleable")
public abstract class BaseShapedImageView extends AppCompatImageView {

    private static final float INNER_DRAWABLE_PADDING_THRESHOLD = 0.25f;

    @IntDef({Shape.RECTANGLE, Shape.ROUNDED_RECTANGLE, Shape.CIRCLE, Shape.SQUARE, Shape.ROUNDED_SQUARE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shape {
        int RECTANGLE = 0;
        int ROUNDED_RECTANGLE = 1;
        int CIRCLE = 2;
        int SQUARE = 3;
        int ROUNDED_SQUARE = 4;
    }

    // view attributes
    @Shape
    int shape = Shape.CIRCLE;
    int shapeColor;
    int borderColor;
    int borderSize;
    int innerDrawablePadding;
    int innerDrawablePaddingLeft;
    int innerDrawablePaddingRight;
    int innerDrawablePaddingTop;
    int innerDrawablePaddingBottom;

    private static final float EVEN_FRACTION = 0.26f;
    float mTopLeftFraction = EVEN_FRACTION;
    float mTopRightFraction = EVEN_FRACTION;
    float mBottomRightFraction = EVEN_FRACTION;
    float mBottomLeftFraction = EVEN_FRACTION;

    Drawable mInnerDrawable;
    private Drawable mBorderDrawable;

    private PorterDuffXfermode mXfermode;
    private Canvas mMaskCanvas;
    private Bitmap mMaskBitmap;
    private Paint mMaskPaint;

    private Canvas mDrawableCanvas;
    private Bitmap mDrawableBitmap;
    private Paint mDrawablePaint;

    private Canvas mBorderDrawableCanvas;
    private Bitmap mBorderDrawableBitmap;
    private Paint mBorderDrawablePaint;

    private Canvas mBackgroundMaskCanvas;
    private Bitmap mBackgroundMaskBitmap;
    private Paint mBackgroundMaskPaint;

    private Canvas mBackgroundDrawableCanvas;
    private Bitmap mBackgroundDrawableBitmap;
    private Paint mBackgroundDrawablePaint;

    private Canvas mInnerDrawableCanvas;
    private Bitmap mInnerDrawableBitmap;

    private Drawable mBackgroundDrawable;

    private boolean invalidated = true;

    public BaseShapedImageView(Context context) {
        this(context, null);
    }

    public BaseShapedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseShapedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs, defStyle);
    }

    @CallSuper
    protected void setup(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomShapedImageView, defStyle, 0);
            shape = typedArray.getInt(R.styleable.CustomShapedImageView_imageShape, Shape.CIRCLE);
            shapeColor = typedArray.getColor(R.styleable.CustomShapedImageView_shapeColor, ContextCompat.getColor(context, R.color.light_grey));
            borderColor = typedArray.getColor(R.styleable.CustomShapedImageView_borderColor, shapeColor);
            borderSize = typedArray.getDimensionPixelOffset(R.styleable.CustomShapedImageView_borderWidth, 0);
            mInnerDrawable = typedArray.getDrawable(R.styleable.CustomShapedImageView_innerDrawable);
            innerDrawablePadding = typedArray.getDimensionPixelOffset(R.styleable.CustomShapedImageView_innerDrawablePadding, 0);
            innerDrawablePaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.CustomShapedImageView_innerDrawablePaddingStart, innerDrawablePadding);
            innerDrawablePaddingTop = typedArray.getDimensionPixelOffset(R.styleable.CustomShapedImageView_innerDrawablePaddingTop, innerDrawablePadding);
            innerDrawablePaddingRight = typedArray.getDimensionPixelOffset(R.styleable.CustomShapedImageView_innerDrawablePaddingEnd, innerDrawablePadding);
            innerDrawablePaddingBottom = typedArray.getDimensionPixelOffset(R.styleable.CustomShapedImageView_innerDrawablePaddingBottom, innerDrawablePadding);
            mTopLeftFraction = typedArray.getFloat(R.styleable.CustomShapedImageView_topLeftRadius, 0.0f);
            mTopRightFraction = typedArray.getFloat(R.styleable.CustomShapedImageView_topRightRadius, 0.0f);
            mBottomRightFraction = typedArray.getFloat(R.styleable.CustomShapedImageView_bottomRightRadius, 0.0f);
            mBottomLeftFraction = typedArray.getFloat(R.styleable.CustomShapedImageView_bottomLeftRadius, 0.0f);
            typedArray.recycle();
        }

        mBorderDrawable = ContextCompat.getDrawable(context, shape == Shape.CIRCLE ? R.drawable.shape_circle_white_stroke : R.drawable.shape_rect_white_stroke);

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mBackgroundMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundMaskPaint.setColor(Color.BLACK);
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setColor(Color.BLACK);
    }

    public void invalidate() {
        invalidated = true;
        super.invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createMaskCanvas(w, h, oldw, oldh);
    }

    protected void setCornerRadius(GradientDrawable gradientDrawable, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
        if (gradientDrawable == null) {
            return;
        }
        gradientDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
    }

    private void configureImageBorderLayer(Canvas canvas, int width, int height) {
        if (mBorderDrawable instanceof GradientDrawable) {
            int dimen = Math.max(width, height);
            mBorderDrawable.setBounds(0, 0, width, height);
            ((GradientDrawable) mBorderDrawable).setStroke(borderSize, borderColor);
            switch (shape) {
                case Shape.RECTANGLE:
                case Shape.SQUARE:
                    setCornerRadius(((GradientDrawable) mBorderDrawable), dimen * 0.0f, dimen * 0.0f, dimen * 0.0f, dimen * 0.0f);
                    break;
                case Shape.CIRCLE:
                    break;
                case Shape.ROUNDED_RECTANGLE:
                case Shape.ROUNDED_SQUARE:
                default:
                    setCornerRadius(((GradientDrawable) mBorderDrawable), dimen * mTopLeftFraction, dimen * mTopRightFraction, dimen * mBottomRightFraction, dimen * mBottomLeftFraction);
                    break;
            }
            mBorderDrawable.draw(canvas);
        }
    }

    private void configureInnerIconLayer(Canvas canvas, int width, int height) {
        if (mInnerDrawable != null) {
            // to maintain inner icon half of the dimension
            mInnerDrawable.setBounds(0, 0, getInnerDrawableWidth(width), getInnerDrawableHeight(height));
            mInnerDrawable.draw(canvas);
        }
    }

    @NonNull
    private RectF getDrawableBound(int drawableWidth, int drawableHeight) {
        int left = (getMeasuredWidth() - drawableWidth) / 2;
        int top = (getMeasuredHeight() - drawableHeight) / 2;
        int right = (left + drawableWidth);
        int bottom = (top + drawableHeight);
        return new RectF(left, top, right, bottom);
    }

    @NonNull
    private Rect getBackgroundBound(int drawableWidth, int drawableHeight) {
        int left = (getMeasuredWidth() - drawableWidth) / 2;
        int top = (getMeasuredHeight() - drawableHeight) / 2;
        int right = (left + drawableWidth);
        int bottom = (top + drawableHeight);
        return new Rect(left, top, right, bottom);
    }

    private int getInnerDrawableWidth(int width) {
        return getContentWidth(width) - (innerDrawablePaddingLeft + innerDrawablePaddingRight);
    }

    private int getInnerDrawableHeight(int height) {
        return getContentHeight(height) - (innerDrawablePaddingTop + innerDrawablePaddingBottom);
    }

    private int getContentWidth(int width) {
        return width - (getPaddingStart() + getPaddingEnd());
    }

    private int getContentHeight(int height) {
        return height - (getPaddingTop() + getPaddingBottom());
    }

    private void createMaskCanvas(int width, int height, int oldw, int oldh) {
        if (innerDrawablePadding <= 0 && innerDrawablePaddingLeft <= 0 && innerDrawablePaddingTop <= 0 && innerDrawablePaddingRight <= 0 && innerDrawablePaddingBottom <= 0) {
            innerDrawablePadding = innerDrawablePaddingLeft = innerDrawablePaddingTop = innerDrawablePaddingRight = innerDrawablePaddingBottom = (int) (Math.max(width, height) * INNER_DRAWABLE_PADDING_THRESHOLD);
        }
        boolean sizeChanged = width != oldw || height != oldh;
        boolean isValid = width > 0 && height > 0;
        if (isValid && (mBackgroundMaskCanvas == null || mMaskCanvas == null || sizeChanged)) {
            mBackgroundMaskCanvas = new Canvas();
            mBackgroundMaskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mBackgroundMaskCanvas.setBitmap(mBackgroundMaskBitmap);

            mBackgroundMaskPaint.reset();
            paintBackgroundMaskCanvas(mBackgroundMaskCanvas, mBackgroundMaskPaint, width, height);

            mMaskCanvas = new Canvas();
            mMaskBitmap = Bitmap.createBitmap(getContentWidth(width), getContentHeight(height), Bitmap.Config.ARGB_8888);
            mMaskCanvas.setBitmap(mMaskBitmap);

            mMaskPaint.reset();
            paintMaskCanvas(mMaskCanvas, mMaskPaint, getContentWidth(width), getContentHeight(height));

            if (mInnerDrawable != null && getInnerDrawableWidth(width) > 0 && getInnerDrawableHeight(height) > 0) {
                mInnerDrawableCanvas = new Canvas();
                mInnerDrawableBitmap = Bitmap.createBitmap(getInnerDrawableWidth(width), getInnerDrawableHeight(height), Bitmap.Config.ARGB_8888);
                mInnerDrawableCanvas.setBitmap(mInnerDrawableBitmap);
            }

            mBorderDrawableCanvas = new Canvas();
            mBorderDrawableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mBorderDrawableCanvas.setBitmap(mBorderDrawableBitmap);

            mBorderDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBorderDrawablePaint.setStyle(Paint.Style.STROKE);

            mBackgroundDrawableCanvas = new Canvas();
            mBackgroundDrawableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mBackgroundDrawableCanvas.setBitmap(mBackgroundDrawableBitmap);

            mBackgroundDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            mDrawableCanvas = new Canvas();
            mDrawableBitmap = Bitmap.createBitmap(getContentWidth(width), getContentHeight(height), Bitmap.Config.ARGB_8888);
            mDrawableCanvas.setBitmap(mDrawableBitmap);

            mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            invalidated = true;
        }
    }

    protected abstract void paintMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height);

    protected abstract void paintBackgroundMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height);

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            RectF contentBound = getDrawableBound(getContentWidth(getMeasuredWidth()), getContentHeight(getMeasuredHeight()));
            Rect backgroundBound = getBackgroundBound(getMeasuredWidth(), getMeasuredHeight());
            RectF innerDrawableBound = getDrawableBound(getInnerDrawableWidth(getMeasuredWidth()), getInnerDrawableHeight(getMeasuredHeight()));

            int saveCount = canvas.saveLayer(backgroundBound.left, backgroundBound.top, backgroundBound.right, backgroundBound.bottom, null, Canvas.ALL_SAVE_FLAG);

            try {
                if (invalidated) {
                    Drawable drawable = getDrawable();
                    if (drawable != null) {
                        invalidated = false;
                        Matrix imageMatrix = getImageMatrix();
                        if (imageMatrix == null) {
                            drawable.draw(mDrawableCanvas);
                        } else {
                            int drawableSaveCount = mDrawableCanvas.getSaveCount();
                            mDrawableCanvas.save();
                            mDrawableCanvas.concat(imageMatrix);
                            drawable.draw(mDrawableCanvas);
                            mDrawableCanvas.restoreToCount(drawableSaveCount);
                        }

                        mDrawablePaint.reset();
                        mDrawablePaint.setFilterBitmap(false);
                        mDrawablePaint.setXfermode(mXfermode);
                        mDrawableCanvas.drawBitmap(mMaskBitmap, contentBound.left, contentBound.top, mDrawablePaint);
                    } else if (mInnerDrawable != null) {
                        invalidated = true;
                        mDrawablePaint.reset();
                        mDrawablePaint.setXfermode(null);
                        configureInnerIconLayer(mInnerDrawableCanvas, getMeasuredWidth(), getMeasuredHeight());
                        canvas.drawBitmap(mInnerDrawableBitmap, innerDrawableBound.left, innerDrawableBound.top, mDrawablePaint);
                    }
                }

                if (mBackgroundDrawable != null) {
                    mBackgroundDrawable.setBounds(0, 0, backgroundBound.right, backgroundBound.bottom);
                    mBackgroundDrawable.draw(mBackgroundDrawableCanvas);
                }

                mBackgroundDrawablePaint.reset();
                mBackgroundDrawablePaint.setFilterBitmap(false);
                mBackgroundDrawablePaint.setXfermode(mXfermode);
                mBackgroundDrawableCanvas.drawBitmap(mBackgroundMaskBitmap, backgroundBound.left, backgroundBound.top, mBackgroundDrawablePaint);

                mBackgroundDrawablePaint.setXfermode(null);
                canvas.drawBitmap(mBackgroundDrawableBitmap, backgroundBound.left, backgroundBound.top, mBackgroundDrawablePaint);

                mDrawablePaint.setXfermode(null);

                if (invalidated && mInnerDrawable != null) {
                    canvas.drawBitmap(mInnerDrawableBitmap, innerDrawableBound.left, innerDrawableBound.top, mDrawablePaint);
                }

                if (!invalidated) {
                    canvas.drawBitmap(mDrawableBitmap, contentBound.left, contentBound.top, mDrawablePaint);
                }

                mBorderDrawablePaint.reset();
                configureImageBorderLayer(mBorderDrawableCanvas, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawBitmap(mBorderDrawableBitmap, backgroundBound.left, backgroundBound.top, mBorderDrawablePaint);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                canvas.restoreToCount(saveCount);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    public void setBackground(Drawable background) {
        if (mBackgroundDrawable != background) {
            mBackgroundDrawable = background;
            invalidate();
        }
    }

    @Override
    public Drawable getBackground() {
        return mBackgroundDrawable;
    }

    public void setCornerRadius(float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
        if (shape == Shape.ROUNDED_RECTANGLE || shape == Shape.ROUNDED_SQUARE) {
            boolean changed = false;
            if (topLeftRadius >= 0 && topLeftRadius <= 1 && mTopLeftFraction != topLeftRadius) {
                mTopLeftFraction = topLeftRadius;
                changed = true;
            }

            if (topRightRadius >= 0 && topRightRadius <= 1 && mTopRightFraction != topRightRadius) {
                mTopRightFraction = topRightRadius;
                changed = true;
            }

            if (bottomRightRadius >= 0 && bottomRightRadius <= 1 && mBottomRightFraction != bottomRightRadius) {
                mBottomRightFraction = bottomRightRadius;
                changed = true;
            }

            if (bottomLeftRadius >= 0 && bottomLeftRadius <= 1 && mBottomLeftFraction != bottomLeftRadius) {
                mBottomLeftFraction = bottomLeftRadius;
                changed = true;
            }
            if (changed) {
                invalidate();
            }
        }
    }
}
