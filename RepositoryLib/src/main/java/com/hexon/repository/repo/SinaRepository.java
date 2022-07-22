package com.hexon.repository.repo;

import static com.hexon.repository.Constants.DEFAULT_CONNECT_TIMEOUT;
import static com.hexon.repository.Constants.PeriodType;

import androidx.annotation.NonNull;

import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.chartlib.stock.model.RealtimeEntity;
import com.hexon.mvvm.base.BaseApplication;
import com.hexon.repository.Constants;
import com.hexon.repository.base.ApiResponse;
import com.hexon.repository.base.BaseRepository;
import com.hexon.repository.base.IForexRepository;
import com.hexon.repository.base.NetworkBoundResource;
import com.hexon.repository.base.Resource;
import com.hexon.repository.database.room.SinaDatabase;
import com.hexon.repository.database.room.entity.DayHistory;
import com.hexon.repository.database.room.entity.FiveMinutesHistory;
import com.hexon.repository.database.room.entity.FourHoursHistory;
import com.hexon.repository.database.room.entity.OneHourHistory;
import com.hexon.repository.database.room.entity.OneMinuteHistory;
import com.hexon.repository.database.room.entity.ThirtyMinutesHistory;
import com.hexon.repository.database.room.entity.TwoHoursHistory;
import com.hexon.repository.http.SinaService;
import com.hexon.repository.model.ExchangeRate;
import com.hexon.util.LogUtils;
import com.hexon.util.SharedPrefsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
 * Copyright (C), 2022-2040
 * ClassName: SinaRepository
 * Description:
 * Author: Hexon
 * Date: 2022/7/20 16:51
 * Version V1.0
 */
public class SinaRepository extends BaseRepository implements IForexRepository {
    static SinaRepository sInstance;
    SinaService mService;
    SharedPrefsUtils mSpUtils;
    SinaDatabase mRoom;
    private static final int DEFAULT_FETCH_COUNT = 10000000;
    private List<ExchangeRate> mRealtimeForexList = new ArrayList<>();

