package com.thb.scratchcard.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.thb.scratchcard.R;

/**
 * 刮奖View
 * 
 * @author tanghb
 */
public class ScratchCard extends View {

    private String mTxtContent;
    private int mTxtColor;
    private int mTxtSize;
    private Drawable mDrawable;

    private Canvas mCanvas;
    private Paint mFingerPaint;
    private Path mFingerPath;
    private Paint mTxtPaint;
    private Rect mTextBound;
    private Bitmap mBitmap;
    private PorterDuffXfermode mXMode;

    private int mLastX;
    private int mLastY;

    private int mWidth;
    private int mHeight;

    public ScratchCard(Context context) {
        this(context, null);
    }

    public ScratchCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ScratchCard, 0, 0);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.ScratchCard_text:
                mTxtContent = a.getString(attr);
                break;
            case R.styleable.ScratchCard_textColor:
                mTxtColor = a.getColor(attr, Color.RED);
                break;
            case R.styleable.ScratchCard_textSize:
                mTxtSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        14, getResources().getDisplayMetrics()));
                break;
            case R.styleable.ScratchCard_src:
                mDrawable = a.getDrawable(attr);
                break;
            default:
                break;
            }
        }
        init();
    }

    private void init() {
        mFingerPath = new Path();
        mFingerPaint = new Paint();
        mFingerPaint.setColor(Color.RED);
        mFingerPaint.setAntiAlias(true);
        mFingerPaint.setDither(true);
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
        mFingerPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角
        // 设置画笔宽度
        mFingerPaint.setStrokeWidth(20);

        mTxtPaint = new Paint();
        mTxtPaint.setStyle(Style.FILL);
        mTxtPaint.setTextScaleX(2f);
        mTxtPaint.setColor(mTxtColor);
        mTxtPaint.setTextSize(mTxtSize);

        mTextBound = new Rect();
        mTxtPaint.getTextBounds(mTxtContent, 0, mTxtContent.length(), mTextBound);

        mCanvas = new Canvas();
        mXMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mLastX = x;
            mLastY = y;
            mFingerPath.moveTo(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
            int dx = Math.abs(x - mLastX);
            int dy = Math.abs(y - mLastY);

            if (dx > 3 || dy > 3)
                mFingerPath.lineTo(x, y);

            mLastX = x;
            mLastY = y;
            break;

        default:
            break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mBitmap) {
            mWidth = getWidth();
            mHeight = getHeight();
            mDrawable.setBounds(0, 0, mWidth, mHeight);
            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
            mCanvas.setBitmap(mBitmap);
            mDrawable.draw(mCanvas);
        }
        final float x = mWidth / 2 - mTextBound.width() / 2;
        final float y = mHeight / 2 + mTextBound.height() / 2;
        canvas.drawText(mTxtContent, x, y, mTxtPaint);

        mFingerPaint.setXfermode(mXMode);
        mCanvas.drawPath(mFingerPath, mFingerPaint);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

}