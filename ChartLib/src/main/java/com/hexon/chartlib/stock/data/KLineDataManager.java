package com.hexon.chartlib.stock.data;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.hexon.chartlib.R;
import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.chartlib.stock.model.KLineDataModel;
import com.hexon.chartlib.stock.model.bean.BOLLEntity;
import com.hexon.chartlib.stock.model.bean.KDJEntity;
import com.hexon.chartlib.stock.model.bean.MACDEntity;
import com.hexon.chartlib.stock.model.bean.RSIEntity;
import com.hexon.chartlib.stock.utils.DataTimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * K线数据解析
 */
public class KLineDataManager {
    final String TAG = "KLineDataManager";
    public final static String MA_HEAD_LABEL = "MA";

    public final static String DEA_HEAD_LABEL = "DEA:";
    public final static String DIFF_HEAD_LABEL = "DIFF:";

    public final static String K_HEAD_LABEL = "K:";
    public final static String D_HEAD_LABEL = "D:";
    public final static String J_HEAD_LABEL = "J:";

    public final static String RSI_HEAD_LABEL = "RSI";

    public final static String MID_HEAD_LABEL = "MID:";
    public final static String UPPER_HEAD_LABEL = "UPPER:";
    public final static String LOWER_HEAD_LABEL = "LOWER:";

    private Context mContext;
    private ArrayList<KLineDataModel> mKDataSet = new ArrayList<>();
    private float offSet = 0f;//K线图最右边偏移量
    private String assetId;
    private boolean landscape = false;//横屏还是竖屏

    //MA参数
    public enum MaPeriod {
        MA_PERIOD_1(5),MA_PERIOD_2(10), MA_PERIOD_3(20), MA_PERIOD_4(30);

        private int period;
        MaPeriod(int period) {
            this.period = period;
        }

        public int getValue() {
            return period;
        }
    };

    public enum Index {
        MACD, KDJ, RSI, BOLL, CCI, EMA
    };

    //EMA参数
    public int EMAN1 = 5;
    public int EMAN2 = 10;
    public int EMAN3 = 30;
    //SMA参数
    public int SMAN = 14;
    //BOLL参数
    public int BOLLN = 26;

    //MACD参数
    public int SHORT = 12;
    public int LONG = 26;
    public int M = 9;
    //KDJ参数
    public int KDJN = 9;
    public int KDJM1 = 3;
    public int KDJM2 = 3;
    //CCI参数
    public int CCIN = 14;
    //RSI参数
    public int RSIN1 = 6;
    public int RSIN2 = 12;
    public int RSIN3 = 24;

    //X轴数据
    private ArrayList<String> mXAxisVal = new ArrayList<>();

    private CandleDataSet mCandleDataSet;//蜡烛图集合
    private BarDataSet volumeDataSet;//成交量集合
    private BarDataSet barDataMACD;//MACD集合
    private CandleDataSet mBollCandleDataSet;//BOLL蜡烛图集合

    private List<ILineDataSet> lineDataMA = new ArrayList<>();
    private List<ILineDataSet> lineDataMACD = new ArrayList<>();
    private ArrayList<BarEntry> macdData = new ArrayList<>();
    private ArrayList<Entry> deaData = new ArrayList<>();
    private ArrayList<Entry> difData = new ArrayList<>();

    private List<ILineDataSet> lineDataKDJ = new ArrayList<>();
    private ArrayList<Entry> kData = new ArrayList<>();
    private ArrayList<Entry> dData = new ArrayList<>();
    private ArrayList<Entry> jData = new ArrayList<>();

    private List<ILineDataSet> lineDataBOLL = new ArrayList<>();
    private ArrayList<Entry> bollDataUP = new ArrayList<>();
    private ArrayList<Entry> bollDataMB = new ArrayList<>();
    private ArrayList<Entry> bollDataDN = new ArrayList<>();

    private List<ILineDataSet> lineDataRSI = new ArrayList<>();
    private ArrayList<Entry> rsiData6 = new ArrayList<>();
    private ArrayList<Entry> rsiData12 = new ArrayList<>();
    private ArrayList<Entry> rsiData24 = new ArrayList<>();
    private double preClosePrice;//K线图昨收价

    public KLineDataManager(Context context) {
        mContext = context;
    }

