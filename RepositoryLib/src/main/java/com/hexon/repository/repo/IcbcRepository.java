package com.hexon.repository.repo;

import static com.hexon.repository.Constants.DEFAULT_CONNECT_TIMEOUT;
import static com.hexon.repository.Constants.SP_KEY_METAL_NOW_QUOTE;
import static com.hexon.repository.http.IcbcService.DATA_TYPE;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.gson.annotations.SerializedName;

import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.chartlib.stock.model.RealtimeEntity;
import com.hexon.chartlib.stock.model.RealtimeQuotesEntity;
import com.hexon.mvvm.base.BaseApplication;
import com.hexon.repository.Constants;
import com.hexon.repository.base.ApiResponse;
import com.hexon.repository.base.BaseRepository;
import com.hexon.repository.base.NetworkBoundResource;
import com.hexon.repository.base.Resource;
import com.hexon.repository.database.room.IcbcDatabase;
import com.hexon.repository.database.room.dao.DayHistoryDao;
import com.hexon.repository.database.room.dao.MonthHistoryDao;
import com.hexon.repository.database.room.dao.RealtimeDao;
import com.hexon.repository.database.room.dao.WeekHistoryDao;
import com.hexon.repository.database.room.entity.DayHistory;
import com.hexon.repository.database.room.entity.History;
import com.hexon.repository.database.room.entity.MonthHistory;
import com.hexon.repository.database.room.entity.Realtime;
import com.hexon.repository.database.room.entity.WeekHistory;
import com.hexon.repository.http.IcbcService;
import com.hexon.util.LogUtils;
import com.hexon.util.SharedPrefsUtils;
import com.hexon.util.StringUtils;
import com.hexon.util.constant.TimeConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Copyright (C), 2020-2025
 * FileName    : IcbcRepository
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/22 9:06
 * Version     : V1.0
 */
public class IcbcRepository extends BaseRepository {
    public final static String REALTIME_DATA = "realtimeData";
    public final static String DAY_DATA = "dayData";
    public final static String WEEK_DATA = "weekData";
    public final static String MONTH_DATA = "monthData";
    public final static long MARKET_CLOSE_TIMEOUT = TimeConstants.HOUR;

    private final static String SP_NAME = "icbc";
    private final static String SP_KEY_HISTORY_PREFIX = "icbc_history_";

    static IcbcRepository sInstance;

    IcbcService mService;
    SharedPrefsUtils mSpUtils;
    IcbcDatabase mRoom;

    private List<RealtimeQuotesEntity> mRealtimeQuotesEntityList = new ArrayList<>();
    private RealtimeQuotesEntity mRealtimeQuotesEntity = new RealtimeQuotesEntity();

    public static class PriceInfo {
        // 0点价格
        @SerializedName("price_at0")
        public RealtimeEntity priceAt0;
        // 收市报价(4点/5点)
        @SerializedName("pre_close")
        public RealtimeEntity preClose;
        // 开市报价(6点/7点)
        @SerializedName("curr_open")
        public RealtimeEntity currOpen;

        public PriceInfo() {
            priceAt0 = null;
            preClose = null;
            currOpen = null;
        }

        @Override
        public String toString() {
            return "priceAt0:" + priceAt0
                    + " preClose:" + preClose
                    + " currOpen:" + currOpen;
        }
    }

    static final Map<Constants.MetalType, String> sMetalIdMap = new HashMap<Constants.MetalType, String>() {{
        put(Constants.MetalType.RMB_GOLD, "901");//rmb_gold
        put(Constants.MetalType.RMB_SILVER, "903");//rmb_silver
        put(Constants.MetalType.RMB_PLATINUM, "905");//rmb_pt
        put(Constants.MetalType.RMB_PALLADIUM, "907");//rmb_pd
        put(Constants.MetalType.USD_GOLD, "801");//us_gold
        put(Constants.MetalType.USD_SILVER, "803");//us_silver
        put(Constants.MetalType.USD_PLATINUM, "805");//us_pt
        put(Constants.MetalType.USD_PALLADIUM, "807");//us_pd
    }};

