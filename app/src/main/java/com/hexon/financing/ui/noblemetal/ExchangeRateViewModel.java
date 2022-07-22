package com.hexon.financing.ui.noblemetal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.hexon.chartlib.stock.model.RealtimeQuotesEntity;
import com.hexon.financing.MyApplication;
import com.hexon.financing.base.NetworkViewModel;
import com.hexon.mvvm.base.BaseViewModel;
import com.hexon.repository.Constants;
import com.hexon.repository.base.Resource;
import com.hexon.repository.model.ExchangeRate;
import com.hexon.repository.repo.IcbcRepository;
import com.hexon.repository.repo.SinaRepository;
import com.hexon.util.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ExchangeRateViewModel extends NetworkViewModel {
    SinaRepository mSinaRepo = SinaRepository.getInstance(MyApplication.getInstance());
    ArrayList<MutableLiveData<ExchangeRate>> mList =
            new ArrayList<>(Constants.ForexType.values().length);
    private Disposable mDisposable;

    public ExchangeRateViewModel(@NonNull Application application) {
        super(application);
        initList();
    }

    @Override
    public boolean isMarketOpen() {
        return mSinaRepo.checkMarketOpen();
    }

    @Override
    public void startGetData() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            LogUtils.w("dispose");
            mDisposable.dispose();
        }

        if (isMarketOpen()) {
            mIsStartRefresh.postValue(true);
        }

        mDisposable = Flowable.interval(0, mUpdatePeriod, TimeUnit.MILLISECONDS)
                .takeUntil(aLong -> {
                    mIsStartRefresh.postValue(false);
                    return !mSinaRepo.checkMarketOpen();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //LogUtils.d("times:" + aLong);
                    fetchForexData();
                    if (mSinaRepo.checkMarketOpen()) {
                        mIsStartRefresh.postValue(true);
                    }
                });
    }

    private void fetchForexData() {
        Observable.fromIterable(Arrays.asList(Constants.ForexType.values()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Constants.ForexType>() {
                    @Override
                    public void accept(Constants.ForexType forexType) throws Exception {
                        mSinaRepo.getRealtimeForexData(forexType)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Resource<ExchangeRate>>() {
                                    @Override
                                    public void accept(Resource<ExchangeRate> exchangeRateResource) throws Exception {
                                        mList.get(forexType.ordinal()).postValue(exchangeRateResource.getData());
                                    }
                                });
                    }
                });
        List<Observable<Resource<ExchangeRate>>> list = mSinaRepo.getAllRealtimeForexData();

    }

    @Override
    public void stopGetData() {
        mIsStartRefresh.postValue(false);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            //LogUtils.d("dispose");
            mDisposable.dispose();
        }

        mDisposable = null;
    }

    private void initList() {
        mList.clear();
        for (Constants.ForexType type : Constants.ForexType.values()) {
            MutableLiveData<ExchangeRate> entity = new MutableLiveData<>();
            entity.setValue(new ExchangeRate());
            mList.add(entity);
        }
    }

    ArrayList<MutableLiveData<ExchangeRate>> getDataList() {
        return mList;
    }
}
