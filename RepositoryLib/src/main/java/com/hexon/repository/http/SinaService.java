package com.hexon.repository.http;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Copyright (C), 2022-2040
 * ClassName: SinaService
 * Description:
 * Author: Hexon
 * Date: 2022/7/20 17:01
 * Version V1.0
 */
public interface SinaService {
    public final String BASE_URL = "http://hq.sinajs.cn/";

    public final String FOREX_URL =
            "http://vip.stock.finance.sina.com.cn/forex/api/jsonp.php/data/";

    @Headers({"Referer: http://finance.sina.com.cn"})
    @GET("https://hq.sinajs.cn/list={type}")
    Observable<Response<ResponseBody>> getForexRealtimeData(@Path("type") String type);

    //http://vip.stock.finance.sina.com.cn/forex/api/jsonp.php/data/NewForexService.getOldMinKline?symbol=DINIW&scale=1&datalen=1
    @GET("NewForexService.getOldMinKline/")
    Observable<Response<ResponseBody>> getForexMinKlineData(@Query("symbol") String dataType,
                                                     @Query("scale") int periodByMinute,
                                                     @Query("datalen") int length);

    //http://vip.stock.finance.sina.com.cn/forex/api/jsonp.php/data/NewForexService.getDayKLine?symbol=DINIW
    @GET("NewForexService.getDayKLine/")
    Observable<Response<ResponseBody>> getForexDayKlineData(@Query("symbol") String dataType);
}
