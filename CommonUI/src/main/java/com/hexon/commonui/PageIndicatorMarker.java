package com.hexon.commonui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class PageIndicatorMarker extends FrameLayout {
    private static final int MARKER_FADE_DURATION = 175;

    private ImageView mActiveMarker;
    private ImageView mInactiveMarker;
    private boolean mIsActive = false;

    public PageIndicatorMarker(Context context) {
        this(context, null);
    }

    public PageIndicatorMarker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorMarker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        mActiveMarker = (ImageView) findViewById(R.id.active);
        mInactiveMarker = (ImageView) findViewById(R.id.inactive);
    }

    void setMarkerDrawables(int activeResId, int inactiveResId) {
        Resources r = getResources();
        mActiveMarker.setImageDrawable(r.getDrawable(activeResId));
        mInactiveMarker.setImageDrawable(r.getDrawable(inactiveResId));
    }

    void activate(boolean immediate) {
        if (immediate) {
            mActiveMarker.animate().cancel();
            mActiveMarker.setAlpha(1f);
            mActiveMarker.setScaleX(1f);
            mActiveMarker.setScaleY(1f);
            mInactiveMarker.animate().cancel();
            mInactiveMarker.setAlpha(0f);
        } else {
            mActiveMarker.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(MARKER_FADE_DURATION).start();
            mInactiveMarker.animate()
                    .alpha(0f)
                    .setDuration(MARKER_FADE_DURATION).start();
        }
        mIsActive = true;
    }

    void inactivate(boolean immediate) {
        if (immediate) {
            mInactiveMarker.animate().cancel();
            mInactiveMarker.setAlpha(1f);
            mActiveMarker.animate().cancel();
            mActiveMarker.setAlpha(0f);
            mActiveMarker.setScaleX(0.5f);
            mActiveMarker.setScaleY(0.5f);
        } else {
            mInactiveMarker.animate().alpha(1f)
                    .setDuration(MARKER_FADE_DURATION).start();
            mActiveMarker.animate()
                    .alpha(0f)
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .setDuration(MARKER_FADE_DURATION).start();
        }
        mIsActive = false;
    }

    boolean isActive() {
        return mIsActive;
    }
}
