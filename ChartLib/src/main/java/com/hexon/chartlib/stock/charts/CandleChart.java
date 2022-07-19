package com.hexon.chartlib.stock.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.jobs.MoveViewJob;
import com.github.mikephil.charting.jobs.ZoomJob;
import com.hexon.chartlib.R;
import com.hexon.chartlib.stock.data.KLineDataManager;
import com.hexon.chartlib.stock.model.KLineDataModel;
import com.hexon.chartlib.stock.renderer.StockCombinedChartRenderer;
import com.hexon.chartlib.stock.utils.CommonUtil;
import com.hexon.chartlib.stock.view.KLineMarkerView;

/**
 * @author Hexh
 * @date 2019-07-06 13:23
 */
public class CandleChart extends CombinedChart {
    private final String TAG = "CandleChart";
    KLineMarkerView mLeftMarkerView;
    KLineMarkerView mRightMarkerView;
    KLineMarkerView mBottomMarkerView;
    private KLineDataManager mManager;

    public CandleChart(Context context) {
        super(context);
    }

    public CandleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new StockCombinedChartRenderer(this, mAnimator, mViewPortHandler);
        mLeftMarkerView = new KLineMarkerView(getContext());
        mRightMarkerView = new KLineMarkerView(getContext());
        mBottomMarkerView = new KLineMarkerView(getContext());
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        // if there is no marker view or drawing marker is disabled
        if ((mLeftMarkerView == null && mRightMarkerView == null && mBottomMarkerView == null)
                || !isDrawMarkersEnabled() || !valuesToHighlight()) {
            return;
        }

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByHighlight(highlight);

            Entry entry = mData.getEntryForHighlight(highlight);
            //Log.d(TAG, "drawMarkers entry:" + entry);
            if (entry == null)
                continue;

            int entryIndex = set.getEntryIndex(entry);

            // make sure entry not null
            if (entryIndex > set.getEntryCount() * mAnimator.getPhaseX())
                continue;

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                continue;

            //Log.d(TAG, "drawMarkers entry:" + entry);
            KLineDataModel kLineDataModel = mManager.getKLineDatas().get((int) mIndicesToHighlight[i].getX());
            String tip = getContext().getString(R.string.close);
            tip += ":" + (float)kLineDataModel.getClose();
            tip += getContext().getString(R.string.open);
            tip += ":" + (float)kLineDataModel.getOpen() + "\n";
            tip += getContext().getString(R.string.highest);
            tip += ":" + (float)kLineDataModel.getHigh();
            tip += getContext().getString(R.string.lowest);
            tip += ":" + (float)kLineDataModel.getLow();
            if (pos[0] >= CommonUtil.getWindowWidth(getContext()) / 2) {//高亮点在右边,显示 mLeftMarkerView
                mLeftMarkerView.setData(tip);
                mLeftMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mLeftMarkerView.layout(0, 0, mLeftMarkerView.getMeasuredWidth(), mLeftMarkerView.getMeasuredHeight());
                if (getAxisLeft().getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                    Log.d(TAG, "mViewPortHandler.contentLeft():" + mViewPortHandler.contentLeft());
                    Log.d(TAG, "mLeftMarkerView.getWidth()):" + mLeftMarkerView.getWidth());
                    Log.d(TAG, "mLeftMarkerView.getMeasuredWidth()):" + mLeftMarkerView.getMeasuredWidth());
                    mLeftMarkerView.draw(canvas,
                            mViewPortHandler.contentLeft()/* - mLeftMarkerView.getWidth() / 2*/,
                            pos[1] - mLeftMarkerView.getHeight() / 2);
                } else {
                    mLeftMarkerView.draw(canvas,
                            mViewPortHandler.contentLeft() + mLeftMarkerView.getWidth() / 2,
                            pos[1] - mLeftMarkerView.getHeight() / 2);
                }
            } else {//高亮点在左边显示 mRightMarkerView
                mRightMarkerView.setData(tip);
                mRightMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mRightMarkerView.layout(0, 0, mRightMarkerView.getMeasuredWidth(), mRightMarkerView.getMeasuredHeight());// - myMarkerViewRight.getHeight() / 2
                if (getAxisRight().getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                    mRightMarkerView.draw(canvas,
                            mViewPortHandler.contentRight() - mRightMarkerView.getWidth() / 2,
                            pos[1] - mRightMarkerView.getHeight() / 2);
                } else {
                    mRightMarkerView.draw(canvas,
                            mViewPortHandler.contentRight() - mRightMarkerView.getWidth(),
                            pos[1] - mRightMarkerView.getHeight() / 2);
                }
            }

            //显示 mBottomMarkerView
            mBottomMarkerView.setData(mManager.getxVals().get((int) mIndicesToHighlight[i].getX()));
            mBottomMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mBottomMarkerView.layout(0, 0, mBottomMarkerView.getMeasuredWidth(),
                    mBottomMarkerView.getMeasuredHeight());
            int width = mBottomMarkerView.getWidth() / 2;
            if (mViewPortHandler.contentRight() - pos[0] <= width) {
                mBottomMarkerView.draw(canvas, mViewPortHandler.contentRight()- width*2,
                        mViewPortHandler.contentBottom());
            } else if (pos[0] - mViewPortHandler.contentLeft() <= width) {
                mBottomMarkerView.draw(canvas, mViewPortHandler.contentLeft(),
                        mViewPortHandler.contentBottom());
            } else {
                mBottomMarkerView.draw(canvas, pos[0] - width,
                        mViewPortHandler.contentBottom());
            }
        }
    }

    public void setData(KLineDataManager manager, CombinedData candleChartData) {
        super.setData(candleChartData);
        mManager = manager;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        fixChartMemoryLeaks();
    }

    private void fixChartMemoryLeaks() {
        // Fix https://github.com/PhilJay/MPAndroidChart/issues/2238
        MoveViewJob.getInstance(null, 0, 0, null, null);

        // the same issue with ZoomJob
        ZoomJob.getInstance(null, 0, 0, 0, 0,
                null, null, null);
    }
}
