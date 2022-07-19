package com.hexon.repository.http;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Copyright (C), 2020-2025
 * FileName    : IcbcService
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/21 21:26
 * Version     : V1.0
 */
public interface IcbcService {
    public final String BASE_URL = "http://m.icbc.com.cn/";
    public final int DATA_TYPE = 0;

    /* 所有实时数据 3K-25K 样式:
        人民币账户黄金	431.22	-0.05
        人民币账户白银	5.946	-0.05
        人民币账户铂金	205.78	1.27
        人民币账户钯金	485.13	-0.50
        美元账户黄金	1938.3500	-0.05
        美元账户白银	26.7267	-0.06
        美元账户铂金	924.9900	1.27
        美元账户钯金	2180.6800	-0.50
        */
    /* http://m.icbc.com.cn/WapDynamicSite/Windroid/GoldMarket/AccResponse.aspx */
    @GET("WapDynamicSite/Windroid/GoldMarket/AccResponse.aspx")
    Observable<Response<ResponseBody>> getSummaryRealtimeQuotes();

    // 所有实时数据 1K-2K XML data: ID,名称,单位,买入,卖出,中间,最高,最低 (不含涨跌幅)
    // http://www.icbc.com.cn/ICBCDynamicSite/Charts/GetGoldDataService.asmx/GetAllAccGoldDatas
    @GET("http://www.icbc.com.cn/ICBCDynamicSite/Charts/GetGoldDataService.asmx/GetAllAccGoldDatas")
    Observable<Response<ResponseBody>> getAllRealtimeQuotes();

    // 某一产品的实时数据 21K左右 含涨跌幅 买入,卖出,中间,最高,最低,涨跌幅,单位,查询时间
    // id -> 901001, 903001, 905001, 907001, 801014, 803014, 805014, 807014
    /* http://m.icbc.com.cn/WapDynamicSite/Windroid/GoldMarket/AccInfo.aspx?ID=901001 */
    @GET("WapDynamicSite/Windroid/GoldMarket/AccInfo.aspx")
    Observable<Response<ResponseBody>> getRealtimeQuotes(@Query("ID") String id);

    // 包含某一个产品的所有历史数据: 当日实时, 日, 周, 月 JSON格式
    /*http://www.icbc.com.cn/ICBCDynamicSite/Charts/GetGoldDataService.asmx/GetTouchPagePicData?dataType=0&dataId=901*/
    //dataType -> 0(不变);
    // dataId -> 901, 903, 905, 907, 801, 803, 805, 807;
    @GET("http://www.icbc.com.cn/ICBCDynamicSite/Charts/GetGoldDataService.asmx/GetTouchPagePicData")
    Observable<Response<ResponseBody>> getAllHistory(@Query("dataType") int dataType, @Query("dataId") String dataId);

    // 获取某产品某周期的历史数据
    // http://www.icbc.com.cn/ICBCDynamicSite/Charts/GetGoldDataService.asmx/GetGoldPicData?dataType=0&dataId=901&dataTimeType=3
    // dataType -> 0(不变);
    // dataId -> 901, 903, 905, 907, 801, 803, 805, 807;
    // dataTimeType: 0 ->day; 1 -> week; 2 -> month; 3 -> realtime
    @GET("http://www.icbc.com.cn/ICBCDynamicSite/Charts/GetGoldDataService.asmx/GetGoldPicData")
    Observable<Response<ResponseBody>> getHistoryByPeriod(@Query("dataType") int dataType,
                                                          @Query("dataId") String dataId,
                                                          @Query("dataTimeType") int dataTimeType);
}
