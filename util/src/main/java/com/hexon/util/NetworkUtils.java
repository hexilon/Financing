package com.hexon.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Created by Hexh on 2018/6/17.
 */
public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        if (null == context)
            throw new IllegalArgumentException("isNetworkAvailable argument cannot be null");

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }

        WifiManager wifiManager =
                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            if (WifiInfo.getDetailedStateOf(info.getSupplicantState()) == NetworkInfo.DetailedState.OBTAINING_IPADDR
                    || WifiInfo.getDetailedStateOf(info.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNetPingAvailable(Context context){
        Runtime runtime = Runtime.getRuntime();
        try {
            //Log.d(TAG, "time:" + System.currentTimeMillis());
            Process process = runtime.exec("ping -c 3 www.baidu.com");
            int ret = process.waitFor();
            //Log.d(TAG, "time:" + System.currentTimeMillis());
            if (ret == 0){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
