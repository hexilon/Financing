package com.hexon.chartlib.stock.charts;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.hexon.chartlib.R;
import com.hexon.chartlib.stock.data.RealtimeDataManager;
import com.hexon.chartlib.stock.model.RealtimeEntity;
import com.hexon.chartlib.stock.renderer.RealtimeYAxisRenderer;
import com.hexon.chartlib.stock.renderer.TimeXAxisRenderer;
import com.hexon.chartlib.stock.utils.CommonUtil;
import com.hexon.chartlib.stock.view.BaseStockView;
import com.hexon.chartlib.stock.view.RealtimeMarkerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by loro on 2017/2/8.
 */
public class RealtimeChart extends LineChart implements BaseStockView {
    private final String TAG = "RealtimeChart";
    private RealtimeMarkerView mLeftMarkerView;
    private RealtimeMarkerView mRightMarkerView;
    private RealtimeMarkerView mBottomMarkerView;
    public boolean mLandscape = false;//是否横屏模式
    public int mPrecision = 2;//小数精度
    RealtimeDataManager mManager;
    private RealtimeYAxisRenderer mLeftRealtimeYAxisRenderer, mRightRealtimeYAxisRenderer;

    public RealtimeChart(Context context) {
        super(context);
    }

    public RealtimeChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RealtimeChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mLandscape = true;
        }else{
            mLandscape = false;
        }
    }

    @Override
    protected void init() {
        super.init();
        Log.d(TAG, "init");
        //底X轴label渲染
        mXAxisRenderer = new TimeXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer, this);

        mManager = new RealtimeDataManager(getContext());

        //X轴
        XAxis xAxisLine = getXAxis();
        xAxisLine.setDrawAxisLine(true);
        xAxisLine.setDrawGridLines(false);
        xAxisLine.setLabelCount(5, true);
        xAxisLine.setTextColor(ContextCompat.getColor(getContext(), R.color.label_text));
        xAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisLine.setAvoidFirstLastClipping(true);
        xAxisLine.setGridColor(ContextCompat.getColor(getContext(), R.color.grid_color));
        xAxisLine.setGridLineWidth(0.7f);
        xAxisLine.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss",//yyyy-MM-dd
                        Locale.getDefault());
                long lcc = Long.valueOf((long)value *1000);
                return format.format(new Date(lcc));
            }
        });

        //左Y轴
        YAxis leftYAxis = getAxisLeft();
        leftYAxis.setLabelCount(5, true);
        leftYAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.label_text));
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setDrawAxisLine(true);
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftYAxis.setValueFormatter(new DefaultAxisValueFormatter(mPrecision));
        //左Y轴label渲染颜色
        Transformer leftYTransformer = getRendererLeftYAxis().getTransformer();
        mLeftRealtimeYAxisRenderer =
                new RealtimeYAxisRenderer(getViewPortHandler(), leftYAxis, leftYTransformer);
        mLeftRealtimeYAxisRenderer.setLabelColor(ContextCompat.getColor(getContext(), R.color.positive),
                ContextCompat.getColor(getContext(), R.color.negative));
        setRendererLeftYAxis(mLeftRealtimeYAxisRenderer);

        //右Y轴
        YAxis rightYAxis = getAxisRight();
        rightYAxis.setLabelCount(5, true);
        rightYAxis.setCenterAxisLabels(true);
        rightYAxis.setDrawGridLines(true);
        rightYAxis.setGridLineWidth(0.7f);
        rightYAxis.enableGridDashedLine(CommonUtil.dip2px(getContext(), 4),
                CommonUtil.dip2px(getContext(), 3), 0);
        rightYAxis.setDrawAxisLine(true);
        rightYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightYAxis.setGridColor(ContextCompat.getColor(getContext(), R.color.grid_color));
        rightYAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.label_text));
        rightYAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat mFormat = new DecimalFormat("#0.00%");
                //Log.d(TAG, "value:" + value + " mFormat.format(value)" + mFormat.format(value));
                return mFormat.format(value);
            }
        });
        //左Y轴label渲染颜色
        Transformer rightYTransformer = getRendererRightYAxis().getTransformer();
        mRightRealtimeYAxisRenderer =
                new RealtimeYAxisRenderer(getViewPortHandler(), rightYAxis, rightYTransformer);
        mRightRealtimeYAxisRenderer.setLabelColor(ContextCompat.getColor(getContext(), R.color.positive),
                ContextCompat.getColor(getContext(), R.color.negative));
        setRendererRightYAxis(mRightRealtimeYAxisRenderer);

        mLeftMarkerView = new RealtimeMarkerView(getContext(), RealtimeMarkerView.Postion.LEFT);
        mRightMarkerView = new RealtimeMarkerView(getContext(), RealtimeMarkerView.Postion.RIGHT);
        mBottomMarkerView = new RealtimeMarkerView(getContext(), RealtimeMarkerView.Postion.BOTTOM);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        /*Log.d(TAG, "onAttachedToWindow isDoubleTapToZoomEnabled:" + isDoubleTapToZoomEnabled()
                + " isScaleXEnabled:" + isScaleXEnabled()
                + " isScaleYEnabled:" + isScaleYEnabled());*/
        //放在init中无效,只能放在onAttachedToWindow函数中
        setBackgroundResource(com.hexon.util.R.color.black);
        setNoDataText(getResources().getString(R.string.loading));
        setDrawBorders(true);
        setBorderColor(ContextCompat.getColor(getContext(), R.color.border_color));
        setBorderWidth(1.5f);

        Legend lineChartLegend = getLegend();
        lineChartLegend.setEnabled(false);

        getDescription().setEnabled(false);

        setTouchEnabled(true);
        setDragEnabled(true);
        //setScaleEnabled(false);
        setScaleXEnabled(true);
        setScaleYEnabled(false);
        setDoubleTapToZoomEnabled(false);
    }

    public void setMarker(RealtimeMarkerView markerLeft, RealtimeMarkerView markerRight, RealtimeMarkerView markerBottom) {
        this.mLeftMarkerView = markerLeft;
        this.mRightMarkerView = markerRight;
        this.mBottomMarkerView = markerBottom;
    }

    public void setData(LineData dataSet) {
        super.setData(dataSet);
    }

    public void setHighlightValue(Highlight h) {
        if (mData == null)
            mIndicesToHighlight = null;
        else {
            mIndicesToHighlight = new Highlight[]{h};
        }
        invalidate();
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if ((mLeftMarkerView == null && mRightMarkerView == null && mBottomMarkerView == null)
                || !isDrawMarkersEnabled() || !valuesToHighlight()) {
            return;
        }

        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            //Log.d(TAG, "highlight[" + i + "]:" + highlight);
            int dataSetIndex = mIndicesToHighlight[i].getDataSetIndex();
            float deltaX = mXAxis != null
                    ? mXAxis.mAxisRange
                    : ((mData == null ? 0.f : mData.getEntryCount()) - 1.f);
                Entry entry = mData.getEntryForHighlight(mIndicesToHighlight[i]);
                Log.d(TAG, "entry:" + entry);
                // make sure entry not null
                if (entry == null)
                    continue;

                float[] pos = getMarkerPosition(highlight);
                // check bounds
                if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                    continue;

                mLeftMarkerView.setData("" + highlight.getY());
                mRightMarkerView.setPercentData(mManager.getOpenPrice(), entry);
                mBottomMarkerView.setData(entry);//time
                mLeftMarkerView.refreshContent(entry, mIndicesToHighlight[i]);
                mRightMarkerView.refreshContent(entry, mIndicesToHighlight[i]);
                mBottomMarkerView.refreshContent(entry, mIndicesToHighlight[i]);
                /*修复bug*/
                // invalidate();
                /*重新计算大小*/
                mLeftMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mLeftMarkerView.layout(0, 0, mLeftMarkerView.getMeasuredWidth(),
                        mLeftMarkerView.getMeasuredHeight());
                mRightMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mRightMarkerView.layout(0, 0, mRightMarkerView.getMeasuredWidth(),
                        mRightMarkerView.getMeasuredHeight());
                mBottomMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mBottomMarkerView.layout(0, 0, mBottomMarkerView.getMeasuredWidth(),
                        mBottomMarkerView.getMeasuredHeight());

                mBottomMarkerView.draw(canvas,
                        pos[0]-mBottomMarkerView.getWidth()/2,
                        mViewPortHandler.contentBottom());
                mLeftMarkerView.draw(canvas,
                        mViewPortHandler.contentLeft() - mLeftMarkerView.getWidth(),
                        pos[1] - mLeftMarkerView.getHeight() / 2);
                mRightMarkerView.draw(canvas,
                        mViewPortHandler.contentRight(),
                        pos[1] - mRightMarkerView.getHeight() / 2);
        }
    }

    public void addEntry(RealtimeEntity entity) {
        Log.d(TAG, "addEntry entity:" + entity);
        LineData data = getData();
        if (data == null) {
            return;
        }

        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            return;
        }

        data.addEntry(new Entry(entity.mTime.getTimeInMillis()/1000, entity.mPrice), 0);
        data.notifyDataChanged();
        notifyDataSetChanged();
        invalidate();
    }

    public void setDataSet(List<RealtimeEntity> realtimeList) {
        if (realtimeList.size() <= 0) {//周末无信息
            setNoDataText(getResources().getString(R.string.no_data));
            return;
        }

        mManager.setData(realtimeList);

        Log.d(TAG,"realtimeList.size:" + realtimeList.size()
                + " mManager.getMin():" + mManager.getMin()
                + " mManager.getMax():" + mManager.getMax()
                + " mManager.getPercentMin():" + mManager.getPercentMin()
                + " mManager.getPercentMax():" + mManager.getPercentMax()
        );
        mAxisLeft.setAxisMinimum(mManager.getMin());
        mAxisLeft.setAxisMaximum(mManager.getMax());
        mLeftRealtimeYAxisRenderer.setOpenPrice(mManager.getOpenPrice());

        mAxisRight.setAxisMinimum(mManager.getPercentMin());
        mAxisRight.setAxisMaximum(mManager.getPercentMax());
        mRightRealtimeYAxisRenderer.setOpenPrice(mManager.getOpenPrice());

        LineDataSet set;
        ArrayList<Entry> Vals = new ArrayList<Entry>();
        float valX, valY;

        for (int i = 0; i < realtimeList.size(); i++) {
            valX = realtimeList.get(i).mTime.getTimeInMillis()/1000;
            valY = realtimeList.get(i).mPrice;
            //Log.d(TAG, "index:" + i + " valX:" + valX + " valY:" + valY);
            Vals.add(new Entry(valX, valY));
        }

        //Log.d(TAG,"realtimeList.get(0):" + realtimeList.get(0).mTime.get(Calendar.HOUR_OF_DAY)
                //+ " realtimeData.get(0).mTime.getDay():" + realtimeList.get(0).mTime.get(Calendar.DAY_OF_WEEK));
        float LimitLineY = realtimeList.get(0).mPrice;
        if (realtimeList.get(realtimeList.size()-1).mTime.get(Calendar.HOUR_OF_DAY) >= 6
                && realtimeList.get(realtimeList.size()-1).mTime.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {//monday
            LimitLineY = mManager.getFiveClockPrice();
        }
        //Log.d(TAG, "LimitLineY:" + LimitLineY);
        //设置开盘线
        LimitLine ll = new LimitLine(LimitLineY, "5:00 AM:"+LimitLineY);
        ll.setLineWidth(2f);
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextSize(16f);
        ll.setTextColor(Color.RED);
        getAxisLeft().removeAllLimitLines();
        getAxisLeft().addLimitLine(ll);
        getAxisLeft().setDrawLimitLinesBehindData(false);

        if (getData() != null &&
                getData().getDataSetCount() > 0) {
            set = (LineDataSet)getData().getDataSetByIndex(0);
            set.setValues(Vals);
            getData().notifyDataChanged();
            notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set = new LineDataSet(Vals, "Realtime");
            set.setDrawFilled(true);
            //set.setCubicIntensity(0.2f);
            set.setDrawCircles(false);
            set.setLineWidth(1.0f);
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setColor(Color.WHITE);
            set.setFillColor(Color.rgb(112, 128, 144));
            set.setFillAlpha(60);
            //set.setDrawHorizontalHighlightIndicator(false);//水平高亮线
            set.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(set);
            //data.setValueTypeface(mTfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            setData(data);
            Calendar calendar = realtimeList.get(0).mTime;
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            getXAxis().setAxisMaximum(calendar.getTimeInMillis()/1000 + 24*60*60);
            invalidate();
        }
    }

    public void resetData() {
        LineData data = getData();

        if (data != null) {
            data.removeDataSet(data.getDataSetByIndex(0));
            setNoDataText(getResources().getString(R.string.loading));
            notifyDataSetChanged();
            invalidate();
        }
    }
}