    private SinaRepository(BaseApplication application) {
        super(application);
        //HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();//打印日志
        //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//设定日志级别
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        //builder.addInterceptor(httpLoggingInterceptor);//添加拦截器
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SinaService.FOREX_URL)
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())//返回String类型
                .addConverterFactory(GsonConverterFactory.create())//返回Gson类型
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加 RxJava 适配器
                .build();
        mService = retrofit.create(SinaService.class);
        mRoom = SinaDatabase.getInstance(mApp);
        initData();
    }

    private void initData() {
        for (Constants.ForexType forexType : Constants.ForexType.values()) {
            mRealtimeForexList.add(forexType.ordinal(), new ExchangeRate());
        }
        for (Constants.ForexType forexType : Constants.ForexType.values()) {
            getForexData(forexType, PeriodType.ONE_MINUTE)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            getForexData(forexType, PeriodType.FIVE_MINUTES)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            getForexData(forexType, PeriodType.THIRTY_MINUTES)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            getForexData(forexType, PeriodType.ONE_HOUR)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            getForexData(forexType, PeriodType.TWO_HOURS)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            getForexData(forexType, PeriodType.FOUR_HOURS)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            getForexData(forexType, PeriodType.DAY)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    public static SinaRepository getInstance(BaseApplication application) {
        if (sInstance == null) {
            synchronized (SinaRepository.class) {
                if (sInstance == null) {
                    sInstance = new SinaRepository(application);
                }
            }
        }

        return sInstance;
    }

    @Override
    public Boolean checkMarketOpen() {
        return true;
    }

    public Observable<Resource<List<HistoryEntity>>> getForexData(
            Constants.ForexType forexType, Constants.PeriodType period) {
        return new NetworkBoundResource<List<HistoryEntity>, ResponseBody>() {

            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                LogUtils.d("saveCallResult " + item);
                List<HistoryEntity> list = null;
                try {
                    list = parseHistoryDataResponse(forexType, item.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                putHistoryData(forexType, period, list);
            }

            @Override
            protected boolean shouldFetch(Resource<List<HistoryEntity>> dbResource) {
                LogUtils.d("shouldFetch " + dbResource);
                if (dbResource == null) {
                    return true;
                }

                return false;
            }

            @NonNull
            @Override
            protected Flowable<List<HistoryEntity>> loadFromDb() {
                LogUtils.d("loadFromDb");
                return getHistoryDataFromDb(forexType, period)
                        .toFlowable(BackpressureStrategy.LATEST);
            }

            @NonNull
            @Override
            protected Observable<ApiResponse<ResponseBody>> createCall() {
                LogUtils.d("createCall");
                mSpUtils.putData(getHistorySpKey(forexType, period), Calendar.getInstance());
                if (period != PeriodType.DAY) {
                    return mService.getForexMinKlineData(forexType.name(), getPeriodByMinute(period),
                            DEFAULT_FETCH_COUNT).map(new Function<Response<ResponseBody>, ApiResponse<ResponseBody>>() {
                        @Override
                        public ApiResponse<ResponseBody> apply(Response<ResponseBody> responseBody) throws Exception {
                            return new ApiResponse(responseBody);
                        }
                    });
                } else {
                    return mService.getForexDayKlineData(forexType.name()).map(new Function<Response<ResponseBody>, ApiResponse<ResponseBody>>() {
                        @Override
                        public ApiResponse<ResponseBody> apply(Response<ResponseBody> responseBody) throws Exception {
                            return new ApiResponse(responseBody);
                        }
                    });
                }
            }
        }.asObservable();
    }

    private void putHistoryData(Constants.ForexType forexType, PeriodType period, List<HistoryEntity> list) {
    }

    private List<HistoryEntity> parseHistoryDataResponse(Constants.ForexType forexType, String response) {
        LogUtils.d("forexType:" + forexType + " response:" + response);
        List<HistoryEntity> list = new ArrayList<>();
        return list;
    }

    private Observable<List<HistoryEntity>> getHistoryDataFromDb(Constants.ForexType forexType, PeriodType period) {
        switch (period) {
            case ONE_MINUTE:
                return mRoom.oneMinuteHistoryDao().query(forexType.name()).toObservable()
                        .map(new Function<List<OneMinuteHistory>, List<HistoryEntity>>() {
                            @Override
                            public List<HistoryEntity> apply(List<OneMinuteHistory> list) throws Exception {
                                LogUtils.d("type:" + forexType + " size:" + list.size());
                                return convertEntityList(list);
                            }
                        });
            case FIVE_MINUTES:
                return mRoom.fiveMinutesHistoryDao().query(forexType.name()).toObservable()
                        .map(new Function<List<FiveMinutesHistory>, List<HistoryEntity>>() {
                            @Override
                            public List<HistoryEntity> apply(List<FiveMinutesHistory> list) throws Exception {
                                LogUtils.d("type:" + forexType + " size:" + list.size());
                                return convertEntityList(list);
                            }
                        });
            case THIRTY_MINUTES:
                return mRoom.thirtyMinutesHistoryDao().query(forexType.name()).toObservable()
                        .map(new Function<List<ThirtyMinutesHistory>, List<HistoryEntity>>() {
                            @Override
                            public List<HistoryEntity> apply(List<ThirtyMinutesHistory> list) throws Exception {
                                LogUtils.d("type:" + forexType + " size:" + list.size());
                                return convertEntityList(list);
                            }
                        });
            case ONE_HOUR:
                return mRoom.oneHourHistoryDao().query(forexType.name()).toObservable()
                        .map(new Function<List<OneHourHistory>, List<HistoryEntity>>() {
                            @Override
                            public List<HistoryEntity> apply(List<OneHourHistory> list) throws Exception {
                                LogUtils.d("type:" + forexType + " size:" + list.size());
                                return convertEntityList(list);
                            }
                        });
            case TWO_HOURS:
                return mRoom.twoHoursHistoryDao().query(forexType.name()).toObservable()
                        .map(new Function<List<TwoHoursHistory>, List<HistoryEntity>>() {
                            @Override
                            public List<HistoryEntity> apply(List<TwoHoursHistory> list) throws Exception {
                                LogUtils.d("type:" + forexType + " size:" + list.size());
                                return convertEntityList(list);
                            }
                        });
            case FOUR_HOURS:
                return mRoom.fourHoursHistoryDao().query(forexType.name()).toObservable()
                        .map(new Function<List<FourHoursHistory>, List<HistoryEntity>>() {
                            @Override
                            public List<HistoryEntity> apply(List<FourHoursHistory> list) throws Exception {
                                LogUtils.d("type:" + forexType + " size:" + list.size());
                                return convertEntityList(list);
                            }
                        });
            case DAY:
                return mRoom.dayHistoryDao().query(forexType.name()).toObservable()
                        .map(new Function<List<DayHistory>, List<HistoryEntity>>() {
                            @Override
                            public List<HistoryEntity> apply(List<DayHistory> list) throws Exception {
                                LogUtils.d("type:" + forexType + " size:" + list.size());
                                return convertEntityList(list);
                            }
                        });
            default:
                throw new IllegalArgumentException("period is invalid");
        }
    }

    private int getPeriodByMinute(PeriodType period) {
        int result = 0;
        switch (period) {
            case ONE_MINUTE:
                result = 1;
                break;
            case FIVE_MINUTES:
                result = 5;
                break;
            case THIRTY_MINUTES:
                result = 30;
                break;
            case ONE_HOUR:
                result = 60;
                break;
            case TWO_HOURS:
                result = 120;
                break;
            case FOUR_HOURS:
                result = 240;
                break;
        }

        return result;
    }

    private String getHistorySpKey(Constants.ForexType forexType, Constants.PeriodType period) {
        return Constants.SP_KEY_HISTORY_PREFIX + "_" + forexType + "_" + period;
    }

    @Override
    public Observable<Resource<List<RealtimeEntity>>> getRealtimeDataForChart(
            Constants.ForexType type) {
        return null;
    }

    @Override
    public Observable<Resource<List<HistoryEntity>>> getHistoryDataForChart(
            Constants.ForexType type, Constants.PeriodType period) {
        return null;
    }

    public Observable<Resource<ExchangeRate>> getRealtimeForexData(Constants.ForexType forexType) {
        return new NetworkBoundResource<ExchangeRate, ResponseBody>() {

            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                //LogUtils.d("saveCallResult " + item);
                try {
                    parseRealtimeDataResponse(forexType, item.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean shouldFetch(Resource<ExchangeRate> dbResource) {
                return true;
            }

            @NonNull
            @Override
            protected Flowable<ExchangeRate> loadFromDb() {
                return Flowable.just(mRealtimeForexList.get(forexType.ordinal()));
            }

            @NonNull
            @Override
            protected Observable<ApiResponse<ResponseBody>> createCall() {
                return mService.getForexRealtimeData(getSinaForexType(forexType)).map(
                        new Function<Response<ResponseBody>, ApiResponse<ResponseBody>>() {
                            @Override
                            public ApiResponse<ResponseBody> apply(Response<ResponseBody> response) throws Exception {
                                return new ApiResponse<ResponseBody>(response);
                            }
                        }
                );
            }
        }.asObservable();
    }

    private String getSinaForexType(Constants.ForexType forexType) {
        String result = null;
        switch (forexType) {
            case USDCNH:
                result = "fx_susdcnh";
                break;
            case USDCNY:
                result = "fx_susdcny";
                break;
            case DINIW:
                result = "DINIW";
                break;
            case HKDCNY:
                result = "fx_shkdcny";
                break;
        }
        return result;
    }

    private ExchangeRate parseRealtimeDataResponse(Constants.ForexType forexType, String response) {
        //LogUtils.d("parseRealtimeDataResponse " + forexType + " response " + response);
        ExchangeRate entity = mRealtimeForexList.get(forexType.ordinal());
        int quotation_start = response.indexOf('\"');
        int quotation_end = response.lastIndexOf('\"');
        String temp = response.substring(quotation_start + 1, quotation_end);
        String[] elements = temp.split(",");
        //LogUtils.d("temp:" + temp + " elements.length:" + elements.length);
        entity.mName = elements[9];
        entity.mUpdateTime = elements[elements.length - 1] + " " + elements[0];
        entity.mCurrPrice = Float.parseFloat(elements[8]);
        entity.mClose = Float.parseFloat(elements[3]);
        entity.mOpen = Float.parseFloat(elements[5]);
        entity.mHighPrice = Float.parseFloat(elements[6]);
        entity.mLowPrice = Float.parseFloat(elements[7]);
        entity.mAmplitudePercent = 0.0001f * Float.parseFloat(elements[4]);
        entity.mVolatilityRange = (float) (Math.round(entity.mAmplitudePercent * 10000)) / 10000;
        entity.mAmplitudePercent = entity.mVolatilityRange / entity.mClose;
        entity.mAmplitudePercent = (float) (Math.round(entity.mAmplitudePercent * 10000)) / 10000;
        entity.mChange = (entity.mCurrPrice - entity.mClose);
        entity.mChange = (float) (Math.round(entity.mChange * 10000)) / 10000;
        return entity;
    }

    public List<Observable<Resource<ExchangeRate>>> getAllRealtimeForexData() {
        List<Observable<Resource<ExchangeRate>>> list = new ArrayList<>();
        for (Constants.ForexType forexType : Constants.ForexType.values()) {
            list.add(forexType.ordinal(), getRealtimeForexData(forexType));
        }
        return list;
    }
}
