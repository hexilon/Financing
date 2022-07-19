package com.hexon.chartlib.stock.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/9/8.
 */
public class RealtimeEntity implements Serializable {
    @SerializedName("time")
    public Calendar mTime;
    @SerializedName("price")
    public float mPrice;

    public RealtimeEntity() {
    }

    public RealtimeEntity(Calendar time, float price) {
        mTime = time;
        mPrice = price;
    }

    public RealtimeEntity(long timestamp, float price) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        mTime = calendar;
        mPrice = price;
    }

    @Override
    public String toString() {
        return "time:" + (mTime != null ? mTime.getTime() : "null")
                + " price:" + mPrice;
    }
}