    public void setData(List<HistoryEntity> list) {
        mKDataSet.clear();
        lineDataMA.clear();
        mXAxisVal.clear();

        ArrayList<CandleEntry> candleEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<Entry> line5Entries = new ArrayList<>();
        ArrayList<Entry> line10Entries = new ArrayList<>();
        ArrayList<Entry> line20Entries = new ArrayList<>();
        ArrayList<Entry> line30Entries = new ArrayList<>();
        int index = 0;
        for (HistoryEntity entity : list) {
            KLineDataModel klineDatamodel = new KLineDataModel();
            klineDatamodel.setDateMills(entity.mDate.getTimeInMillis());
            klineDatamodel.setOpen(entity.mOpen);
            klineDatamodel.setHigh(entity.mHigh);
            klineDatamodel.setLow(entity.mLow);
            klineDatamodel.setClose(entity.mClose);
            preClosePrice = klineDatamodel.getPreClose();
            mKDataSet.add(klineDatamodel);

            mXAxisVal.add(DataTimeUtil.secToDate(entity.mDate.getTimeInMillis()));
            candleEntries.add(new CandleEntry(index, entity.mHigh, entity.mLow, entity.mOpen, entity.mClose));
            if (index >= 4) {
                line5Entries.add(new Entry(index, getAverage(list, index - 4, 5)));
            }
            if (index >= 9) {
                line10Entries.add(new Entry(index, getAverage(list, index - 9, 10)));
            }
            if (index >= 19) {
                line20Entries.add(new Entry(index, getAverage(list, index - 19, 20)));
            }
            if (index >= 29) {
                line30Entries.add(new Entry(index, getAverage(list, index - 29, 30)));
            }

            ++index;
        }

        mCandleDataSet = setCandleDataSet(candleEntries);
        mBollCandleDataSet = setBOLLCandleDataSet(candleEntries);
        lineDataMA.add(setALine(MaPeriod.MA_PERIOD_1, line5Entries, false));
        lineDataMA.add(setALine(MaPeriod.MA_PERIOD_2, line10Entries, false));
        lineDataMA.add(setALine(MaPeriod.MA_PERIOD_3, line20Entries, false));
        lineDataMA.add(setALine(MaPeriod.MA_PERIOD_4, line30Entries, false));

        initMACD();
        initKDJ();
        initRSI();
        initBOLL();
    }

    private float getAverage(List<HistoryEntity> list, int start, int count) {
        float result = 0f;
        int end = start + count;

        if (start > list.size()) {
            return -1f;
        }

        if (count == 0) {
            return list.get(start).mClose;
        }

        if (list.size() < end) {
            end = list.size();
        }
        for (int i = start; i < end; i++) {
            result += list.get(i).mClose;
        }

        return result/(end - start);
    }

    /**
     * 初始化自己计算MACD
     */
    public void initMACD() {
        MACDEntity macdEntity = new MACDEntity(getKLineDatas(), SHORT, LONG, M);

        macdData = new ArrayList<>();
        deaData = new ArrayList<>();
        difData = new ArrayList<>();
        for (int i = 0; i < macdEntity.getMACD().size(); i++) {
            macdData.add(new BarEntry(i + offSet, macdEntity.getMACD().get(i), macdEntity.getMACD().get(i)));
            deaData.add(new Entry(i + offSet, macdEntity.getDEA().get(i)));
            difData.add(new Entry(i + offSet, macdEntity.getDIF().get(i)));
        }
        barDataMACD = setBarData(macdData);
        if (lineDataMACD.size() > 0) {
            lineDataMACD.clear();
        }
        lineDataMACD.add(setALine(MaPeriod.MA_PERIOD_1, deaData, DEA_HEAD_LABEL));
        lineDataMACD.add(setALine(MaPeriod.MA_PERIOD_2, difData, DIFF_HEAD_LABEL));
    }

    /**
     * 初始化自己计算KDJ
     */
    public void initKDJ() {
        KDJEntity kdjEntity = new KDJEntity(getKLineDatas(), KDJN, KDJM1, KDJM2);

        kData = new ArrayList<>();
        dData = new ArrayList<>();
        jData = new ArrayList<>();
        for (int i = 0; i < kdjEntity.getD().size(); i++) {
            kData.add(new Entry(i, kdjEntity.getK().get(i)));
            dData.add(new Entry(i, kdjEntity.getD().get(i)));
            jData.add(new Entry(i, kdjEntity.getJ().get(i)));
        }
        if (lineDataKDJ.size() > 0) {
            lineDataKDJ.clear();
        }
        lineDataKDJ.add(setALine(MaPeriod.MA_PERIOD_1, kData, K_HEAD_LABEL, false));
        lineDataKDJ.add(setALine(MaPeriod.MA_PERIOD_2, dData, D_HEAD_LABEL, false));
        lineDataKDJ.add(setALine(MaPeriod.MA_PERIOD_3, jData, J_HEAD_LABEL, true));
    }

