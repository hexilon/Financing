package com.hexon.chartlib.stock.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.hexon.chartlib.stock.renderer.StockCombinedChartRenderer;

/**
 * @author Hexh
 * @date 2019-08-12 15:07
 */
public class IndexChart extends CombinedChart {
    public IndexChart(Context context) {
        super(context);
    }

    public IndexChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new StockCombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }
}
