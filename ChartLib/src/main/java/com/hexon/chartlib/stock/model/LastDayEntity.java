package com.hexon.chartlib.stock.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Copyright (C), 2018-2020
 * FileName: LastDayEntity
 * Description:
 * Author: Hexon
 * Date: 2020/7/4 17:43
 * Version V1.0
 */
class LastDayEntity implements Serializable {
    @SerializedName("date") public Calendar mDate;
    @SerializedName("priceAt0") public float mPriceAt0; //0点
    @SerializedName("open") public float mOpen; //7点
    @SerializedName("close") public float mClose; //5点/6点
}