    /**
     * 初始化自己计算BOLL
     */
    public void initBOLL() {
        BOLLEntity bollEntity = new BOLLEntity(getKLineDatas(), BOLLN);
        bollDataUP = new ArrayList<>();
        bollDataMB = new ArrayList<>();
        bollDataDN = new ArrayList<>();
        for (int i = 0; i < bollEntity.getUPs().size(); i++) {
            bollDataUP.add(new Entry(i + BOLLN-1, bollEntity.getUPs().get(i)));
            bollDataMB.add(new Entry(i + BOLLN-1, bollEntity.getMBs().get(i)));
            bollDataDN.add(new Entry(i + BOLLN-1, bollEntity.getDNs().get(i)));
        }

        if (lineDataBOLL.size() > 0) {
            lineDataBOLL.clear();
        }
        lineDataBOLL.add(setALine(MaPeriod.MA_PERIOD_1, bollDataUP, UPPER_HEAD_LABEL,false));
        lineDataBOLL.add(setALine(MaPeriod.MA_PERIOD_2, bollDataMB, MID_HEAD_LABEL,false));
        lineDataBOLL.add(setALine(MaPeriod.MA_PERIOD_3, bollDataDN, LOWER_HEAD_LABEL, false));
    }
    /**
     * 初始化自己计算RSI
     */
    public void initRSI() {
        RSIEntity rsiEntity6 = new RSIEntity(getKLineDatas(), RSIN1);
        RSIEntity rsiEntity12 = new RSIEntity(getKLineDatas(), RSIN2);
        RSIEntity rsiEntity24 = new RSIEntity(getKLineDatas(), RSIN3);

        rsiData6 = new ArrayList<>();
        rsiData12 = new ArrayList<>();
        rsiData24 = new ArrayList<>();
        for (int i = 0; i < rsiEntity6.getRSIs().size(); i++) {
            rsiData6.add(new Entry(i, rsiEntity6.getRSIs().get(i)));
            rsiData12.add(new Entry(i, rsiEntity12.getRSIs().get(i)));
            rsiData24.add(new Entry(i, rsiEntity24.getRSIs().get(i)));
        }
        if (lineDataRSI.size() > 0) {
            lineDataRSI.clear();
        }
        lineDataRSI.add(setALine(MaPeriod.MA_PERIOD_1, rsiData6, RSI_HEAD_LABEL + RSIN1, true));
        lineDataRSI.add(setALine(MaPeriod.MA_PERIOD_2, rsiData12, RSI_HEAD_LABEL + RSIN2, false));
        lineDataRSI.add(setALine(MaPeriod.MA_PERIOD_3, rsiData24, RSI_HEAD_LABEL + RSIN3, false));
    }

