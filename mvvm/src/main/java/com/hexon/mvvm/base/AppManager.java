package com.hexon.mvvm.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;

import com.hexon.mvvm.utils.LogUtils;

import java.util.Stack;

public class AppManager implements Application.ActivityLifecycleCallbacks {
    private static Stack<Activity> sActivityStack;
    private static Stack<Fragment> sFragmentStack;
    private static AppManager sInstance;
    private ObservableBoolean mIsAppForeground = new ObservableBoolean();

    private AppManager() {
        mIsAppForeground.set(true);
    }

    public static AppManager getAppManager() {
        if (sInstance == null) {
            synchronized (AppManager.class) {
                if (sInstance == null) {
                    sInstance = new AppManager();
                }
            }
        }

        return sInstance;
    }

    public ObservableBoolean getIsAppForeground() {
        return mIsAppForeground;
    }

    public static Stack<Activity> getActivityStack() {
        return sActivityStack;
    }

    public static Stack<Fragment> getFragmentStack() {
        return sFragmentStack;
    }

    public void addActivity(Activity activity) {
        if (sActivityStack == null) {
            sActivityStack = new Stack<Activity>();
        }
        sActivityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            sActivityStack.remove(activity);
        }
    }

    public boolean isActivityExist() {
        if (sActivityStack != null) {
            return !sActivityStack.isEmpty();
        }
        return false;
    }

    public Activity currentActivity() {
        Activity activity = sActivityStack.lastElement();
        return activity;
    }

    public void finishActivity() {
        Activity activity = sActivityStack.lastElement();
        finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public void finishActivity(Class<?> cls) {
        for (Activity activity : sActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    public void finishAllActivity() {
        for (int i = 0, size = sActivityStack.size(); i < size; i++) {
            if (null != sActivityStack.get(i)) {
                finishActivity(sActivityStack.get(i));
            }
        }
        sActivityStack.clear();
    }

    public Activity getActivity(Class<?> cls) {
        if (sActivityStack != null)
            for (Activity activity : sActivityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    public void addFragment(Fragment fragment) {
        if (sFragmentStack == null) {
            sFragmentStack = new Stack<Fragment>();
        }
        sFragmentStack.add(fragment);
    }

    public void removeFragment(Fragment fragment) {
        if (fragment != null) {
            sFragmentStack.remove(fragment);
        }
    }

    public boolean isFragmentExist() {
        if (sFragmentStack != null) {
            return !sFragmentStack.isEmpty();
        }
        return false;
    }

    public Fragment currentFragment() {
        if (sFragmentStack != null) {
            Fragment fragment = sFragmentStack.lastElement();
            return fragment;
        }
        return null;
    }

    public void AppExit() {
        try {
            finishAllActivity();
            // 杀死该应用进程
//          android.os.Process.killProcess(android.os.Process.myPid());
//            调用 System.exit(n) 实际上等效于调用：
//            Runtime.getRuntime().exit(n)
//            finish()是Activity的类方法，仅仅针对Activity，当调用finish()时，只是将活动推向后台，并没有立即释放内存，活动的资源并没有被清理；
//            当调用System.exit(0)时，退出当前Activity并释放资源（内存），但是该方法不可以结束整个App如有多个Activty或者有其他组件service等不会结束。
//            其实android的机制决定了用户无法完全退出应用，当你的application最长时间没有被用过的时候，android自身会决定将application关闭了。
            //System.exit(0);
        } catch (Exception e) {
            sActivityStack.clear();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if(!mIsAppForeground.get()){
            mIsAppForeground.set(true); // background switch foreground
            LogUtils.d("background to foreground");
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if(sActivityStack.empty()){
            mIsAppForeground.set(false); // foreground switch background
            LogUtils.d("foreground switch background");
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        removeActivity(activity);
    }
}