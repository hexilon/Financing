package com.hexon.financing;

import androidx.lifecycle.MutableLiveData;

import com.hexon.financing.common.NetworkUpdatePeriod;
import com.hexon.financing.util.BuglyUtils;
import com.hexon.mvvm.base.BaseApplication;
import com.hexon.repository.repo.IcbcRepository;
import com.hexon.util.LogUtils;

public class MyApplication extends BaseApplication {
    private static MyApplication sApp;

    public static MyApplication getInstance() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        LogUtils.initLogger(this, BuildConfig.DEBUG);
        BuglyUtils.initBugly(getApplicationContext());
        NetworkUpdatePeriod.init(this);
        IcbcRepository.getInstance(this);//用于初始化当天的实时数据
    }

    public MutableLiveData<Long> getUpdatePeriod() {
        return NetworkUpdatePeriod.getUpdatePeriod();
    }

    public void setUpdatePeriod(long period) {
        NetworkUpdatePeriod.setUpdatePeriod(period);
    }
}
