package com.w3engineers.unicef.util.lib.boxpassword;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;

import com.w3engineers.unicef.telemesh.R;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class BoxPassword extends AppCompatEditText {
    private float mSpace = 15; //24 dp by default, space between the lines
    private float mNumChars = 4;
    private float mLineSpacing = 8; //8dp by default, height of the text from our lines
    private int mMaxLength = 8;
    private float mLineStroke = 2;
    private Paint mFillPaint;
    private Paint mStrokePaint;
    private OnClickListener mClickListener;

    private boolean isPasswordShow;

    public BoxPassword(Context context) {
        super(context);
    }

    public BoxPassword(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
        init(context, attrs);
    }

    public BoxPassword(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float multi = context.getResources().getDisplayMetrics().density;
        mLineStroke = multi * mLineStroke;
        mFillPaint = new Paint(getPaint());
        mFillPaint.setStrokeWidth(mLineStroke);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(getResources().getColor(R.color.white));

        mStrokePaint = new Paint(getPaint());
        mStrokePaint.setStrokeWidth(mLineStroke);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(getResources().getColor(R.color.password_border_color));

        setFocusable(true);

        setBackgroundResource(0);
        mSpace = multi * mSpace; //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing; //convert to pixels for our density
        mNumChars = mMaxLength;

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // When tapped, move cursor to end of text.
                setSelection(getText().length());
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });

        // setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(mMaxLength)});

        //setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");

                Log.d("FiterTest", "text length: " + result.length());

                if (!s.toString().equals(result)) {
                    setText(result);
                    setSelection(result.length());
                }

                if (result.length() > mMaxLength) {
                    Log.d("FiterTest", "before text: " + result);
                    result = result.substring(0, mMaxLength);
                    Log.d("FiterTest", "after text: " + result);
                    setText(result);
                    setSelection(result.length());
                }
            }
        });
    }

    @Override
    public void setOnClickListener(View.OnClickListener l) {
        mClickListener = l;
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        float mCharSize;
        if (mSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        } else {
            mCharSize = (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars;
        }

        int startX = getPaddingLeft();
        int bottom = getHeight() - getPaddingBottom();

        int top = getPaddingTop() - 10;


        //Text Width
        String text;
        if (isPasswordShow) {
            text = getText().toString();
        } else {
            text = convertStar();
        }
        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(getText(), 0, textLength, textWidths);
        getPaint().setColor(getResources().getColor(R.color.new_user_button_color));

        for (int i = 0; i < mNumChars; i++) {
            //canvas.drawLine(startX, bottom, startX + mCharSize, bottom, mFillPaint);
            //canvas.drawLine(startX, top, startX + mCharSize, top, mFillPaint);
            canvas.drawRect(startX, top, startX + mCharSize + 10, bottom, mFillPaint);
            canvas.drawRect(startX, top, startX + mCharSize + 10, bottom, mStrokePaint);
            if (getText().length() > i) {
                float middle = startX + mCharSize / 2;
                canvas.drawText(text, i, i + 1, middle - textWidths[0] / 2 + 5, bottom - mLineSpacing, getPaint());
            }
            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
        }
    }

    private String convertStar() {
        StringBuilder start = new StringBuilder();
        for (int i = 0; i < getText().length(); i++) {
            start.append("*");
        }
        return start.toString();
    }

    public void setPasswordShow(boolean isPasswordShow) {
        this.isPasswordShow = isPasswordShow;
        invalidate();
    }

}