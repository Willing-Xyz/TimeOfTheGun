package com.willing.android.timeofgun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.willing.android.timeofgun.R;

import java.util.ArrayList;

public class StartStopButton extends View
{
    // 属性默认值
	private static final int	DEF_START_COLOR	= 0x00ff00;
	private static final int	DEF_STOP_COLOR	= 0xff0000;
	private static final int	DEF_COLOR	= 0xdddddd;
	private static final float	DEF_TEXT_SIZE	= 30;
	private static final float	DEF_CIRCLE_WIDTH	= 3;
	private static final String DEF_START_TEXT = "开始";
	private static final String DEF_STOP_TEXT = "结束";
	
	private Paint mPaint;
	private volatile boolean mStarted;
	private ArrayList<StateChangeListener> listeners;
	
	// 半径
	private float mRadius;
	private String mStartText;
	private String mStopText;
	private int mStartColor;
	private int mStopColor;
	private float mTextSize;
	// Circle内部的颜色
	private int mColor;
	// 圆边界的宽度
	private float mCircileThickness;
	
	public StartStopButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		TypedArray arr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.start_stop_button, defStyle, 0);
		
		mRadius = arr.getDimension(R.styleable.start_stop_button_android_radius, 20);
		mStartText = arr.getString(R.styleable.start_stop_button_startText);
		mStopText = arr.getString(R.styleable.start_stop_button_stopText);
		mStartColor = arr.getColor(R.styleable.start_stop_button_startColor, DEF_START_COLOR);
		mStopColor = arr.getColor(R.styleable.start_stop_button_stopColor, DEF_STOP_COLOR);
		mColor = arr.getColor(R.styleable.start_stop_button_android_color, DEF_COLOR);
		mTextSize = arr.getDimension(R.styleable.start_stop_button_android_textSize, DEF_TEXT_SIZE);
		mCircileThickness = arr.getDimension(R.styleable.start_stop_button_circleThickness, DEF_CIRCLE_WIDTH);

		if (mStartText == null)
		{
			mStartText = DEF_START_TEXT;
		}
		if (mStopText == null)
		{
			mStopText = DEF_STOP_TEXT;
		}
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setDither(true);
		mPaint.setStyle(Style.STROKE);
		mPaint.setTextSize(mTextSize);

		listeners = new ArrayList<StateChangeListener>();
	}

	public StartStopButton(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public StartStopButton(Context context)
	{
		this(context, null, 0);
	}
	
	

	public float getTextSize()
	{
		return mTextSize;
	}

	public void setTextSize(float textSize)
	{
		mTextSize = textSize;
		mPaint.setTextSize(textSize);
		invalidate();
	}

	public float getCircileThickness()
	{
		return mCircileThickness;
	}

	public void setCircileThickness(float circileThickness)
	{
		mCircileThickness = circileThickness;
		invalidate();
	}

	public String getStartText()
	{
		return mStartText;
	}

	public void setStartText(String startText)
	{
		mStartText = startText;
		invalidate();
	}

	public String getStopText()
	{
		return mStopText;
	}

	public void setStopText(String stopText)
	{
		mStopText = stopText;
		invalidate();
	}

	public int getStartColor()
	{
		return mStartColor;
	}

	public void setStartColor(int startColor)
	{
		mStartColor = startColor;
		invalidate();
	}

	public int getStopColor()
	{
		return mStopColor;
	}

	public void setStopColor(int stopColor)
	{
		mStopColor = stopColor;
		invalidate();
	}

	public int getColor()
	{
		return mColor;
	}

	public void setColor(int color)
	{
		mColor = color;
		invalidate();
	}

	public float getRadius()
	{
		return mRadius;
	}

	public void setRadius(float radius)
	{
		mRadius = radius;
		invalidate();
	}

	public boolean isStarted()
	{
		return mStarted;
	}

	public void setStarted(boolean started)
	{
		if (this.mStarted != started)
		{
			this.mStarted = started;
			fireStateChangeListener(mStarted);
			invalidate();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int width = 0;
		int height = 0;
		
		width = (int) (2 * mRadius + getPaddingLeft() + getPaddingRight() + mCircileThickness);
		height = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + mCircileThickness);
		
		switch (widthMode)
		{
		case MeasureSpec.AT_MOST:
			width = Math.min(width, widthSize);
			break;
		case MeasureSpec.EXACTLY:
			width = widthSize;
			break;
		}
		switch (heightMode)
		{
		case MeasureSpec.AT_MOST:
			height = Math.min(height, heightSize);
			break;
		case MeasureSpec.EXACTLY:
			height = heightSize;
			break;
		}
		
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// 画Circle
		int x = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
		int y = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();

        int len = (int) Math.min(getHeight() - getPaddingTop() - getPaddingBottom() - mCircileThickness,
                getWidth() - getPaddingLeft() - getPaddingRight() - mCircileThickness);
        int radius = (int) Math.min(mRadius, len / 2);

        mPaint.setStrokeWidth(mCircileThickness);
		mPaint.setStyle(Style.FILL);
        mPaint.setColor(mColor);
		canvas.drawCircle(x, y, radius, mPaint);

		// 画Circle的边界
        String text = null;
        if (mStarted)
        {
            text = mStopText;
            mPaint.setColor(mStopColor);
        }
        else
        {
            text = mStartText;
            mPaint.setColor(mStartColor);
        }
        mPaint.setStyle(Style.STROKE);
        canvas.drawCircle(x, y, radius, mPaint);

		// 画文本
		mPaint.setStrokeWidth(1);
		mPaint.setStyle(Style.FILL);
		Rect bounds = new Rect();
		mPaint.getTextBounds(text, 0, 2, bounds);
		// 文本的起始点
		x = x - bounds.width() / 2;
		y = (int) (y + bounds.height() / 2 - mPaint.descent());


		if (mStarted)
		{
			canvas.drawText(mStopText, x, y, mPaint);
		}
		else
		{
			canvas.drawText(mStartText, x, y, mPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
        int x = (int) event.getX();
        int y = (int) event.getY();

        // 圆的中心点
        int centerX = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
        int centerY = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();

        int dx = Math.abs(centerX - x);
        int dy = Math.abs(centerY - y);

        // 如果点击事件不在圆内部，则直接返回
        if (dx * 2 + dy * 2 > mRadius * 2)
        {
            return true;
        }


		int action = event.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_UP:
			mStarted = !mStarted;
			fireStateChangeListener(mStarted);
			postInvalidate();
			break;
		}

		return true;
	}


	public void registerStateChangeListener(StateChangeListener listener)
	{
		if (!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}
	public void unregisterStateChangeListener(StateChangeListener listener)
	{
		listeners.remove(listener);
	}
	private void fireStateChangeListener(boolean nowState)
	{
		for (int i = 0; i < listeners.size(); ++i)
		{
			listeners.get(i).stateChanged(nowState);
		}
	}

	@Override
	public Parcelable onSaveInstanceState()
	{
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.mStarted = mStarted;

		return ss;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		mStarted = ss.mStarted;
	}

	public void setStartStateJust(boolean startStateJust)
	{
		mStarted = startStateJust;

		postInvalidate();
	}

	private static class SavedState extends BaseSavedState
	{
		boolean mStarted;

		public SavedState(Parcelable superState)
		{
			super(superState);
		}

		public SavedState(Parcel source)
		{
			super(source);

			mStarted = source.readInt() > 0 ? true : false;
		}

		@Override
		public void writeToParcel(Parcel out, int flags)
		{
			super.writeToParcel(out, flags);
			out.writeInt(mStarted ? 1 : 0);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>()
		{

			@Override
			public SavedState createFromParcel(Parcel source)
			{
				return new SavedState(source);
			}

			@Override
			public SavedState[] newArray(int size)
			{
				return new SavedState[size];
			}
		};
	}

	public static interface StateChangeListener
	{
		void stateChanged(boolean started);
	}
}
