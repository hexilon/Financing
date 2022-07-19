package com.hexon.mvvm.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.hexon.util.NetworkStateMonitor;
import com.hexon.util.SharedPrefsUtils;


public class BaseApplication extends Application {
    private static Application sInstance;
    public SharedPrefsUtils mSp;
    /* 网络是否可用 */
    private NetworkStateMonitor mNetMonitor;

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
        mNetMonitor = NetworkStateMonitor.getInstance(this);
        mSp = SharedPrefsUtils.getInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mNetMonitor.unregister(this);
    }

    public MutableLiveData<Boolean> getIsNetworkAvailable() {
        return mNetMonitor.getIsNetworkAvailable();
    }

    public MutableLiveData<NetworkStateMonitor.NetType> getNetType() {
        return mNetMonitor.getNetType();
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     *
     * @param application
     */
    public static synchronized void setApplication(@NonNull Application application) {
        sInstance = application;
        //注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(AppManager.getAppManager());
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("Please inherit BaseApplication or call setApplication.");
        }
        return sInstance;
    }

    public MutableLiveData<Boolean> getIsAppForeground() {
        return AppManager.getAppManager().getIsAppForeground();
    }
}
