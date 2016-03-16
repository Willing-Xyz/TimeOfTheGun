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
public class CircleView extends View {
    private static final float DEFAULT_RAIDUS = 20.0f;
    private static final int DEFAULT_COLOR = 0xffff0000;
    private static final int DEFAULT_LINE_COLOR = 0xff000000;
    private int mLineColor;
    private float mLineWidth;

    private float mRadius;
    private int mColor;
    private Paint mPaint;
    private Paint mLinePaint;


    public CircleView(Context context) {
        this(context, null, 0);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mRadius = typedArray.getDimension(R.styleable.CircleView_circleRadius, DEFAULT_RAIDUS);

        mColor = typedArray.getColor(R.styleable.CircleView_android_color, DEFAULT_COLOR);
        mPaint = new Paint();
        mPaint.setColor(mColor);

        mLineColor = typedArray.getColor(R.styleable.CircleView_lineColor, DEFAULT_LINE_COLOR);
        mLineWidth = typedArray.getDimension(R.styleable.CircleView_lineWidth, 1);
        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineWidth);
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
                width = (int) (getPaddingLeft() + getPaddingRight() + mRadius * 2);
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
                height = (int) (getPaddingTop() + getPaddingBottom() + mRadius * 2);
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int startX = (getWidth() - getPaddingLeft() - getPaddingRight() ) / 2;
        int startY = getPaddingTop();
        int stopX = startX;
        int stopY = getHeight() - getPaddingTop();
        canvas.drawLine(startX, startY, stopX, stopY, mLinePaint);

        int x = getWidth() / 2 - (getPaddingLeft() + getPaddingRight()) / 2;
        canvas.drawCircle(x, getHeight() / 2, mRadius, mPaint);
    }

    public void setRadius(float radius)
    {
        if (mRadius != radius) {
            mRadius = radius;
            invalidate();
        }
    }

    public float getRadius()
    {
        return mRadius;
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

  public int getColor() {
      return mColor;
    }

    public void setLineColor(int color)
    {
        if (mLineColor != color)
        {
            mLineColor = color;
            mLinePaint.setColor(mLineColor);
            invalidate();
        }
    }

    public int getLineColor()
    {
        return mLineColor;
    }

    public void setLineWidth(float width)
    {
        if (mLineWidth != width)
        {
            mLineWidth = width;
            mLinePaint.setStrokeWidth(mLineWidth);
            invalidate();
        }
    }

    public float getLineWidth()
    {
        return mLineWidth;
    }
}
