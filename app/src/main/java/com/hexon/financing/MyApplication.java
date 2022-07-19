package com.hexon.financing;

import androidx.lifecycle.MutableLiveData;

import com.hexon.financing.common.NetworkUpdateCycle;
import com.hexon.financing.util.BuglyUtils;
import com.hexon.mvvm.base.BaseApplication;
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
        NetworkUpdateCycle.init(this);
    }

    public MutableLiveData<Long> getUpdateCycle() {
        return NetworkUpdateCycle.getUpdateCycle();
    }
}