    private CandleDataSet setCandleDataSet(ArrayList<CandleEntry> candleEntries) {
        Log.d(TAG, "setCandleDataSet candleEntries.size:" + candleEntries.size());
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, null);
        candleDataSet.setDrawHorizontalHighlightIndicator(true);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highlight));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.decrease));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.increase));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.neutral));
        candleDataSet.setBarSpace(0.12f);
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setValueTextSize(10);
        candleDataSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.highlight));
        candleDataSet.setDrawValues(false);

        return candleDataSet;
    }

    private CandleDataSet setBOLLCandleDataSet(ArrayList<CandleEntry> candleEntries) {
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "KLine");
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highlight));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.decrease));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.increase));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.neutral));
        candleDataSet.setDrawValues(false);
        candleDataSet.setShowCandleBar(false);
        return candleDataSet;
    }

    private LineDataSet setALine(MaPeriod ma, ArrayList<Entry> lineEntries) {
        String label = MA_HEAD_LABEL + ma.period;
        return setALine(ma, lineEntries, label);
    }

    private LineDataSet setALine(MaPeriod ma, ArrayList<Entry> lineEntries, boolean highlightEnable) {
        String label = MA_HEAD_LABEL + ma.period;
        return setALine(ma, lineEntries, label, highlightEnable);
    }

    private LineDataSet setALine(MaPeriod ma, ArrayList<Entry> lineEntries, String label) {
        boolean highlightEnable = false;
        return setALine(ma, lineEntries, label, highlightEnable);
    }

    private LineDataSet setALine(MaPeriod period, ArrayList<Entry> lineEntries, String label, boolean highlightEnable) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, label);
        lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
        lineDataSetMa.setHighlightEnabled(highlightEnable);
        lineDataSetMa.setHighLightColor(ContextCompat.getColor(mContext, R.color.highlight));
        lineDataSetMa.setDrawValues(false);
        if (period == MaPeriod.MA_PERIOD_1) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma5));
        } else if (period == MaPeriod.MA_PERIOD_2) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma10));
        } else if (period == MaPeriod.MA_PERIOD_3) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma20));
        } else if (period == MaPeriod.MA_PERIOD_4) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma30));
        }
        lineDataSetMa.setLineWidth(0.6f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSetMa;
    }

    private BarDataSet setBarData(ArrayList<BarEntry> barEntries) {
        String label = "";
        return setBarData(barEntries, label);
    }

    private BarDataSet setBarData(ArrayList<BarEntry> barEntries, String label) {
        BarDataSet barDataSet = new BarDataSet(barEntries, label);
        barDataSet.setHighlightEnabled(true);
        barDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highlight));
        barDataSet.setValueTextSize(10);
        barDataSet.setDrawValues(false);
        barDataSet.setColors(ContextCompat.getColor(mContext, R.color.increase),
                ContextCompat.getColor(mContext, R.color.decrease));
        return barDataSet;
    }


    float sum = 0;

    private float getSum(Integer a, Integer b) {
        sum = 0;
        if (a < 0) {
            return 0;
        }
        for (int i = a; i <= b; i++) {
            sum += getKLineDatas().get(i).getClose();
        }
        return sum;
    }

    public void addAKLineData(KLineDataModel kLineData) {
        mKDataSet.add(kLineData);
    }

    public void addKLineDatas(List<KLineDataModel> kLineData) {
        mKDataSet.addAll(kLineData);
    }

    public synchronized ArrayList<KLineDataModel> getKLineDatas() {
        return mKDataSet;
    }

    public void resetKLineData() {
        mKDataSet.clear();
    }

    public void setKLineData(ArrayList<KLineDataModel> datas) {
        mKDataSet.clear();
        mKDataSet.addAll(datas);
    }

    public ArrayList<String> getxVals() {
        return mXAxisVal;
    }

    public List<ILineDataSet> getLineDataMA() {
        return lineDataMA;
    }

    public List<ILineDataSet> getLineDataBOLL() {
        return lineDataBOLL;
    }

    public List<ILineDataSet> getLineDataKDJ() {
        return lineDataKDJ;
    }

    public List<ILineDataSet> getLineDataRSI() {
        return lineDataRSI;
    }

    public List<ILineDataSet> getLineDataMACD() {
        return lineDataMACD;
    }

    public BarDataSet getBarDataMACD() {
        return barDataMACD;
    }

    public BarDataSet getVolumeDataSet() {
        return volumeDataSet;
    }

    public CandleDataSet getCandleDataSet() {
        return mCandleDataSet;
    }

    public CandleDataSet getBollCandleDataSet() {
        return mBollCandleDataSet;
    }

    public float getOffSet() {
        return offSet;
    }

    public ArrayList<BarEntry> getMacdData() {
        return macdData;
    }

    public ArrayList<Entry> getDeaData() {
        return deaData;
    }

    public ArrayList<Entry> getDifData() {
        return difData;
    }

    public ArrayList<Entry> getkData() {
        return kData;
    }

    public ArrayList<Entry> getdData() {
        return dData;
    }

    public ArrayList<Entry> getjData() {
        return jData;
    }

    public ArrayList<Entry> getBollDataUP() {
        return bollDataUP;
    }

    public ArrayList<Entry> getBollDataMB() {
        return bollDataMB;
    }

    public ArrayList<Entry> getBollDataDN() {
        return bollDataDN;
    }

    public ArrayList<Entry> getRsiData6() {
        return rsiData6;
    }

    public ArrayList<Entry> getRsiData12() {
        return rsiData12;
    }

    public ArrayList<Entry> getRsiData24() {
        return rsiData24;
    }

    public String getAssetId() {
        return assetId;
    }

    public double getPreClosePrice() {
        return preClosePrice;
    }
}
