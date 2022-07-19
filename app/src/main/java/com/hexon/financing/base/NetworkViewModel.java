package com.hexon.financing.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.hexon.financing.MyApplication;
import com.hexon.mvvm.base.BaseViewModel;

/**
 * Copyright (C), 2020-2025
 * FileName    : NetworkViewModel
 * Description :
 * Author      : Hexon
 * Date        : 2020/11/26 9:56
 * Version     : V1.0
 */
public abstract class NetworkViewModel extends BaseViewModel {
    protected long mUpdateCycle = Long.MAX_VALUE;
    protected MutableLiveData<Boolean> mIsStartRefresh = new MutableLiveData<>();

    public NetworkViewModel(@NonNull Application application) {
        super(application);
        initData();
    }

    private void initData() {
        MyApplication.getInstance().getUpdateCycle()
                .observe(ProcessLifecycleOwner.get(), new Observer<Long>() {
                    @Override
                    public void onChanged(Long period) {
                        if (mUpdateCycle == Long.MAX_VALUE) {// first time
                            mUpdateCycle = period;
                        } else if (mUpdateCycle != period) {
                            mUpdateCycle = period;
                            if (mUpdateCycle == -1L) {//no network
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

    public long getUpdateCycle() {
        return mUpdateCycle;
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
