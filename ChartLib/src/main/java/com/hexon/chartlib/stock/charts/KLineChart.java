package com.hexon.chartlib.stock.charts;

import static com.hexon.chartlib.stock.data.KLineDataManager.Index.BOLL;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.material.tabs.TabLayout;
import com.hexon.chartlib.R;
import com.hexon.chartlib.stock.data.KLineDataManager;
import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.chartlib.stock.utils.CommonUtil;
import com.hexon.chartlib.stock.utils.NumberUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Hexh
 * @date 2019-07-06 13:58
 */
public class KLineChart extends LinearLayout implements TabLayout.BaseOnTabSelectedListener {
    final String TAG = "KLineChart";
    CandleChart mCandleChart;
    IndexChart mIndexChart;
    List<ILineDataSet> mIndexLineSet = new ArrayList<>();
    public int mPrecision = 2;//小数精度
    KLineDataManager mManager;
    ArrayList<String> mIndexList = new ArrayList<>();
    float mLeftOffset, mRightOffset;
    private boolean mIsFirst = true;//是否是第一次加载数据
    KLineDataManager.Index mCurrIndex = KLineDataManager.Index.MACD;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage");
            mCandleChart.setAutoScaleMinMaxEnabled(true);
            mIndexChart.setAutoScaleMinMaxEnabled(true);
            mCandleChart.notifyDataSetChanged();
            mIndexChart.notifyDataSetChanged();
            mCandleChart.invalidate();
            mIndexChart.animateY(1000);
        }
    };

    public KLineChart(Context context) {
        this(context, null);
    }

    public KLineChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_kline_chart, this);
        mCandleChart = (CandleChart)findViewById(R.id.candle_chart);
        mIndexChart = (IndexChart) findViewById(R.id.index_chart);
        initChart();
        mManager = new KLineDataManager(getContext());
    }

    private void initChart() {
        //蜡烛图
        mCandleChart.getDescription().setEnabled(false);
        mCandleChart.setDrawBorders(true);
        mCandleChart.setBorderWidth(1.5f);
        mCandleChart.setBorderColor(ContextCompat.getColor(getContext(), R.color.border_color));
        mCandleChart.setDragEnabled(true);
        mCandleChart.setScaleXEnabled(true);
        mCandleChart.setScaleYEnabled(false);
        mCandleChart.setHardwareAccelerationEnabled(true);
        mCandleChart.setMaxVisibleValueCount(60);
        Legend chartKlineLegend = mCandleChart.getLegend();
        chartKlineLegend.setDrawInside(false);
        chartKlineLegend.setTextColor(Color.WHITE);
        chartKlineLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        chartKlineLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        mCandleChart.setDragDecelerationEnabled(true);
        mCandleChart.setDragDecelerationFrictionCoef(0.6f);//0.92持续滚动时的速度快慢，[0,1) 0代表立即停止。
        mCandleChart.setDoubleTapToZoomEnabled(false);
        mCandleChart.setNoDataText(getResources().getString(R.string.loading));

        //蜡烛图X轴
        XAxis xAxisCandle = mCandleChart.getXAxis();
        xAxisCandle.setDrawLabels(true);
        xAxisCandle.setLabelCount(5, true);
        xAxisCandle.setDrawGridLines(true);
        xAxisCandle.setDrawAxisLine(false);
        xAxisCandle.setGridLineWidth(0.7f);
        xAxisCandle.setGridColor(ContextCompat.getColor(getContext(), R.color.grid_color));
        xAxisCandle.setTextColor(ContextCompat.getColor(getContext(), R.color.label_text));
        xAxisCandle.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisCandle.setAvoidFirstLastClipping(true);
        xAxisCandle.setDrawLimitLinesBehindData(true);
        xAxisCandle.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                //Log.d(TAG, "getFormattedValue value:" + value);
                if (mManager != null) {
                    int index = (int)value;
                    if (index >= 0 && index < mManager.getxVals().size()) {
                        return mManager.getxVals().get((int) value);
                    }
                }

                return "" + value;
            }
        });

        //蜡烛图左Y轴
        YAxis axisLeftCandle = mCandleChart.getAxisLeft();
        axisLeftCandle.setDrawGridLines(true);
        axisLeftCandle.setDrawAxisLine(false);
        axisLeftCandle.setDrawLabels(true);
        axisLeftCandle.setLabelCount(5, true);
        axisLeftCandle.enableGridDashedLine(CommonUtil.dip2px(getContext(), 4),
                CommonUtil.dip2px(getContext(), 3), 0);
        axisLeftCandle.setTextColor(ContextCompat.getColor(getContext(), R.color.axis_text));
        axisLeftCandle.setGridColor(ContextCompat.getColor(getContext(), R.color.grid_color));
        axisLeftCandle.setGridLineWidth(0.7f);
        axisLeftCandle.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        axisLeftCandle.setValueFormatter(new DefaultAxisValueFormatter(mPrecision));

        //蜡烛图右Y轴
        YAxis axisRightCandle = mCandleChart.getAxisRight();
        axisRightCandle.setDrawLabels(false);
        axisRightCandle.setDrawGridLines(false);
        axisRightCandle.setDrawAxisLine(false);
        axisRightCandle.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        //指标TAB栏
        mIndexList.add(getResources().getString(R.string.macd));
        mIndexList.add(getResources().getString(R.string.kdj));
        mIndexList.add(getResources().getString(R.string.rsi));
        mIndexList.add(getResources().getString(R.string.boll));
        mIndexList.add(getResources().getString(R.string.cci));
        mIndexList.add(getResources().getString(R.string.ema));
        TabLayout tab = findViewById(R.id.tab_layout);
        tab.addOnTabSelectedListener(this);

        //指标图
        mIndexChart.getDescription().setEnabled(false);
        mIndexChart.setDrawBorders(true);
        mIndexChart.setBorderWidth(1.5f);
        mIndexChart.setBorderColor(ContextCompat.getColor(getContext(), R.color.border_color));
        mIndexChart.setDragEnabled(true);
        mIndexChart.setScaleXEnabled(true);
        mIndexChart.setScaleYEnabled(false);
        mIndexChart.setHardwareAccelerationEnabled(true);
        Legend mChartChartsLegend = mIndexChart.getLegend();
        mChartChartsLegend.setDrawInside(true);
        mChartChartsLegend.setTextColor(Color.WHITE);
        mChartChartsLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        mChartChartsLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        mIndexChart.setDragDecelerationEnabled(true);
        mIndexChart.setDragDecelerationFrictionCoef(0.6f);//设置太快，切换滑动源滑动不同步
        mIndexChart.setDoubleTapToZoomEnabled(false);
        mIndexChart.setNoDataText(getResources().getString(R.string.loading));
        //指标图X轴
        XAxis xAxisIndex = mIndexChart.getXAxis();
        xAxisIndex.setDrawGridLines(true);
        xAxisIndex.setDrawAxisLine(false);
        xAxisIndex.setDrawLabels(false);
        xAxisIndex.setLabelCount(5, true);
        xAxisIndex.setTextColor(ContextCompat.getColor(getContext(), R.color.label_text));
        xAxisIndex.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisIndex.setGridColor(ContextCompat.getColor(getContext(), R.color.grid_color));
        xAxisIndex.setGridLineWidth(0.7f);
        xAxisIndex.setAvoidFirstLastClipping(true);
        xAxisIndex.setDrawLimitLinesBehindData(true);

        //指标图左Y轴
        YAxis axisLeftIndex = mIndexChart.getAxisLeft();
        //axisLeftIndex.setAxisMinimum(0);
        axisLeftIndex.setDrawGridLines(false);
        axisLeftIndex.setDrawAxisLine(false);
        axisLeftIndex.setTextColor(ContextCompat.getColor(getContext(), R.color.axis_text));
        axisLeftIndex.setDrawLabels(true);
        axisLeftIndex.setLabelCount(2, true);
        axisLeftIndex.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        //指标图右Y轴
        YAxis axisRightIndex = mIndexChart.getAxisRight();
        axisRightIndex.setDrawLabels(false);
        axisRightIndex.setDrawGridLines(true);
        axisRightIndex.setDrawAxisLine(false);
        axisRightIndex.setLabelCount(3, true);
        axisRightIndex.setGridColor(ContextCompat.getColor(getContext(), R.color.grid_color));
        axisRightIndex.setGridLineWidth(0.7f);
        axisRightIndex.enableGridDashedLine(
                CommonUtil.dip2px(getContext(), 4),
                CommonUtil.dip2px(getContext(), 3), 0);

        //手势联动监听
        mCandleChart.setOnChartGestureListener(new CoupleChartGestureListener(mCandleChart, new Chart[]{mIndexChart}));
        mIndexChart.setOnChartGestureListener(new CoupleChartGestureListener(mIndexChart, new Chart[]{mCandleChart}));

        mCandleChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
           @Override
           public void onValueSelected(Entry e, Highlight h) {
               Log.d(TAG, "mCandleChart Highlight:" + h.getX());
               mCandleChart.highlightValue(h);
               if (mIndexChart.getData().getBarData().getDataSets().size() != 0) {
                   Highlight highlight = new Highlight(h.getX(), h.getDataSetIndex(), h.getStackIndex());
                   highlight.setDataIndex(h.getDataIndex());
                   mIndexChart.highlightValues(new Highlight[]{highlight});
               } else {
                   Highlight highlight = new Highlight(h.getX(), 2, h.getStackIndex());
                   highlight.setDataIndex(0);
                   mIndexChart.highlightValues(new Highlight[]{highlight});
               }

               updateDescription((int)e.getX(), true);
           }

           @Override
           public void onNothingSelected() {
               mIndexChart.highlightValues(null);
               updateDescription(mManager.getKLineDatas().size() - 1, false);
           }
       });
        mIndexChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
           @Override
           public void onValueSelected(Entry e, Highlight h) {
               //Log.d(TAG, "mIndexChart Highlight:" + h.getX());
               mIndexChart.highlightValue(h);
               Highlight highlight = new Highlight(h.getX(), 0, h.getStackIndex());
               highlight.setDataIndex(1);
               mCandleChart.highlightValues(new Highlight[]{highlight});
               //Log.d(TAG, "mIndexChart h.getDataIndex():" + h.getDataIndex());
               //Log.d(TAG, "mIndexChart e.getX():" + e.getX());
               updateDescription((int)e.getX(), true);
           }

           @Override
           public void onNothingSelected() {
               mCandleChart.highlightValues(null);
               updateDescription(mManager.getKLineDatas().size() - 1, false);
           }
       });
    }

    private void updateCandleDescription(int index, boolean b) {
        Legend legend = mCandleChart.getLegend();
        LegendEntry[] entries = legend.getEntries();
        List<ILineDataSet> lineSets = mCandleChart.getLineData().getDataSets();
        for (int i = 0; i < entries.length; i ++) {
            //Log.d(TAG, "Legend label:" + entries[i].label);
            if (entries[i].label != null) {
                //ma1 线标题
                if (entries[i].label.contains(KLineDataManager.MA_HEAD_LABEL
                        + KLineDataManager.MaPeriod.MA_PERIOD_1.getValue())) {
                    int maPeriod = KLineDataManager.MaPeriod.MA_PERIOD_1.getValue();
                    entries[i].label = KLineDataManager.MA_HEAD_LABEL + maPeriod;
                    if (index >= maPeriod -1) {
                        Entry entry = lineSets.get(i).getEntryForIndex(index - maPeriod +1);
                        entries[i].label += ":" + new DecimalFormat("###,###,###.##").format(entry.getY());
                    }
                } else if (entries[i].label.contains(KLineDataManager.MA_HEAD_LABEL
                        + KLineDataManager.MaPeriod.MA_PERIOD_2.getValue())) {
                    int maPeriod = KLineDataManager.MaPeriod.MA_PERIOD_2.getValue();
                    entries[i].label = KLineDataManager.MA_HEAD_LABEL + maPeriod;
                    if (index >= maPeriod -1) {
                        Entry entry = lineSets.get(i).getEntryForIndex(index - maPeriod +1);
                        entries[i].label += ":" + new DecimalFormat("###,###,###.##").format(entry.getY());
                    }
                } else if (entries[i].label.contains(KLineDataManager.MA_HEAD_LABEL
                        + KLineDataManager.MaPeriod.MA_PERIOD_3.getValue())) {
                    int maPeriod = KLineDataManager.MaPeriod.MA_PERIOD_3.getValue();
                    entries[i].label = KLineDataManager.MA_HEAD_LABEL + maPeriod;
                    if (index >= maPeriod -1) {
                        Entry entry = lineSets.get(i).getEntryForIndex(index - maPeriod +1);
                        entries[i].label += ":" + new DecimalFormat("###,###,###.##").format(entry.getY());
                    }
                } else if (entries[i].label.contains(KLineDataManager.MA_HEAD_LABEL
                        + KLineDataManager.MaPeriod.MA_PERIOD_4.getValue())) {
                    int maPeriod = KLineDataManager.MaPeriod.MA_PERIOD_4.getValue();
                    entries[i].label = KLineDataManager.MA_HEAD_LABEL + maPeriod;
                    if (index >= maPeriod -1) {
                        Entry entry = lineSets.get(i).getEntryForIndex(index - maPeriod +1);
                        entries[i].label += ":" + new DecimalFormat("###,###,###.##").format(entry.getY());
                    }
                }
            }
        }

        legend.setEntries(Arrays.asList(entries));
        legend.calculateDimensions(mCandleChart.getLegendRenderer().getLabelPaint(),
                mCandleChart.getViewPortHandler());
    }

    private void updateIndexDescription(int index, boolean b) {
        Legend legend = mIndexChart.getLegend();
        LegendEntry[] entries = legend.getEntries();
        List<ILineDataSet> lineSets = mIndexChart.getLineData().getDataSets();

        for (int i = 0; i < lineSets.size(); i ++) {
            //Log.d(TAG, "Legend label:" + entries[i].label);
            if (entries[i].label != null) {
                if (entries[i].label.startsWith(KLineDataManager.DEA_HEAD_LABEL)) {//dea
                    entries[i].label = KLineDataManager.DEA_HEAD_LABEL;
                } else if (entries[i].label.startsWith(KLineDataManager.DIFF_HEAD_LABEL)) {//diff
                    entries[i].label = KLineDataManager.DIFF_HEAD_LABEL;
                } else if (entries[i].label.startsWith(KLineDataManager.MID_HEAD_LABEL)) {//boll MID
                    entries[i].label = KLineDataManager.MID_HEAD_LABEL;
                } else if (entries[i].label.startsWith(KLineDataManager.UPPER_HEAD_LABEL)) {//boll upper
                    entries[i].label = KLineDataManager.UPPER_HEAD_LABEL;
                } else if (entries[i].label.startsWith(KLineDataManager.LOWER_HEAD_LABEL)) {//boll lower
                    entries[i].label = KLineDataManager.LOWER_HEAD_LABEL;
                } else if (entries[i].label.startsWith(KLineDataManager.K_HEAD_LABEL)) {//k
                    entries[i].label = KLineDataManager.K_HEAD_LABEL;
                } else if (entries[i].label.startsWith(KLineDataManager.D_HEAD_LABEL)) {//d
                    entries[i].label = KLineDataManager.D_HEAD_LABEL;
                } else if (entries[i].label.startsWith(KLineDataManager.J_HEAD_LABEL)) {//j
                    entries[i].label = KLineDataManager.J_HEAD_LABEL;
                } else if (entries[i].label.startsWith(KLineDataManager.RSI_HEAD_LABEL + mManager.RSIN1)) {//rsi 1
                    entries[i].label = KLineDataManager.RSI_HEAD_LABEL + mManager.RSIN1 + ":";
                } else if (entries[i].label.startsWith(KLineDataManager.RSI_HEAD_LABEL + mManager.RSIN2)) {//rsi 2
                    entries[i].label = KLineDataManager.RSI_HEAD_LABEL + mManager.RSIN2 + ":";
                } else if (entries[i].label.startsWith(KLineDataManager.RSI_HEAD_LABEL + mManager.RSIN3)) {//rsi 3
                    entries[i].label = KLineDataManager.RSI_HEAD_LABEL + mManager.RSIN3 + ":";
                } else {
                    entries[i].label = "";
                }

                Entry entry = null;
                if (mCurrIndex == BOLL) {
                    if (index >= mManager.BOLLN -1) {
                        entry = lineSets.get(i).getEntryForIndex(index - mManager.BOLLN + 1);
                    }
                } else {
                    entry = lineSets.get(i).getEntryForIndex(index);
                }
                if (entries != null && entry != null) {
                    entries[i].label += new DecimalFormat("###,###,###.##").format(entry.getY());
                }
            }
        }

        legend.setEntries(Arrays.asList(entries));
        legend.calculateDimensions(mIndexChart.getLegendRenderer().getLabelPaint(),
                mIndexChart.getViewPortHandler());
    }

    private void updateDescription(int index, boolean b) {
        Log.d(TAG, "updateDescription index:" + index);
        //candle chart
        updateCandleDescription(index, b);

        //Index chart
        updateIndexDescription(index, b);
    }

    public void setDataSet(List<HistoryEntity> list) {
        if (list == null || list.size() == 0) {
            mCandleChart.setNoDataText(getResources().getString(R.string.no_data));
            mIndexChart.setNoDataText(getResources().getString(R.string.no_data));
            return;
        }

        mManager.setData(list);
        /****************************蜡烛图数据********************************/
        CandleDataSet candleDataSet = mManager.getCandleDataSet();
        CombinedData candleChartData = new CombinedData();
        candleChartData.setData(new CandleData(candleDataSet));
        candleChartData.setData(new LineData(mManager.getLineDataMA()));
        mCandleChart.setData(mManager, candleChartData);
        mCandleChart.getXAxis().setAxisMinimum(candleDataSet.getXMin() - 0.5f);//左边显示不全问题
        mCandleChart.getXAxis().setAxisMaximum(candleDataSet.getXMax() + 0.5f);//右边显示不全问题
        /*****************************指标数据*********************************/
        if (mIsFirst) {
            CombinedData barChartData = new CombinedData();
            if (mIndexLineSet.size() > 0) {
                mIndexLineSet.clear();
            }
            mIndexLineSet.addAll(mManager.getLineDataMACD());
            barChartData.setData(new LineData(mIndexLineSet));
            barChartData.setData(new BarData(mManager.getBarDataMACD()));
            barChartData.setData(new CandleData());
            mIndexChart.setData(barChartData);
            mIndexChart.getXAxis().setAxisMinimum(candleDataSet.getXMin() - 0.5f);//左边显示不全问题
            mIndexChart.getXAxis().setAxisMaximum(candleDataSet.getXMax() + 0.5f);//右边显示不全问题
            final ViewPortHandler viewPortHandler = mCandleChart.getViewPortHandler();
            viewPortHandler.setMaximumScaleX(culcMaxScale(list.size()));
            Matrix matrixCombin = viewPortHandler.getMatrixTouch();
            final float xscaleCombin = 2;
            matrixCombin.postScale(xscaleCombin, 1f);
            mCandleChart.moveViewToX(list.size() - 1);

            float priceWidth = Utils.calcTextWidth(mCandleChart.getRendererLeftYAxis().getPaintAxisLabels(),
                    NumberUtils.keepPrecision(mManager.getPreClosePrice() + "", mPrecision) + "#");
            Log.d(TAG, "priceWidth:" + priceWidth);
            mLeftOffset = priceWidth + 10;
            mRightOffset = priceWidth / 2;
            mCandleChart.setViewPortOffsets(mLeftOffset, CommonUtil.dip2px(getContext(), 18),
                    mRightOffset, CommonUtil.dip2px(getContext(), 18));
            mIndexChart.setViewPortOffsets(mLeftOffset, CommonUtil.dip2px(getContext(), 5),
                    mRightOffset, CommonUtil.dip2px(getContext(), 5));
            mIsFirst = false;
        } else {
            switchIndexChart(mCurrIndex);
        }
        /****************************************************************************************
         此处解决方法来源于CombinedChartDemo，k线图y轴显示问题，图表滑动后才能对齐的bug
         ****************************************************************************************/
        mHandler.sendEmptyMessageDelayed(0, 100);
    }

    private float culcMaxScale(float count) {
        float max = 1;
        max = count / 30;
        return max;
    }

    public void resetData() {
    }

    private void switchIndexChart(KLineDataManager.Index index) {
        //只设置数据
        switch (index) {
            case MACD:
                setMACDToChart();
                break;
            case KDJ:
                setKDJToChart();
                break;
            case RSI:
                setRSIToChart();
                break;
            case BOLL:
                setBOLLToChart();
                break;
            case CCI:
                setCCIToChart();
                break;
            case EMA:
                setEMAToChart();
                break;
        }
        //统一更新
        mIndexChart.notifyDataSetChanged();
        mIndexChart.invalidate();
    }

    private void resetIndexDataSet() {
        if (mIndexChart != null) {
            if (mIndexChart.getBarData() != null) {
                mIndexChart.getBarData().clearValues();
            }
            if (mIndexChart.getLineData() != null) {
                mIndexChart.getLineData().clearValues();
            }
            if (mIndexChart.getCandleData() != null) {
                mIndexChart.getCandleData().clearValues();
            }

            mIndexChart.getAxisLeft().resetAxisMaximum();
            mIndexChart.getAxisLeft().resetAxisMinimum();

            mPrecision = 2;
            mIndexChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return NumberUtils.keepPrecision(value, mPrecision);
                }
            });
        }
    }
    private void setBOLLToChart() {
        if (mIndexChart != null) {
            resetIndexDataSet();
            mPrecision = 0;
            CombinedData combinedData = mIndexChart.getData();
            combinedData.setData(new CandleData(mManager.getBollCandleDataSet()));
            mIndexLineSet.clear();
            mIndexLineSet.addAll(mManager.getLineDataBOLL());
            combinedData.setData(new LineData(mIndexLineSet));
        }
    }

    private void setCCIToChart() {
        if (mIndexChart != null) {
            resetIndexDataSet();

            //CombinedData combinedData = mIndexChart.getData();
            //combinedData.setData(new CandleData(mManager.getBollCandleDataSet()));
            //combinedData.setData(new LineData(mManager.getLineDataBOLL()));
        }
    }

    private void setEMAToChart() {
        if (mIndexChart != null) {
            resetIndexDataSet();

            //CombinedData combinedData = mIndexChart.getData();
            //combinedData.setData(new CandleData(mManager.getBollCandleDataSet()));
            //combinedData.setData(new LineData(mManager.getLineDataBOLL()));
        }
    }

    private void setRSIToChart() {
        if (mIndexChart != null) {
            resetIndexDataSet();

            CombinedData combinedData = mIndexChart.getData();
            mIndexLineSet.clear();
            mIndexLineSet.addAll(mManager.getLineDataRSI());
            combinedData.setData(new LineData(mIndexLineSet));
        }
    }

    private void setKDJToChart() {
        if (mIndexChart != null) {
            resetIndexDataSet();

            CombinedData combinedData = mIndexChart.getData();
            mIndexLineSet.clear();
            mIndexLineSet.addAll(mManager.getLineDataKDJ());
            combinedData.setData(new LineData(mIndexLineSet));
        }
    }

    private void setMACDToChart() {
        if (mIndexChart != null) {
            resetIndexDataSet();

            CombinedData combinedData = mIndexChart.getData();
            mIndexLineSet.clear();
            mIndexLineSet.addAll(mManager.getLineDataMACD());
            combinedData.setData(new LineData(mIndexLineSet));
            combinedData.setData(new BarData(mManager.getBarDataMACD()));
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        mCurrIndex = KLineDataManager.Index.values()[pos];
        switchIndexChart(mCurrIndex);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
