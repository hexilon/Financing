package com.hexon.commonui;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;

/**
 * Copyright (C), 2020-2025
 * FileName    : AdaptionSizeTextView
 * Description :
 * Author      : Hexon
 * Date        : 2020/9/19 10:35
 * Version     : V1.0
 */
public class AdaptionSizeTextView extends androidx.appcompat.widget.AppCompatTextView {
    private Paint mTestPaint;

    public AdaptionSizeTextView(Context context) {
        super(context);
        initialise();
    }

    public AdaptionSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    private void initialise() {
        mTestPaint = new Paint();
        mTestPaint.set(getPaint());
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(String text, int textWidth) {
        if (textWidth <= 0)
            return;
        int targetWidth = textWidth - getPaddingLeft() - getPaddingRight();
        float textSize = getTextSize();

        mTestPaint.set(getPaint());
        while (textSize > 2) {
            mTestPaint.setTextSize(textSize);
            if (mTestPaint.measureText(text) >= targetWidth) {
                textSize--;
            } else if (mTestPaint.measureText(text) < targetWidth) {
                break;
            }
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = getMeasuredHeight();
        refitText(this.getText().toString(), parentWidth);
        this.setMeasuredDimension(parentWidth, height);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }
}
