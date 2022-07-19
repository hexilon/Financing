package com.hexon.chartlib.stock.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;


public class TimeXAxisRenderer extends XAxisRenderer {
    private final BarLineChartBase mChart;
    protected XAxis mXAxis;

    public TimeXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, BarLineChartBase chart) {
        super(viewPortHandler, xAxis, trans);
        mXAxis = xAxis;
        mChart = chart;
    }

    @Override
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {
            super.drawLabels(c, pos, anchor);
//        float[] position = new float[]{0f, 0f};
//        int count = 13;//mXAxis.getXLabels().size();
//        for (int i = 0; i < count; i++) {
//            /*获取label对应key值，也就是x轴坐标0,60,121,182,242*/
//            int ix = i;//mXAxis.getXLabels().keyAt(i);
//            if (mXAxis.isCenterAxisLabelsEnabled()) {
//                //float offset = mXAxis.getXLabels().keyAt(count - 1) / (count - 1);
//                //position[0] = ix + offset / 2;
//            } else {
//                position[0] = ix;
//            }
//            /*在图表中的x轴转为像素，方便绘制text*/
//            mTrans.pointValuesToPixel(position);
//            /*x轴越界*/
////            if (mViewPortHandler.isInBoundsX(position[0])) {
//            String label = "";//mXAxis.getXLabels().valueAt(i);
//            /*文本长度*/
//            int labelWidth = Utils.calcTextWidth(mAxisLabelPaint, label);
//            /*右出界*/
//            if ((labelWidth / 2 + position[0]) > mChart.getViewPortHandler().contentRight()) {
//                position[0] = mChart.getViewPortHandler().contentRight() - labelWidth / 2;
//            } else if ((position[0] - labelWidth / 2) < mChart.getViewPortHandler().contentLeft()) {//左出界
//                position[0] = mChart.getViewPortHandler().contentLeft() + labelWidth / 2;
//            }
//            c.drawText(label, position[0], pos + Utils.convertPixelsToDp(mChart.getViewPortHandler().offsetBottom() + 10),
//                    mAxisLabelPaint);
////            }
//        }
    }


    /*x轴垂直线*/
    @Override
    public void renderGridLines(Canvas c) {
        super.renderGridLines(c);
    }

}
