package com.hexon.financing.ui.noblemetal;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.hexon.chartlib.stock.model.RealtimeQuotesEntity;
import com.hexon.financing.MyApplication;
import com.hexon.financing.base.NetworkViewModel;
import com.hexon.repository.Constants;
import com.hexon.repository.IcbcRepository;
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
    ArrayList<MutableLiveData<RealtimeQuotesEntity>> mList =
            new ArrayList<>(Constants.MetalType.values().length);
    private Disposable mDisposable;
    private Constants.NobleMetalBank mBank;

    public NobleMetalViewModel(@NonNull Application application) {
        super(application);
        initList();
    }

    ArrayList<MutableLiveData<RealtimeQuotesEntity>> getDataList() {
        return mList;
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
            startGetIcbcQuotes();
        }
    }

    private void startGetIcbcQuotes() {
        LogUtils.d("startGetQuotes");
        if (mDisposable != null && !mDisposable.isDisposed()) {
            LogUtils.w("dispose");
            mDisposable.dispose();
        }

        if (mIcbcRepo.isMarketOpening()) {
            mIsStartRefresh.postValue(true);
        }
        mDisposable = Flowable.interval(0, mUpdateCycle, TimeUnit.MILLISECONDS)
                .takeUntil(aLong -> {
                    mIsStartRefresh.postValue(false);
                    return !mIcbcRepo.isMarketOpening();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //LogUtils.d("times:" + aLong);
                    fetchIcbcRealtimeQuotes();
                    if (mIcbcRepo.isMarketOpening()) {
                        mIsStartRefresh.postValue(true);
                    }
                });
    }

    private void initList() {
        mList.clear();
        for (Constants.MetalType type : Constants.MetalType.values()) {
            MutableLiveData<RealtimeQuotesEntity> entity = new MutableLiveData<>();
            entity.setValue(new RealtimeQuotesEntity());
            mList.add(entity);
        }
    }

    @SuppressLint("CheckResult")
    private void fetchIcbcRealtimeQuotes() {
        mIcbcRepo.getAllRealtimeQuotes()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(listResource -> {
                    if (listResource.getStatus() == Status.SUCCESS) {
                        List<RealtimeQuotesEntity> list = listResource.getData();
                        for (RealtimeQuotesEntity entity : list) {
                            Constants.MetalType type = Constants.MetalType.values()[entity.mType];
                            LogUtils.d("fetchIcbcRealtimeQuotes:" + entity);
                            mList.get(type.ordinal()).postValue(entity);
                        }
                    }
                });
    }

    @Override
    public void stopGetData() {
        if (mBank == Constants.NobleMetalBank.ICBC) {
            stopGetIcbcQuotes();
        }
    }

    private void stopGetIcbcQuotes() {
        LogUtils.d("stopGetIcbcQuotes");
        mIsStartRefresh.postValue(false);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            //LogUtils.d("dispose");
            mDisposable.dispose();
        }

        mDisposable = null;
    }

    public void setNobleMetal(Constants.NobleMetalBank bank) {
        mBank = bank;
        startGetData();
    }
}
