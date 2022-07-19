package com.hexon.chartlib.stock.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hexon.chartlib.stock.utils.NumberUtils;

import java.util.List;

/**
 * @author Hexh
 * @date 2019-07-08 20:50
 */
public class StockCandleChartRenderer extends CandleStickChartRenderer {
    private float mOffSet = 0.5f;
    private float[] mShadowBuffers = new float[8];
    private float[] mBodyBuffers = new float[4];
    private float[] mRangeBuffers = new float[4];
    private float[] mOpenBuffers = new float[4];
    private float[] mCloseBuffers = new float[4];

    public StockCandleChartRenderer(CandleDataProvider chart, ChartAnimator animator,
                                    ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @SuppressWarnings("ResourceAsColor")
    protected void drawDataSet(Canvas c, ICandleDataSet dataSet) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();

        mXBounds.set(mChart, dataSet);

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the body
        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

            // get the entry
            CandleEntry e = dataSet.getEntryForIndex(j);

            if (e == null)
                continue;

            final float xPos = e.getX();

            final float open = e.getOpen();
            final float close = e.getClose();
            final float high = e.getHigh();
            final float low = e.getLow();

            if (showCandleBar) {
                // calculate the shadow
                mShadowBuffers[0] = xPos;
                mShadowBuffers[2] = xPos;
                mShadowBuffers[4] = xPos;
                mShadowBuffers[6] = xPos;

                if (open > close) {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = open * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = close * phaseY;
                } else if (open < close) {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = close * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = open * phaseY;
                } else {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = open * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = mShadowBuffers[3];
                }

                trans.pointValuesToPixel(mShadowBuffers);

                // draw the shadows
                if (dataSet.getShadowColorSameAsCandle()) {
                    if (open > close) {
                        mRenderPaint.setColor(
                                dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getDecreasingColor()
                        );
                    } else if (open < close) {
                        mRenderPaint.setColor(
                                dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getIncreasingColor()
                        );
                    } else {
                        mRenderPaint.setColor(
                                dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getNeutralColor()
                        );
                    }
                } else {
                    mRenderPaint.setColor(
                            dataSet.getShadowColor() == ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getShadowColor()
                    );
                }

                mRenderPaint.setStyle(Paint.Style.STROKE);
                c.drawLines(mShadowBuffers, mRenderPaint);

                // calculate the body
                mBodyBuffers[0] = xPos - mOffSet + barSpace;
                mBodyBuffers[1] = close * phaseY;
                mBodyBuffers[2] = xPos + mOffSet - barSpace;
                mBodyBuffers[3] = open * phaseY;
                /*Log.d("Hexh", "mBodyBuffers0:" + mBodyBuffers[0]
                        + " mBodyBuffers1:" + mBodyBuffers[1]
                        + " mBodyBuffers2:" + mBodyBuffers[2]
                        + " mBodyBuffers3:" + mBodyBuffers[3]);*/

                trans.pointValuesToPixel(mBodyBuffers);

                // draw body differently for increasing and decreasing entry
                if (open > close) { // decreasing
                    if (dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getDecreasingColor());
                    }

                    mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                    c.drawRect(
                            mBodyBuffers[0], mBodyBuffers[3],
                            mBodyBuffers[2], mBodyBuffers[1],
                            mRenderPaint);
                } else if (open < close) {
                    if (dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getIncreasingColor());
                    }

                    mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                    c.drawRect(
                            mBodyBuffers[0], mBodyBuffers[1],
                            mBodyBuffers[2], mBodyBuffers[3],
                            mRenderPaint);
                } else { // equal values
                    if (dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getNeutralColor());
                    }
                    c.drawLine(
                            mBodyBuffers[0], mBodyBuffers[1],
                            mBodyBuffers[2], mBodyBuffers[3],
                            mRenderPaint);
                }
            } else {
                mRangeBuffers[0] = xPos;
                mRangeBuffers[1] = high * phaseY;
                mRangeBuffers[2] = xPos;
                mRangeBuffers[3] = low * phaseY;

                mOpenBuffers[0] = xPos - mOffSet + barSpace;
                mOpenBuffers[1] = open * phaseY;
                mOpenBuffers[2] = xPos;
                mOpenBuffers[3] = open * phaseY;

                mCloseBuffers[0] = xPos + mOffSet - barSpace;
                mCloseBuffers[1] = close * phaseY;
                mCloseBuffers[2] = xPos;
                mCloseBuffers[3] = close * phaseY;

                trans.pointValuesToPixel(mRangeBuffers);
                trans.pointValuesToPixel(mOpenBuffers);
                trans.pointValuesToPixel(mCloseBuffers);

                // draw the ranges
                int barColor;

                if (open > close)
                    barColor = dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getDecreasingColor();
                else if (open < close)
                    barColor = dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getIncreasingColor();
                else
                    barColor = dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getNeutralColor();

                mRenderPaint.setColor(barColor);
                c.drawLine(
                        mRangeBuffers[0], mRangeBuffers[1],
                        mRangeBuffers[2], mRangeBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mOpenBuffers[0], mOpenBuffers[1],
                        mOpenBuffers[2], mOpenBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mCloseBuffers[0], mCloseBuffers[1],
                        mCloseBuffers[2], mCloseBuffers[3],
                        mRenderPaint);
            }
        }
}

    @Override
    public void drawValues(Canvas c) {
        super.drawValues(c);
        drawMinMaxValue(c);
    }

    private void drawMinMaxValue(Canvas c) {
        Paint minMaxPaint = mValuePaint;
        minMaxPaint.setColor(Color.parseColor("#FFFFFF"));
        List<ICandleDataSet> dataSets = mChart.getCandleData().getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {
            ICandleDataSet dataSet = dataSets.get(i);

            if (!shouldDrawValues(dataSet) || dataSet.getEntryCount() < 1)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            mXBounds.set(mChart, dataSet);

            float[] positions = trans.generateTransformedValuesCandle(
                    dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), mXBounds.min, mXBounds.max);

            float yOffset = Utils.convertDpToPixel(5f);

            ValueFormatter formatter = dataSet.getValueFormatter();

            MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

            //计算最大值和最小值
            float maxValue = dataSet.getEntryForIndex(mXBounds.min).getHigh();
            float minValue = dataSet.getEntryForIndex(mXBounds.min).getLow();
            int maxIndex = 0, minIndex = 0;
            CandleEntry maxEntry = dataSet.getEntryForIndex(mXBounds.min);
            CandleEntry minEntry = dataSet.getEntryForIndex(mXBounds.min);

            for (int j = 0; j < positions.length; j += 2) {

                float x = positions[j];
                float y = positions[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x))
                    break;

                if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                    continue;

                CandleEntry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);

                if (entry.getHigh() > maxValue) {
                    maxValue = entry.getHigh();
                    maxIndex = j;
                    maxEntry = entry;
                }

                if (entry.getLow() < minValue) {
                    minValue = entry.getLow();
                    minIndex = j;
                    minEntry = entry;
                }
            }

            //绘制最小值
            if (maxIndex > minIndex) {
                //画右边
                String highString = NumberUtils.keepPrecisionR(minValue, 3);

                //计算显示位置
                //计算文本宽度
                int highStringWidth = Utils.calcTextWidth(minMaxPaint, "← " + highString);
                int highStringHeight = Utils.calcTextHeight(minMaxPaint, "← " + highString);

                float[] tPosition = new float[2];
                tPosition[0] = minEntry == null ? 0f : minEntry.getX();
                tPosition[1] = minEntry == null ? 0f : minEntry.getLow();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] + highStringWidth / 2 > mViewPortHandler.contentRight()) {
                    c.drawText(highString + " →", tPosition[0] - highStringWidth / 2,
                            tPosition[1] + highStringHeight / 2, minMaxPaint);
                } else {
                    c.drawText("← " + highString, tPosition[0] + highStringWidth / 2,
                            tPosition[1] + highStringHeight / 2, minMaxPaint);
                }
            } else {
                //画左边
                String highString = NumberUtils.keepPrecisionR(minValue, 3);

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(minMaxPaint, highString + " →");
                int highStringHeight = Utils.calcTextHeight(minMaxPaint, highString + " →");

                float[] tPosition = new float[2];
                tPosition[0] = minEntry == null ? 0f : minEntry.getX();
                tPosition[1] = minEntry == null ? 0f : minEntry.getLow();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] - highStringWidth / 2 < mViewPortHandler.contentLeft()) {
                    c.drawText("← " + highString, tPosition[0] + highStringWidth / 2,
                            tPosition[1] + highStringHeight / 2, minMaxPaint);
                } else {
                    c.drawText(highString + " →", tPosition[0] - highStringWidth / 2,
                            tPosition[1] + highStringHeight / 2, minMaxPaint);
                }
            }

            //绘制最大值
            if (maxIndex > minIndex) {
                //画左边
                String highString = NumberUtils.keepPrecisionR(maxValue, 3);

                int highStringWidth = Utils.calcTextWidth(minMaxPaint, highString + " →");
                int highStringHeight = Utils.calcTextHeight(minMaxPaint, highString + " →");

                float[] tPosition = new float[2];
                tPosition[0] = maxEntry == null ? 0f : maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f : maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);
                if ((tPosition[0] - highStringWidth / 2) < mViewPortHandler.contentLeft()) {
                    c.drawText("← " + highString, tPosition[0] + highStringWidth / 2,
                            tPosition[1] + highStringHeight / 2, minMaxPaint);
                } else {
                    c.drawText(highString + " →", tPosition[0] - highStringWidth / 2,
                            tPosition[1] + highStringHeight / 2, minMaxPaint);
                }
            } else {
                //画右边
                String highString = NumberUtils.keepPrecisionR(maxValue, 3);

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(minMaxPaint, "← " + highString);
                int highStringHeight = Utils.calcTextHeight(minMaxPaint, "← " + highString);

                float[] tPosition = new float[2];
                tPosition[0] = maxEntry == null ? 0f : maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f : maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] + highStringWidth / 2 > mViewPortHandler.contentRight()) {
                    c.drawText(highString + " →", tPosition[0] - highStringWidth / 2,
                            tPosition[1] + highStringHeight / 2, minMaxPaint);
                } else {
                    c.drawText("← " + highString, tPosition[0] + highStringWidth / 2,
                            tPosition[1] + highStringHeight / 2, minMaxPaint);
                }
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        CandleData candleData = mChart.getCandleData();

        for (Highlight high : indices) {

            ICandleDataSet set = candleData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            CandleEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            float lowValue = e.getLow() * mAnimator.getPhaseY();
            float highValue = e.getHigh() * mAnimator.getPhaseY();
            //float y = (lowValue + highValue) / 2f;
            float y = e.getClose() * mAnimator.getPhaseY();

            MPPointD pix = mChart.getTransformer(
                    set.getAxisDependency()).getPixelForValues(e.getX(), y);

            high.setDraw((float) pix.x, (float) pix.y);

            // draw the lines
            drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
        }
    }
}
