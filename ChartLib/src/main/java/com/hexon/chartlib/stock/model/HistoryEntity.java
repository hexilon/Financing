package com.hexon.chartlib.stock.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hexiaohong on 2017/4/12.
 */

public class HistoryEntity implements Serializable {
    @SerializedName("date") public Calendar mDate;
    @SerializedName("open") public float mOpen;
    @SerializedName("high") public float mHigh;
    @SerializedName("low") public float mLow;
    @SerializedName("close") public float mClose;

    public HistoryEntity() {
    }
    /**
     * Constructor.
     *
     * @param date The date value
     * @param high The high value
     * @param low The low value
     * @param open
     * @param close
     */
    public HistoryEntity(Calendar date, float high, float low, float open, float close) {
        this.mDate = date;
        this.mHigh = high;
        this.mLow = low;
        this.mOpen = open;
        this.mClose = close;
    }

    public HistoryEntity(HistoryEntity data) {
        this.mDate = data.mDate;
        this.mHigh = data.mHigh;
        this.mLow = data.mLow;
        this.mOpen = data.mOpen;
        this.mClose = data.mClose;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(28);
        sb.append("Date:");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",
                Locale.getDefault());
        sb.append(format.format(mDate.getTime()));
        sb.append(' ').append("open:");
        sb.append(mOpen);
        sb.append(' ').append("close:");
        sb.append(mClose);
        sb.append(' ').append("high:");
        sb.append(mHigh);
        sb.append(' ').append("low:");
        sb.append(mLow);
        return sb.toString();
    }
}
