package com.willing.android.timeofgun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.willing.android.timeofgun.R;

/**
 * 圆角图片
 * Created by Willing on 2016/3/12.
 */
public class CircleImageView extends View {
    private static final int DEFAULT_BORDER_COLOR = 0xff000000;
    private static final float DEFAULT_BORDER_WIDTH = 2;
    private static final float DEFAULT_RADIUS = 50;



    // 圆点
    private final float mCircleCenter;

    private int mBorderColor;
    private float mBorderWidth;

    private Bitmap mBitmap;
    private Paint mPaint;
    private Paint mBorderPaint;
    private final Drawable mDrawable;

    private final float mRadius;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        mBorderColor = typedArray.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);
        mBorderWidth = typedArray.getDimension(R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
        mRadius = typedArray.getDimension(R.styleable.CircleImageView_radius, DEFAULT_RADIUS);
        mDrawable = typedArray.getDrawable(R.styleable.CircleImageView_src);
        typedArray.recycle();


        scaleImage(mDrawable);

        // 设置BitmapShader
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        setShaderBitmap();

        // 初始化边界Paint
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setDither(true);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mCircleCenter = mRadius + mBorderWidth;

    }

    // 缩放图片以居中
    private void scaleImage(Drawable mDrawable) {

        int height = mDrawable.getIntrinsicHeight();
        int width = mDrawable.getIntrinsicWidth();

        // 把Drawable绘制到Bitmap中
        mDrawable.setBounds(0, 0, width, height);
        Bitmap tmpBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(tmpBitmap);
        mDrawable.draw(canvas);

        scaleImage(tmpBitmap);
    }

    private void scaleImage(Bitmap tmpBitmap) {

        int height = tmpBitmap.getHeight();
        int width = tmpBitmap.getWidth();
        // 剪切成正方形
        int x = 0;
        int y = 0;
        int len = width;
        if (height > width)
        {
            y = (int) (height - width) / 2;
            len = width;
        }
        else
        {
            x = (int) (width - height) / 2;
            len = height;
        }
        Bitmap tmpBitmap2 = Bitmap.createBitmap(tmpBitmap, x, y, len, len);
        if (tmpBitmap != tmpBitmap2) {
            tmpBitmap.recycle();
        }

        // 缩放到合适的大小
        mBitmap = Bitmap.createScaledBitmap(tmpBitmap2, (int)mRadius * 2, (int)mRadius * 2, false);
        if (mBitmap != tmpBitmap2) {
            tmpBitmap2.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension((int) (mRadius + mBorderWidth) * 2, (int) (mRadius + mBorderWidth) * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(mCircleCenter,mCircleCenter, mRadius, mPaint);
        canvas.drawCircle(mCircleCenter, mCircleCenter, mRadius + mBorderWidth / 2, mBorderPaint);
    }

    public void setImage(Bitmap bitmap)
    {
        scaleImage(bitmap);
        setShaderBitmap();
        invalidate();
    }

    public void setImage(int res)
    {
        scaleImage(getResources().getDrawable(res));
        setShaderBitmap();
        invalidate();
    }

    public void setImage(Drawable drawable)
    {
        scaleImage(drawable);
        setShaderBitmap();
        invalidate();
    }

    private void setShaderBitmap() {

        mPaint.setShader(new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
    }
}
