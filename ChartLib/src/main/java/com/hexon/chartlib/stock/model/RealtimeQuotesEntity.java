package com.hexon.chartlib.stock.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Hexh
 * @date 2019-06-21 15:28
 */
public class RealtimeQuotesEntity implements Serializable {
    @SerializedName("type") public int mType;
    @SerializedName("time") public Calendar mTime;
    @SerializedName("price") public float mPrice;
    @SerializedName("name") public String mName;
    @SerializedName("buy") public float mBuy;
    @SerializedName("sale") public float mSale;
    @SerializedName("high") public float mHigh;
    @SerializedName("low") public float mLow;
    @SerializedName("change") public float mChange;
    @SerializedName("unit") public String mUnit;
    @SerializedName("refresh") public String mStrRefresh;

    public void RealtimeQuotesEntity() {
    }

    public String toString() {
        return "name:" + mName
                + " type:" + mType
                + " price:" + mPrice
                + " buy:" + mBuy
                + " sale:" + mSale
                + " high:" + mHigh
                + " low:" + mLow
                + " change:" + mChange
                + " unit:" + mUnit
                + " refresh:" + mStrRefresh;
    }
}
