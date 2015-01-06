package com.thb.slidingmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.thb.slidingmenu.R;

/**
 * Created by tanghb on 15/1/6.
 */
public class SlidingMenu extends HorizontalScrollView {

    private static final String tag = "SlidingMenu";
    private int mMenuPaddingRight;

    private View mMenu;
    private View mContent;

    private int mScreenWidth;
    private int mMenuWidth;

    private boolean isOpen;
    private boolean once;


    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = getScreenWidth(context);
        TypedArray a = null;
        try {
            a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.SlidingMenu,
                    defStyleAttr, 0
            );
            int count = a.getIndexCount();
            Log.i(tag, "the count is " + count);
            for (int i = 0; i < count; i++) {
                int attr = a.getIndex(i);
                Log.i(tag, "the attr is " + attr);
                Log.i(tag, "the styleable is " + R.styleable.SlidingMenu_paddingRight);
                switch (attr) {
                    case R.styleable.SlidingMenu_paddingRight:
                        mMenuPaddingRight = a.getDimensionPixelSize(attr,
                                (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 50f,
                                        getResources().getDisplayMetrics()));// 默认为10DP
                        Log.i(tag, "the value of paddingRight is " + mMenuPaddingRight);
                        break;
                }
            }
        } finally {
            if (null != a)
                a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            mMenu = wrapper.getChildAt(0);
            mContent = wrapper.getChildAt(1);
            mMenuWidth = mScreenWidth - mMenuPaddingRight;
            Log.i(tag, "the value of menuWidth is " + mMenuWidth);
            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = mScreenWidth;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mMenuWidth, 0);
            once = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                final int moveX = this.getScrollX();
                if (moveX > mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpen = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    private int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
