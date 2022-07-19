package com.hexon.chartlib.stock.data;

import android.content.Context;
import android.util.Log;

import com.hexon.chartlib.stock.model.RealtimeEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 分时数据解析
 */

public class RealtimeDataManager {
    final String TAG = "RealtimeDataManager";
    private final float LEAVE_BLACK = 0.5f;
    private ArrayList<RealtimeEntity> mRealTimeList = new ArrayList<>();//分时数据
    private float mOpenValue = 0;//分时图基准值
    private float mFiveClockValue = 0;//早晨5点钟收盘价
    private float mMax = 0;//分时图最大价格
    private float mMin = 0;//分时图最小价格
    private Context mContext;

    public RealtimeDataManager(Context context) {
        mContext = context;
    }

    public RealtimeDataManager(Context context, ArrayList<RealtimeEntity> list) {
        mContext = context;
        mRealTimeList.clear();
        mRealTimeList.addAll(list);
        calculateBaseData();
    }

    public void setData(List<RealtimeEntity> dataSet) {
        if (dataSet == null) {
            Log.e(TAG, "data is null", new Throwable("setData"));
            return;
        }
        mRealTimeList.clear();
        mRealTimeList.addAll(dataSet);
        calculateBaseData();
    }

    private void calculateBaseData() {
        //选择第一个数据作为开盘价
        mOpenValue = mRealTimeList.get(0).mPrice;
        mMax = mMin = mOpenValue;
        RealtimeEntity preEntity = null;

        for (RealtimeEntity entity : mRealTimeList) {
            if (entity.mPrice > mMax) {
                mMax = entity.mPrice;
            } else if (entity.mPrice < mMin) {
                mMin = entity.mPrice;
            }

            if (entity.mTime.get(Calendar.HOUR_OF_DAY) == 6
                    && preEntity.mTime.get(Calendar.HOUR_OF_DAY) <= 5) {//收盘可能早于5点钟
                mFiveClockValue = preEntity.mPrice;
                //Log.d(TAG, "mFiveClockValue:" + mFiveClockValue + " preEntity:" + preEntity);
            }

            preEntity = entity;
        }
    }

    public synchronized ArrayList<RealtimeEntity> getRealTimeData() {
        return mRealTimeList;
    }

    public void resetDataSet() {
        getRealTimeData().clear();
    }

    public float getMax() {
        return (float) (mOpenValue + mOpenValue * getPercentMax());
    }

    public float getMin() {
        return (float) (mOpenValue + mOpenValue * getPercentMin());
    }

    //分时图右Y轴最大涨跌值
    public float getPercentMax() {
        //0.1表示Y轴最大涨跌值再增加LEAVE_BLACK，使图线不至于顶到最顶部
        float upInterval = mMax - mOpenValue;
        float downInterval = mOpenValue - mMin;
        return (float) (upInterval / mOpenValue
                + Math.abs(upInterval > downInterval ? upInterval : downInterval) / mOpenValue * LEAVE_BLACK);
    }

    //分时图右Y轴最小涨跌值 负值
    public float getPercentMin() {
        //0.1表示Y轴最小涨跌值再减小10%，使图线不至于顶到最底部
        float upInterval = mMax - mOpenValue;
        float downInterval = mOpenValue - mMin;
        return (float) (-downInterval / mOpenValue
                - Math.abs(upInterval > downInterval ? upInterval : downInterval) / mOpenValue * LEAVE_BLACK);
    }

    public float getOpenPrice() {
        return mOpenValue;
    }

    public float getFiveClockPrice() {
        return mFiveClockValue;
    }
}
