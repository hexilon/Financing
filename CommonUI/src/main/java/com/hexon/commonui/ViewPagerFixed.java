package com.hexon.commonui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * @author Hexh
 * @date 2019-03-30 13:56
 */
public class ViewPagerFixed extends ViewPager {
    /**1 默认true 可以滑动;
     * 2 只需要将返回值改为false，那么ViewPager就不会消耗掉手指滑动的事件了，转而传递给上层View去处理或者该事件就直接终止了。*/
    private boolean mEnableScroll = true;

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollable(boolean enableScroll) {
        this.mEnableScroll = enableScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mEnableScroll) {
            try {
                return super.onTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mEnableScroll) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            return false;
        } else {
            return false;
        }
    }
}
