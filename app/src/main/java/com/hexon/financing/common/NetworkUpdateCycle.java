package com.hexon.financing.common;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.hexon.mvvm.base.BaseApplication;
import com.hexon.util.NetworkStateMonitor;

/**
 * Copyright (C), 2022-2030
 * ClassName: NetworkUpdateCycle
 * Description:
 * Author: Hexon
 * Date: 2022/6/21 10:20
 * Version V1.0
 */
public class NetworkUpdateCycle {
    private static MutableLiveData<Long> sUpdateCycle = new MutableLiveData<>();
    static BaseApplication sApp;

    public static void init(BaseApplication baseApplication) {
        sApp = baseApplication;
        baseApplication.getNetType().observe(ProcessLifecycleOwner.get(), new Observer<NetworkStateMonitor.NetType>() {
            @Override
            public void onChanged(NetworkStateMonitor.NetType netType) {
                updateCycle(netType);
            }
        });
    }

    private static void updateCycle(NetworkStateMonitor.NetType netType) {
        switch (netType) {
            case NONE:
                sUpdateCycle.postValue(-1L);
                break;
            case WIFI:
                if (sApp.mSp.contains(Constants.SP_KEY_WIFI_UPDATE_CYCLE)) {
                    sUpdateCycle.postValue(
                            sApp.mSp.getData(Constants.SP_KEY_WIFI_UPDATE_CYCLE, -1L));
                } else {
                    sUpdateCycle.postValue(Constants.WIFI_UPDATE_CYCLE);
                }
                break;
            case CELLULAR:
                if (sApp.mSp.contains(Constants.SP_KEY_MOBILE_UPDATE_CYCLE)) {
                    sUpdateCycle.postValue(
                            sApp.mSp.getData(Constants.SP_KEY_MOBILE_UPDATE_CYCLE, -1L));
                } else {
                    sUpdateCycle.postValue(Constants.MOBILE_UPDATE_CYCLE);
                }
                break;
        }
    }

    public static MutableLiveData<Long> getUpdateCycle() {
        return sUpdateCycle;
    }
}
