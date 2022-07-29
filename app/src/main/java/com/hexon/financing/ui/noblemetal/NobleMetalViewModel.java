package com.hexon.financing.ui.noblemetal;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.chartlib.stock.model.RealtimeEntity;
import com.hexon.chartlib.stock.model.RealtimeQuotesEntity;
import com.hexon.financing.MyApplication;
import com.hexon.financing.base.NetworkViewModel;
import com.hexon.repository.Constants;
import com.hexon.repository.repo.IcbcRepository;
import com.hexon.repository.base.Status;
import com.hexon.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Copyright (C), 2022-2030
 * ClassName: NobleMetalViewModel
 * Description:
 * Author: Hexon
 * Date: 2022/4/14 15:37
 * Version V1.0
 */
public class NobleMetalViewModel extends NetworkViewModel {
    IcbcRepository mIcbcRepo = IcbcRepository.getInstance(MyApplication.getInstance());
    ArrayList<MutableLiveData<RealtimeQuotesEntity>> mRealtimeQuotesList =
            new ArrayList<>(Constants.MetalType.values().length);
    public MutableLiveData<RealtimeQuotesEntity> mCurrRealtimeQuotes;
    MutableLiveData<List<HistoryEntity>> mHistoryList = new MutableLiveData<List<HistoryEntity>>();
    MutableLiveData<List<RealtimeEntity>> mRealtimeList = new MutableLiveData<List<RealtimeEntity>>();
    private Disposable mDisposable;
    private Constants.NobleMetalBank mBank;
    public MutableLiveData<String> mCurrMetalName = new MutableLiveData<>();
    private Constants.MetalType mCurrMetalType;
    private FetchDataType mFetchDataType = FetchDataType.INVALID;

    public enum FetchDataType {
        INVALID,
        ALL_DATA_REALTIME_SUMMARY,
        ONE_DATA_REALTIME_DETAIL,
        HISTORY_LIST_DATA,
        REALTIME_LIST_DATA,
    }

    public NobleMetalViewModel(@NonNull Application application) {
        super(application);
        initList();
        LogUtils.d("NobleMetalViewModel create");
    }

    ArrayList<MutableLiveData<RealtimeQuotesEntity>> getRealtimeQuotesList() {
        return mRealtimeQuotesList;
    }

    @Override
    public boolean isMarketOpen() {
        if (mBank == Constants.NobleMetalBank.ICBC) {
            return mIcbcRepo.isMarketOpening();
        } else {
            return false;
        }
    }

    @Override
    public void startGetData() {
        if (mBank == Constants.NobleMetalBank.ICBC) {
            switch (mFetchDataType) {
                case ALL_DATA_REALTIME_SUMMARY:
                    startGetIcbcAllQuotes();
                    break;
                case ONE_DATA_REALTIME_DETAIL:
                    startGetIcbcQuotes(mCurrMetalType);
                    break;
                case REALTIME_LIST_DATA:
                    startGetIcbcRealtimeList(mCurrMetalType);
                    break;
                case HISTORY_LIST_DATA:
                    startGetIcbcHistoryList(mCurrMetalType);
                    break;
                case INVALID:
                    LogUtils.e("invalid");
                    break;
            }
        }
    }

    private void startGetIcbcAllQuotes() {
        LogUtils.d("startGetIcbcAllQuotes");
        if (mDisposable != null && !mDisposable.isDisposed()) {
            LogUtils.w("dispose");
            mDisposable.dispose();
        }

        if (mIcbcRepo.isMarketOpening()) {
            mIsStartRefresh.postValue(true);
        }
        mDisposable = Flowable.interval(0, mUpdatePeriod, TimeUnit.MILLISECONDS)
                .takeUntil(aLong -> {
                    mIsStartRefresh.postValue(false);
                    return !mIcbcRepo.isMarketOpening();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //LogUtils.d("times:" + aLong);
                    fetchIcbcAllRealtimeQuotes();
                    if (mIcbcRepo.isMarketOpening()) {
                        mIsStartRefresh.postValue(true);
                    }
                });
    }

    private void startGetIcbcQuotes(Constants.MetalType metalType) {
        LogUtils.d("startGetIcbcQuotes " + metalType);
        stopGetData();
        mDisposable = Flowable.interval(0, mUpdatePeriod, TimeUnit.MILLISECONDS)
                .takeUntil(aLong -> {
                    mIsStartRefresh.postValue(false);
                    return !mIcbcRepo.isMarketOpening();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //LogUtils.d("times:" + aLong);
                    fetchIcbcRealtimeQuotes(metalType);
                    if (mIcbcRepo.isMarketOpening()) {
                        mIsStartRefresh.postValue(true);
                    }
                });
    }

    private void startGetIcbcRealtimeList(Constants.MetalType metalType) {
        LogUtils.d("startGetIcbcRealtimeList " + metalType);
    }

    private void startGetIcbcHistoryList(Constants.MetalType metalType) {
        LogUtils.d("startGetIcbcHistoryList " + metalType);
    }

    private void initList() {
        mRealtimeQuotesList.clear();
        for (Constants.MetalType type : Constants.MetalType.values()) {
            MutableLiveData<RealtimeQuotesEntity> entity = new MutableLiveData<>();
            entity.setValue(new RealtimeQuotesEntity());
            mRealtimeQuotesList.add(entity);
        }
    }

    @SuppressLint("CheckResult")
    private void fetchIcbcAllRealtimeQuotes() {
        mIcbcRepo.getAllRealtimeQuotes()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(listResource -> {
                    if (listResource.getStatus() == Status.SUCCESS) {
                        List<RealtimeQuotesEntity> list = listResource.getData();
                        for (RealtimeQuotesEntity entity : list) {
                            Constants.MetalType type = Constants.MetalType.values()[entity.mType];
                            //LogUtils.d("fetchIcbcRealtimeQuotes:" + entity);
                            mRealtimeQuotesList.get(type.ordinal()).postValue(entity);
                        }
                    }
                });
    }

    private void fetchIcbcRealtimeQuotes(Constants.MetalType metalType) {
        mIcbcRepo.getRealtimeQuotes(metalType)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(resource -> {
                    if (resource.getStatus() == Status.SUCCESS) {
                        mCurrRealtimeQuotes.postValue(resource.getData());
                    }
                });
    }

    @Override
    public void stopGetData() {
        LogUtils.d("stopGetData");
        mIsStartRefresh.postValue(false);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            LogUtils.d("dispose");
            mDisposable.dispose();
        }

        mDisposable = null;
    }

    public void setNobleMetal(Constants.NobleMetalBank bank) {
        mBank = bank;
        startGetData();
    }

    public void setFetchDataType(FetchDataType type) {
        mFetchDataType = type;
        startGetData();
    }

    public void setNobleMetalType(Constants.MetalType currMetalType) {
        mCurrMetalType = currMetalType;
        mCurrMetalName.postValue(mIcbcRepo.getMetalName(currMetalType));
        mCurrRealtimeQuotes = mRealtimeQuotesList.get(currMetalType.ordinal());
    }

    public void updateOnce() {
        stopGetData();
        fetchIcbcAllRealtimeQuotes();
        mIsStartRefresh.postValue(false);
        startGetData();
    }
}
