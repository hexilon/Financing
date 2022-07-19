package com.hexon.repository;

import java.io.Serializable;

/**
 * Copyright (C), 2022-2030
 * ClassName: Constants
 * Description:
 * Author: Hexon
 * Date: 2022/6/23 10:04
 * Version V1.0
 */
public class Constants {
    public static final String ROOM_ICBC_DB_NAME = "icbc.room.db";

    public final static String PATTERN_DATE_REALTIME = "yyyy.MM.dd HH:mm:ss";
    public final static String PATTERN_DATE_HISTORY = "yyyy.MM.dd";
    public final static String PATTERN_DATE_GENERAL = "HH:mm:ss";
    public static final String ACTION_UPDATE_DB = "com.hexon.financing.intent.action.UPDATE_DB";

    // 最后获取数据的时间
    public static final String SP_KEY_METAL_LAST_FETCH_TIME = "metal_last_fetch_time";
    public static final String SP_KEY_METAL_MARKET_OPEN = "metal_market_open";
    // 即时报价
    public static final String SP_KEY_METAL_NOW_QUOTE = "metal_now_quote";
    // 最后开盘时间, 可能是周六的四点钟, 可能是周日的0点钟
    public static final String SP_KEY_METAL_LAST_OPEN = "metal_last_open";

    public enum NobleMetalBank implements Serializable {
        ICBC,
        CMB,
        ABC,
    }

    public enum MetalType implements Serializable {
        RMB_GOLD,
        RMB_SILVER,
        RMB_PLATINUM,
        RMB_PALLADIUM,
        USD_GOLD,
        USD_SILVER,
        USD_PLATINUM,
        USD_PALLADIUM
    }

    public enum Currency {
        RMB,
        USD
    }

    public enum PeriodType implements Serializable {
        DAY,
        WEEK,
        MONTH,
        REALTIME
    }
}