    static final Map<Constants.Currency, String> sCurrencyIdMap = new HashMap<Constants.Currency, String>() {{
        put(Constants.Currency.RMB, "001");//rmb
        put(Constants.Currency.USD, "014");//usd
    }};

    private IcbcRepository(BaseApplication application) {
        super(application);
        //HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();//打印日志
        //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//设定日志级别
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        //builder.addInterceptor(httpLoggingInterceptor);//添加拦截器
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IcbcService.BASE_URL)
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())//返回String类型
                .addConverterFactory(GsonConverterFactory.create())//返回Gson类型
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加 RxJava 适配器
                .build();
        mService = retrofit.create(IcbcService.class);
        mSpUtils = SharedPrefsUtils.getInstance(mApp, SP_NAME);
        mRoom = IcbcDatabase.getInstance(mApp);
        initData();
    }

    private void initData() {
        // 获取一次今天的数据用于计算涨跌幅度,同时用于判定有没有开市
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkMarketOpen();
            }
        }).start();
        for (Constants.MetalType metalType : Constants.MetalType.values()) {
            getTodayDatas(metalType)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
        mApp.getIsAppForeground().observe(ProcessLifecycleOwner.get(),
                new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean foreground) {
                LogUtils.d("onChanged:" + foreground);
                if (foreground) {
                    checkMarketOpen();
                }
            }
        });
    }

    public static IcbcRepository getInstance(BaseApplication application) {
        if (sInstance == null) {
            synchronized (IcbcRepository.class) {
                if (sInstance == null) {
                    sInstance = new IcbcRepository(application);
                }
            }
        }

        return sInstance;
    }

    int getDataTimeType(Constants.PeriodType periodType) {
        int result;
        switch (periodType) {
            case  DAY:
                result = 0;
            case WEEK:
                result = 1;
            case MONTH:
                result = 2;
            case REALTIME:
                result = 3;
                break;
            default:
                result = 3;
        }

        return result;
    }

    private void checkMarketOpen() {
        // TODO 找到确定的方法检测是否开盘
        try {
            Response<ResponseBody> response = mService.getHistoryByPeriod(
                            DATA_TYPE, getMetalString(Constants.MetalType.RMB_GOLD),
                            getDataTimeType(Constants.PeriodType.REALTIME))
                    .subscribeOn(Schedulers.io())
                    .blockingSingle();

            parseTodayDatasResponse(Constants.MetalType.RMB_GOLD, response.body().string());
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.d("checkMarketOpen:" + isMarketOpening());
    }

    private void setMarketOpen(boolean open) {
        LogUtils.d("setMarketOpen:" + open);
        mSpUtils.putData(Constants.SP_KEY_METAL_MARKET_OPEN, open);
    }

    public boolean isMarketOpening() {
        //LogUtils.d("isMarketOpening:" + mSpUtils.getData(Constants.SP_KEY_METAL_MARKET_OPEN, true));
        return mSpUtils.getData(Constants.SP_KEY_METAL_MARKET_OPEN, true);
    }

    public String getMetalName(Constants.MetalType type) {
        return mSpUtils.getData(type.toString(), "");
    }

    public void setMetalName(Constants.MetalType type, String name) {
        mSpUtils.putData(type.toString(), name);
    }

    /*
     * 获取所有的实时行情数据
     * */
    public Observable<Resource<List<RealtimeQuotesEntity>>> getAllRealtimeQuotes() {
        return new NetworkBoundResource<List<RealtimeQuotesEntity>, ResponseBody>() {

            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                try {
                    String response = item.string();
                    mRealtimeQuotesEntityList.clear();
                    mRealtimeQuotesEntityList.addAll(parseAllAccGoldDatas(response));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean shouldFetch(Resource<List<RealtimeQuotesEntity>> dbResource) {
                if (mRealtimeQuotesEntityList.isEmpty()) {
                    return true;
                }
                return isMarketOpening();
            }

            @NonNull
            @Override
            protected Flowable<List<RealtimeQuotesEntity>> loadFromDb() {
                return Flowable.just(mRealtimeQuotesEntityList);
            }

            @NonNull
            @Override
            protected Observable<ApiResponse<ResponseBody>> createCall() {
                return mService.getAllRealtimeQuotes().map(
                        new Function<Response<ResponseBody>, ApiResponse<ResponseBody>>() {
                            @Override
                            public ApiResponse<ResponseBody> apply(Response<ResponseBody> s) {
                                return new ApiResponse(s);
                            }
                        });
            }
        }.asObservable();
    }

    private List<RealtimeQuotesEntity> parseAllAccGoldDatas(@NonNull String response) {
        //LogUtils.d("parseAllAccGoldDatas response:" + response);
        List<RealtimeQuotesEntity> list = new ArrayList<>();
        Document doc = Jsoup.parse(response);
        Elements elements = doc.getElementsByTag("data");
        //LogUtils.d("data elements:" + elements.size());
        for (Element element : elements) {
            Constants.MetalType index = getMetalType(element.attr("id"));
            if (index == null) {
                continue;
            }

            RealtimeQuotesEntity entity = new RealtimeQuotesEntity();
            entity.mType = index.ordinal();
            entity.mTime = Calendar.getInstance();
            entity.mStrRefresh = StringUtils.dateToDateString(entity.mTime.getTime(),
                    Constants.PATTERN_DATE_GENERAL);

            entity.mName = element.attr("name");
            entity.mUnit = element.attr("dwbs");
            entity.mBuy = Float.parseFloat(element.attr("mrj"));
            entity.mSale = Float.parseFloat(element.attr("mcj"));
            entity.mPrice = Float.parseFloat(element.attr("zjj"));
            entity.mHigh = Float.parseFloat(element.attr("zgzjj"));
            entity.mLow = Float.parseFloat(element.attr("zdzjj"));

            if (getMetalName(index).isEmpty() && !entity.mName.isEmpty()) {
                setMetalName(index, entity.mName);
            }

            if (mSpUtils.contains(SP_KEY_METAL_NOW_QUOTE + index)) {
                PriceInfo priceInfo = (PriceInfo) mSpUtils.getData(
                        SP_KEY_METAL_NOW_QUOTE + index, new PriceInfo());
                //LogUtils.d(index + " priceInfo:" + priceInfo);
                if (priceInfo.currOpen == null) {
                    if (priceInfo.preClose == null) {
                        if (priceInfo.priceAt0 != null) {
                            entity.mChange = (float) (Math.round((entity.mPrice - priceInfo.priceAt0.mPrice)
                                    / priceInfo.priceAt0.mPrice * 10000f) / 100f);
                        }
                    } else {
                        entity.mChange = (float) (Math.round((entity.mPrice - priceInfo.preClose.mPrice)
                                / priceInfo.preClose.mPrice * 10000f) / 100f);
                    }
                } else {
                    entity.mChange = (float) (Math.round((entity.mPrice - priceInfo.currOpen.mPrice)
                            / priceInfo.currOpen.mPrice * 10000f) / 100f);
                }
            }
            //LogUtils.d(entity);
            list.add(entity);
        }

        return list;
    }

    /*
     *获取当天@{type}的实时数据
     */
    public Observable<Resource<RealtimeQuotesEntity>> getRealtimeQuotes(Constants.MetalType type) {
        Constants.Currency currency;
        switch (type) {
            case RMB_GOLD:
            case RMB_SILVER:
            case RMB_PLATINUM:
            case RMB_PALLADIUM:
                currency = Constants.Currency.RMB;
                break;
            case USD_GOLD:
            case USD_SILVER:
            case USD_PLATINUM:
            case USD_PALLADIUM:
                currency = Constants.Currency.USD;
                break;
            default:
                currency = Constants.Currency.RMB;
        }

        return getRealtimeQuotes(type, currency);
    }

    private Observable<Resource<RealtimeQuotesEntity>> getRealtimeQuotes(
            final Constants.MetalType metal, Constants.Currency currency) {
        String queryId = getMetalString(metal);

        for (Map.Entry<Constants.Currency, String> Id : sCurrencyIdMap.entrySet()) {
            if (Id.getKey() == currency) {
                queryId += Id.getValue();
                //LogUtils.d("mId:" + queryId);
                break;
            }
        }

        String finalQueryId = queryId;
        return new NetworkBoundResource<RealtimeQuotesEntity, ResponseBody>() {

            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                try {
                    String response = item.string();
                    mRealtimeQuotesEntity = parseRealtimeQuotesResponse(metal, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean shouldFetch(Resource<RealtimeQuotesEntity> dbResource) {
                return isMarketOpening();
            }

            @NonNull
            @Override
            protected Flowable<RealtimeQuotesEntity> loadFromDb() {
                return Flowable.just(mRealtimeQuotesEntity);
            }

            @NonNull
            @Override
            protected Observable<ApiResponse<ResponseBody>> createCall() {
                return mService.getRealtimeQuotes(finalQueryId)
                        .map(new Function<Response<ResponseBody>, ApiResponse<ResponseBody>>() {
                            @Override
                            public ApiResponse<ResponseBody> apply(Response<ResponseBody> s) throws Exception {
                                return new ApiResponse(s);
                            }
                        });
            }
        }.asObservable();
    }

    private RealtimeQuotesEntity parseRealtimeQuotesResponse(Constants.MetalType type, String response) {
        //LogUtils.d("parseRealtimeQuotesResponse response:" + response);
        RealtimeQuotesEntity entity = new RealtimeQuotesEntity();
        Document doc = Jsoup.parse(response);
        entity.mTime = Calendar.getInstance();

        // 品名
        Element element = doc.getElementById("headtext");
        entity.mName = element.text();
        if (getMetalName(type).isEmpty() && !entity.mName.isEmpty()) {
            setMetalName(type, entity.mName);
        }

        //1. 银行买入价
        element = doc.getElementById("LabelMrj_bottom");
        //LogUtil.d(TAG, element.text());
        entity.mBuy = Float.parseFloat(element.text());

        //2. 银行卖出价
        element = doc.getElementById("LabelMcj_bottom");
        //LogUtil.d(TAG, element.text());
        entity.mSale = Float.parseFloat(element.text());

        //3. 最高价
        element = doc.getElementById("LabelMaxMidPrice_bottom");
        entity.mHigh = Float.parseFloat(element.text());

        //4. 最低价
        element = doc.getElementById("LabelMinMidPrice_bottom");
        //LogUtil.d(TAG, element.text());
        entity.mLow = Float.parseFloat(element.text());

        //5. 中间价
        element = doc.getElementById("LabelZjj_bottom");
        entity.mPrice = Float.parseFloat(element.text());
        //LogUtil.d(element.text() + " covert to:" + entity.mPrice);

        //6. 单位
        element = doc.getElementById("LabelUnit_bottom");
        entity.mUnit = element.text();

        //7. 涨跌幅%
        element = doc.getElementById("DoubleLabelTagUpdownRate_bottom");
        //LogUtil.d(TAG, element.text());
        entity.mChange = Float.parseFloat(element.text());

        //8. 查询时间
        element = doc.getElementById("LabelTime_bottom");
        //LogUtil.d(TAG, element.text());
        entity.mStrRefresh = element.text();
        //LogUtil.d("entity:" + entity);
        //存储类型对应的产品名称
        if (((String) mSpUtils.getData("" + type, "")).equals("")) {
            LogUtils.d("putData type:" + type + " name:" + entity.mName);
            mSpUtils.putData("" + type, entity.mName);
        }
        return entity;
    }

    /*
     * 获取历史数据
     * @metal : 品种
     * @period : 周期:日, 周, 月
     */
    public Observable<Resource<List<HistoryEntity>>> getHistoryDatas(
            final Constants.MetalType metal, Constants.PeriodType period) {
        //LogUtils.d("getHistoryDatas metal:" + metal + " period:" + period);
        return new NetworkBoundResource<List<HistoryEntity>, ResponseBody>() {
            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                List<HistoryEntity> list = null;
                try {
                    list = parseHistoryDatasResponse(metal, item.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                putHistoryDatas(metal, period, list);
            }

            @Override
            protected boolean shouldFetch(Resource<List<HistoryEntity>> dbResource) {
                boolean should = true;
                if (dbResource.getData() != null && dbResource.getData().size() > 0) {
                    Calendar now = Calendar.getInstance();
                    Calendar calendar = mSpUtils.getData(getHistorySpKey(metal, period), now);
                    //HistoryEntity last = dbResource.getData().get(dbResource.getData().size()-1);
                    if (now != calendar
                            && now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR) <= 1) {
                        should = false;
                    }
                }
                LogUtils.d("shouldFetch:" + should);
                return should;
            }

            @NonNull
            @Override
            protected Flowable<List<HistoryEntity>> loadFromDb() {
                return getHistoryDatasFromDb(metal, period)
                        .toFlowable(BackpressureStrategy.LATEST);
            }

            @NonNull
            @Override
            protected Observable<ApiResponse<ResponseBody>> createCall() {
                LogUtils.d("createCall");
                mSpUtils.putData(getHistorySpKey(metal, period), Calendar.getInstance());
                return mService.getHistoryByPeriod(0, getMetalString(metal), getDataTimeType(period))
                        .map(new Function<Response<ResponseBody>, ApiResponse<ResponseBody>>() {
                            @Override
                            public ApiResponse<ResponseBody> apply(Response<ResponseBody> responseBody) throws Exception {
                                return new ApiResponse(responseBody);
                            }
                        });
            }
        }.asObservable();
    }

    public Observable<List<HistoryEntity>> getHistoryDatasFromDb(
            Constants.MetalType type, Constants.PeriodType period) {
        switch (period) {
            case DAY:
                return getDayHistoryDatas(type);
            case WEEK:
                return getWeekHistoryDatas(type);
            case MONTH:
                return getMonthHistoryDatas(type);
            default:
                throw new IllegalArgumentException("period is invalid");
        }
    }

    private Observable<List<HistoryEntity>> getMonthHistoryDatas(Constants.MetalType type) {
        return mRoom.monthHistoryDao().query(type.ordinal()).toObservable()
                .map(new Function<List<MonthHistory>, List<HistoryEntity>>() {
                    @Override
                    public List<HistoryEntity> apply(List<MonthHistory> list) throws Exception {
                        return convertEntityList(list);
                    }
                });
    }

    private Observable<List<HistoryEntity>> getWeekHistoryDatas(Constants.MetalType type) {
        return mRoom.weekHistoryDao().query(type.ordinal()).toObservable()
                .map(new Function<List<WeekHistory>, List<HistoryEntity>>() {
                    @Override
                    public List<HistoryEntity> apply(List<WeekHistory> list) throws Exception {
                        return convertEntityList(list);
                    }
                });
    }

    private Observable<List<HistoryEntity>> getDayHistoryDatas(Constants.MetalType type) {
        return mRoom.dayHistoryDao().query(type.name()).toObservable()
                .map(new Function<List<DayHistory>, List<HistoryEntity>>() {
                    @Override
                    public List<HistoryEntity> apply(List<DayHistory> list) throws Exception {
                        LogUtils.d("type:" + type + " size:" + list.size());
                        return convertEntityList(list);
                    }
                });
    }

    private void putHistoryDatas(
            Constants.MetalType metal, Constants.PeriodType period, List<HistoryEntity> list) {
        switch (period) {
            case DAY:
                putDayHistory(metal, list);
            case WEEK:
                putWeekHistory(metal, list);
            case MONTH:
                putMonthHistory(metal, list);
            default:
                break;
        }
    }

    private void putDayHistory(Constants.MetalType type, List<HistoryEntity> list) {
        List<DayHistory> historyList = convertHistoryList(DayHistory.class, type, list);
        DayHistoryDao dao = mRoom.dayHistoryDao();
        dao.insertHistories(historyList);
    }

    private void putWeekHistory(Constants.MetalType type, List<HistoryEntity> list) {
        List<WeekHistory> historyList = convertHistoryList(WeekHistory.class, type, list);
        WeekHistoryDao dao = mRoom.weekHistoryDao();
        dao.insertHistories(historyList);
    }

    private void putMonthHistory(Constants.MetalType type, List<HistoryEntity> list) {
        List<MonthHistory> historyList = convertHistoryList(MonthHistory.class, type, list);
        MonthHistoryDao dao = mRoom.monthHistoryDao();
        dao.insertHistories(historyList);
    }

    private <T extends History> List<T> convertHistoryList(
            Class<T> clazz, Constants.MetalType type, List<HistoryEntity> list) {
        List<T> entityList = new ArrayList<>();
        for (HistoryEntity entry : list) {
            T history = null;
            try {
                history = clazz.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            history.set(type.name(), entry);
            entityList.add(history);
        }

        return entityList;
    }

    private String getHistorySpKey(Constants.MetalType metal, Constants.PeriodType period) {
        return SP_KEY_HISTORY_PREFIX + metal + "_" + period;
    }

    private List<HistoryEntity> parseHistoryDatasResponse(Constants.MetalType metal, String response) {
        // LogUtils.d("metalId:" + metal + " response:" + response);
        List<HistoryEntity> list = new ArrayList<>();
        Document doc = Jsoup.parse(response);
        Element body = doc.body();

        try {
            JSONObject jsonObject = new JSONObject(body.text());
            JSONArray nameArray = jsonObject.names();
            for (int index = 0; index < jsonObject.length(); index++) {
                // LogUtils.d("nameArray[" + index + "]:" + nameArray.getString(index));
                if (nameArray.getString(index).equals("picdata")) {
                    String strResult = jsonObject.getString(nameArray.getString(index));
                    // LogUtils.d("strResultReplace:" + strResult.replace(":", "-"));//JSON字串中含有:会报错
                    JSONArray resultArray = new JSONArray(strResult.replace(":", "-"));//JSON字串中含有:会报错
                    LogUtils.d("resultArray length:" + resultArray.length());
                    for (int index1 = 0; index1 < resultArray.length(); index1++) {
                        JSONArray jsonData = resultArray.getJSONArray(index1);
                        list.add(jsonArrayToHistory(jsonData));
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private HistoryEntity jsonArrayToHistory(JSONArray jsonData) {
        HistoryEntity entity = new HistoryEntity();

        try {
            String[] dateArray = jsonData.getString(0).split("-");
            Calendar cald = Calendar.getInstance();
            cald.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            cald.set(Integer.parseInt(dateArray[0]),
                    Integer.parseInt(dateArray[1]) - 1,//0-based
                    Integer.parseInt(dateArray[2]),
                    0,
                    0,
                    0
            );
            cald.set(Calendar.MILLISECOND, 0);
            entity.mDate = cald;
            entity.mHigh = (float) jsonData.getDouble(1);
            entity.mLow = (float) jsonData.getDouble(2);
            entity.mOpen = (float) jsonData.getDouble(3);
            entity.mClose = (float) jsonData.getDouble(4);
            //LogUtils.d("history:" + entity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }

    public Observable<Resource<List<RealtimeEntity>>> getTodayDatas(final Constants.MetalType type) {
        LogUtils.d("getTodayDatas type:" + type);
        return new NetworkBoundResource<List<RealtimeEntity>, ResponseBody>() {
            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                String response = null;
                try {
                    response = item.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<RealtimeEntity> list = parseTodayDatasResponse(type, response);
                putTodayDatas(type, list);
            }

            @Override
            protected boolean shouldFetch(Resource<List<RealtimeEntity>> dbResource) {
                if (!isMarketOpening()) {
                    return false;
                }

                Calendar now = Calendar.getInstance();
                Calendar calendar = mSpUtils.getData(getHistorySpKey(type, Constants.PeriodType.REALTIME), now);
                if (now != calendar && now.getTimeInMillis() - calendar.getTimeInMillis() < 60 * 1000) {
                    LogUtils.w("fetch 1 min ago");
                    return false;
                }
                return true;
            }

            @NonNull
            @Override
            protected Flowable<List<RealtimeEntity>> loadFromDb() {
                return getTodayDatasFromDb(type).toFlowable(BackpressureStrategy.LATEST);
            }

            @NonNull
            @Override
            protected Observable<ApiResponse<ResponseBody>> createCall() {
                mSpUtils.putData(getHistorySpKey(type, Constants.PeriodType.REALTIME), Calendar.getInstance());
                return mService.getHistoryByPeriod(0, getMetalString(type),
                        getDataTimeType(Constants.PeriodType.REALTIME)).map(new Function<Response<ResponseBody>, ApiResponse<ResponseBody>>() {
                    @Override
                    public ApiResponse<ResponseBody> apply(Response<ResponseBody> responseBody) throws Exception {
                        return new ApiResponse(responseBody);
                    }
                });
            }
        }.asObservable();
    }

    //开盘首日可能不是周一, 没有0点价格, 收盘日可能不是周六
    private List<RealtimeEntity> parseTodayDatasResponse(Constants.MetalType type, String response) {
        //LogUtils.d("parseTodayDatasResponse:" + response);
        PriceInfo priceInfo = new PriceInfo();
        List<RealtimeEntity> list = new ArrayList<RealtimeEntity>();
        Document doc = Jsoup.parse(response);
        Element body = doc.body();
        //LogUtils.d("body.length:" + body.text().length() + " body:" + body.text());
        Calendar calendar = Calendar.getInstance();
        mSpUtils.putData(Constants.SP_KEY_METAL_LAST_FETCH_TIME, calendar);
        setMarketOpen(true);
        if (body.text().length() <= 0) {//闭市
            LogUtils.w(type + " no data");
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            mSpUtils.putData(Constants.SP_KEY_METAL_LAST_OPEN, calendar);
            setMarketOpen(false);
            return list;
        }

        boolean hasPreClose = false;
        try {
            JSONObject jsonObject = new JSONObject(body.text());
            JSONArray nameArray = jsonObject.names();
            for (int index = 0; index < jsonObject.length(); index++) {
                //LogUtils.d("nameArray[" + index + "]:" + nameArray.getString(index));
                if (nameArray.getString(index).equals("picdata")) {
                    String strResult = jsonObject.getString(nameArray.getString(index));
                    //LogUtils.d("strResultReplace:" + strResult.replace(":", "-"));
                    //JSON字串中含有:会报错
                    JSONArray resultArray = new JSONArray(strResult.replace(":", "-"));
                    LogUtils.d(type + " resultArray length:" + resultArray.length());
                    RealtimeEntity preEntity = null, entity = null;

                    for (int index1 = 0; index1 < resultArray.length(); index1++) {
                        JSONArray jsonData = resultArray.getJSONArray(index1);
                        entity = jsonArrayToRealtime(jsonData);
                        if (!hasPreClose && entity.mTime.get(Calendar.HOUR_OF_DAY) == 6
                                && preEntity.mTime.get(Calendar.HOUR_OF_DAY) <= 5) {//收盘可能早于5点钟
                            priceInfo.preClose = preEntity;
                            priceInfo.currOpen = entity;
                            hasPreClose = true;
                            //LogUtils.d("priceInfo:" + priceInfo + " preEntity:" + preEntity);
                        }
                        list.add(entity);
                        preEntity = entity;
                    }

                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (hasPreClose) {//开盘首日没有美国收市的记录
            priceInfo.priceAt0 = list.get(0);
        } else {
            priceInfo.currOpen = list.get(0);
            //判断是否周六或者其它节假日前收盘后获取数据
            RealtimeEntity entity = list.get(list.size() - 1);
            long diff = Calendar.getInstance().getTimeInMillis() - entity.mTime.getTimeInMillis();
            if (diff > MARKET_CLOSE_TIMEOUT) {//超过现在2小时没有数据视为收盘
                LogUtils.d("setTimeInMillis: No data over 2 hours");
                calendar.setTimeInMillis(entity.mTime.getTimeInMillis());
                setMarketOpen(false);
            }
        }
        mSpUtils.putData(Constants.SP_KEY_METAL_LAST_OPEN, calendar);

        //更新收市价
        //LogUtils.d("putData type:" + type + " priceInfo:" + priceInfo);
        mSpUtils.putData(SP_KEY_METAL_NOW_QUOTE + type, priceInfo);
        return list;
    }

    private RealtimeEntity jsonArrayToRealtime(JSONArray jsonData) {
        // LogUtils.d("jsonArrayToRealtime " + jsonData);
        RealtimeEntity realtime = new RealtimeEntity();

        try {
            String[] timeArray = jsonData.getString(0).split("-");
            String[] dateArray = jsonData.getString(2).split("-");
            realtime.mPrice = (float) jsonData.getDouble(1);
            Calendar cald = Calendar.getInstance();
            cald.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            cald.set(Integer.parseInt(dateArray[0]),
                    Integer.parseInt(dateArray[1]) - 1,//0-based
                    Integer.parseInt(dateArray[2]),
                    Integer.parseInt(timeArray[0]),
                    Integer.parseInt(timeArray[1]),
                    Integer.parseInt(timeArray[2])
            );
            cald.set(Calendar.MILLISECOND, 0);
            realtime.mTime = cald;
            //Logger.debug("realtime:" + realtime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return realtime;
    }

    Observable<List<RealtimeEntity>> getTodayDatasFromDb(Constants.MetalType type) {
        RealtimeDao dao = mRoom.realtimeDao();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return dao.queryAfter(type.ordinal(), calendar.getTimeInMillis()).toObservable()
                .map(new Function<List<Realtime>, List<RealtimeEntity>>() {
                    @Override
                    public List<RealtimeEntity> apply(List<Realtime> realtimes) throws Exception {
                        List<RealtimeEntity> entityList = new ArrayList<>();
                        for (Realtime entry : realtimes) {
                            RealtimeEntity realtime = new RealtimeEntity(entry.mTime, entry.mPrice);
                            entityList.add(realtime);
                        }
                        return entityList;
                    }
                });

    }

    void putTodayDatas(Constants.MetalType type, @NonNull List<RealtimeEntity> list) {
        RealtimeDao dao = mRoom.realtimeDao();
        List<Realtime> entityList = new ArrayList<>();
        for (RealtimeEntity entry : list) {
            Realtime realtime = new Realtime(type, entry.mTime, entry.mPrice);
            entityList.add(realtime);
        }
        dao.insertRealtimes(entityList);
    }

    private Constants.MetalType getMetalType(String value) {
        Constants.MetalType type = null;
        for (Map.Entry<Constants.MetalType, String> Id : sMetalIdMap.entrySet()) {
            if (Id.getValue().equals(value)) {
                type = Id.getKey();
                //LogUtils.d("mId:" + queryId);
                break;
            }
        }

        return type;
    }

    private String getMetalString(Constants.MetalType metal) {
        String queryId = "";
        for (Map.Entry<Constants.MetalType, String> Id : sMetalIdMap.entrySet()) {
            if (Id.getKey() == metal) {
                queryId = Id.getValue();
                //LogUtils.d("mId:" + queryId);
                break;
            }
        }

        return queryId;
    }
}
