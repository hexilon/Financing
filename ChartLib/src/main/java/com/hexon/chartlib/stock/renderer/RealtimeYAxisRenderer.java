package com.hexon.chartlib.stock.renderer;

import android.graphics.Canvas;
import android.graphics.Color;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hexon.chartlib.stock.utils.NumberUtils;

/**
 * 为每个label设置颜色
 */
public class RealtimeYAxisRenderer extends YAxisRenderer {
    private String TAG = "RealtimeYAxisRenderer";
    private int mPositiveColor = Color.RED, mNegativeColor = Color.GREEN;
    private float mOpenPrice = 0f;//今开价

    public RealtimeYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
    }

    /**
     * 给每个label单独设置颜色
     */
    public void setLabelColor(int positiveColor, int negativeColor) {
        mPositiveColor = positiveColor;
        mNegativeColor = negativeColor;
    }

    /**
     * 今开价
     *
     * @param openPrice
     */
    public void setOpenPrice(float openPrice) {
        mOpenPrice = openPrice;
    }

    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {
        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);
        int originalColor = mAxisLabelPaint.getColor();
        // draw
        for (int i = from; i < to; i++) {
            String text = mYAxis.getFormattedLabel(i);
            //Log.d(TAG, "Label:" + text + " mOpenPrice:" + mOpenPrice);
            if ((!text.endsWith("%") && NumberUtils.String2Float(text) > mOpenPrice)
                    || (text.endsWith("%") && NumberUtils.String2Float(text.substring(0, text.length() - 1)) > 0)) {
                mAxisLabelPaint.setColor(mPositiveColor);
            } else if ((!text.endsWith("%") && NumberUtils.String2Float(text) < mOpenPrice)
                    || (text.endsWith("%") && NumberUtils.String2Double(text.substring(0, text.length() - 1)) < 0)) {
                mAxisLabelPaint.setColor(mNegativeColor);
            } else {
                mAxisLabelPaint.setColor(originalColor);
            }

            c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
        }
    }
}
