package com.hexon.financing.common;

import static com.hexon.repository.Constants.MOBILE_UPDATE_PERIOD;
import static com.hexon.repository.Constants.SP_KEY_CUSTOM_MOBILE_UPDATE_PERIOD;
import static com.hexon.repository.Constants.SP_KEY_CUSTOM_WIFI_UPDATE_PERIOD;
import static com.hexon.repository.Constants.WIFI_UPDATE_PERIOD;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.hexon.mvvm.base.BaseApplication;
import com.hexon.util.NetworkStateMonitor;

/**
 * Copyright (C), 2022-2030
 * ClassName: NetworkUpdatePeriod
 * Description:
 * Author: Hexon
 * Date: 2022/6/21 10:20
 * Version V1.0
 */
public class NetworkUpdatePeriod {
    private static MutableLiveData<Long> sUpdatePeriod = new MutableLiveData<>();
    static BaseApplication sApp;

    public static void init(BaseApplication baseApplication) {
        sApp = baseApplication;
        baseApplication.getNetType().observe(ProcessLifecycleOwner.get(), new Observer<NetworkStateMonitor.NetType>() {
            @Override
            public void onChanged(NetworkStateMonitor.NetType netType) {
                updatePeriod(netType);
            }
        });
    }

    private static void updatePeriod(NetworkStateMonitor.NetType netType) {
        switch (netType) {
            case NONE:
                sUpdatePeriod.postValue(-1L);
                break;
            case WIFI:
                if (sApp.mSp.contains(SP_KEY_CUSTOM_WIFI_UPDATE_PERIOD)) {
                    sUpdatePeriod.postValue(
                            sApp.mSp.getData(SP_KEY_CUSTOM_WIFI_UPDATE_PERIOD, -1L));
                } else {
                    sUpdatePeriod.postValue(WIFI_UPDATE_PERIOD);
                }
                break;
            case CELLULAR:
                if (sApp.mSp.contains(SP_KEY_CUSTOM_MOBILE_UPDATE_PERIOD)) {
                    sUpdatePeriod.postValue(
                            sApp.mSp.getData(SP_KEY_CUSTOM_MOBILE_UPDATE_PERIOD, -1L));
                } else {
                    sUpdatePeriod.postValue(MOBILE_UPDATE_PERIOD);
                }
                break;
        }
    }

    public static MutableLiveData<Long> getUpdatePeriod() {
        return sUpdatePeriod;
    }

    public static void setUpdatePeriod(long period) {
        sUpdatePeriod.postValue(period);
        updatePeriod(sApp.getNetType().getValue());
    }
}
