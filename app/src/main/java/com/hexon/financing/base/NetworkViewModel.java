package com.hexon.financing.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.hexon.financing.MyApplication;
import com.hexon.mvvm.base.BaseViewModel;
import com.hexon.repository.Constants;
import com.hexon.util.SharedPrefsUtils;

/**
 * Copyright (C), 2020-2025
 * FileName    : NetworkViewModel
 * Description :
 * Author      : Hexon
 * Date        : 2020/11/26 9:56
 * Version     : V1.0
 */
public abstract class NetworkViewModel extends BaseViewModel {
    protected long mUpdatePeriod = Long.MAX_VALUE;
    protected MutableLiveData<Boolean> mIsStartRefresh = new MutableLiveData<>();
    SharedPrefsUtils mSp;

    public NetworkViewModel(@NonNull Application application) {
        super(application);
        mSp = SharedPrefsUtils.getInstance(application);
        initData();
    }

    private void initData() {
        MyApplication.getInstance().getUpdatePeriod()
                .observe(ProcessLifecycleOwner.get(), new Observer<Long>() {
                    @Override
                    public void onChanged(Long period) {
                        if (mUpdatePeriod == Long.MAX_VALUE) {// first time
                            mUpdatePeriod = period;
                        } else if (mUpdatePeriod != period) {
                            mUpdatePeriod = period;
                            if (mUpdatePeriod == -1L) {//no network
                                stopGetData();
                            } else {
                                startGetData();
                            }
                        }
                    }
                });
    }

    public abstract boolean isMarketOpen();

    public MutableLiveData<Boolean> getIsStartRefresh() {
        return mIsStartRefresh;
    }

    public long getUpdatePeriod() {
        return mUpdatePeriod;
    }

    public long getWifiUpdatePeriod() {
        return mSp.getData(Constants.SP_KEY_CUSTOM_WIFI_UPDATE_PERIOD, Constants.WIFI_UPDATE_PERIOD);
    }

    public void setWifiUpdatePeriod(long period) {
        mSp.putData(Constants.SP_KEY_CUSTOM_WIFI_UPDATE_PERIOD, period);
        MyApplication.getInstance().setUpdatePeriod(period);
    }

    public long getMobileUpdatePeriod() {
        return mSp.getData(Constants.SP_KEY_CUSTOM_MOBILE_UPDATE_PERIOD, Constants.MOBILE_UPDATE_PERIOD);
    }

    public void setMobileUpdatePeriod(long period) {
        mSp.putData(Constants.SP_KEY_CUSTOM_MOBILE_UPDATE_PERIOD, period);
        MyApplication.getInstance().setUpdatePeriod(period);
    }

    @Override
    public void onResume() {
        startGetData();
    }

    @Override
    public void onPause() {
        stopGetData();
    }

    public abstract void startGetData();

    public abstract void stopGetData();
}
