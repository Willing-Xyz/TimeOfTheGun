package com.willing.android.timeofgun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.willing.android.timeofgun.R;


/**
 * Created by Willing on 2016/3/11.
 */
public class RectView extends View {
    private static final int DEFAULT_COLOR = 0xff00ff00;

    private int mLength;

    private int mColor;
    private Paint mPaint;

    public RectView(Context context) {
        this(context, null, 0);
    }

    public RectView(Context context, AttributeSet attrs) {
        this(context,attrs, 0);
    }

    public RectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RectView);

        mColor = typedArray.getColor(R.styleable.RectView_android_color, DEFAULT_COLOR);
        mPaint = new Paint();
        mPaint.setColor(mColor);

        mLength = typedArray.getInt(R.styleable.RectView_length, 0);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSize;
        switch (widthMode)
        {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                width = widthSize;
                break;
        }

        int height = heightSize;
        switch (heightMode)
        {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                width = widthSize;
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();

            int rectWidth = width * mLength / 100;
            canvas.drawRect(getPaddingLeft(), getPaddingTop(),
                    rectWidth + getPaddingLeft(), getHeight() - getPaddingTop() - getPaddingBottom(), mPaint);
    }

    public void setColor(int color)
    {
        if (mColor != color)
        {
            mColor = color;
            mPaint.setColor(mColor);
            invalidate();
        }
    }

    public int getColor()
    {
        return mColor;
    }

    public void setLength(int len)
    {
        if (mLength != len)
        {
            mLength = len;
            invalidate();
        }
    }

    public int getLength()
    {
        return mLength;
    }

}
