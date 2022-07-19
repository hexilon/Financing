package com.hexon.repository.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hexon on 2022/6/23
 */

public class ExchangeRate implements Serializable {
    @SerializedName("name") public String mName;
    @SerializedName("updateTime") public String mUpdateTime;
    @SerializedName("current") public float mCurrPrice;
    @SerializedName("change") public float mChange;
    @SerializedName("open") public float mOpen;
    @SerializedName("close") public float mClose;
    @SerializedName("amplitudePercent") public float mAmplitudePercent;
    @SerializedName("volatilityRange") public float mVolatilityRange;
    @SerializedName("low") public float mLowPrice;
    @SerializedName("high") public float mHighPrice;

    public ExchangeRate() {
    }

    @Override
    public String toString() {
        return "mUpdateTime:" + mUpdateTime
                + " mName:" + mName
                + " price:" + mCurrPrice
                + " mChange" + mChange
                + " mOpen:" + mOpen
                + " mClose:" + mClose
                + " mAmplitudePercent:" + mAmplitudePercent
                + " mVolatilityRange:" + mVolatilityRange
                + " mLowPrice:" + mLowPrice
                + " mHighPrice:" + mHighPrice;
    }
}
