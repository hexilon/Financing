package com.hexon.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.lifecycle.MutableLiveData;

/**
 * @author Hexh
 * @date 2019-07-31 11:41
 */
public class NetworkStateMonitor {
    private static NetworkStateMonitor sInstance;
    private NetworkCallback mNetworkCallback;
    private ConnectivityManager.NetworkCallback mCallback;
    private NetworkBroadCastReciver mReceiver;
    private static MutableLiveData<NetType> mNetType = new MutableLiveData<>();
    private static MutableLiveData<Boolean> mIsNetworkAvailable = new MutableLiveData<>();

    public enum NetType {
        NONE, WIFI, CELLULAR, BLUETOOTH
    }

    public static NetworkStateMonitor getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NetworkStateMonitor.class) {
                if (sInstance == null) {
                    sInstance = new NetworkStateMonitor(context, new NetworkCallback() {
                        @Override
                        public void onAvailable() {
                            mIsNetworkAvailable.postValue(true);
                        }

                        @Override
                        public void onTransportWifi(boolean wifi) {
                            if (wifi) {
                                mNetType.postValue(NetType.WIFI);
                            } else {
                                mNetType.postValue(NetType.CELLULAR);
                            }
                        }

                        @Override
                        public void onLost() {
                            mNetType.postValue(NetType.NONE);
                            mIsNetworkAvailable.postValue(false);
                        }
                    });
                }
            }
        }

        return sInstance;
    }

    private NetworkStateMonitor(Context context, NetworkCallback networkCallback) {
        mNetworkCallback = networkCallback;
        mReceiver = new NetworkBroadCastReciver();
        mIsNetworkAvailable.setValue(NetworkUtils.isNetworkAvailable(context));
        mNetType.setValue(NetType.NONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    mNetworkCallback.onAvailable();
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    mNetworkCallback.onLost();
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                            mNetworkCallback.onTransportWifi(true);
                        }else {
                            mNetworkCallback.onTransportWifi(false);
                        }
                    }
                }
            };
            connManager.registerDefaultNetworkCallback(mCallback);
        } else {
            IntentFilter intentFilter = new IntentFilter();
            //intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            //intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

            context.registerReceiver(mReceiver, intentFilter);
        }
    }

    public MutableLiveData<Boolean> getIsNetworkAvailable() {
        return mIsNetworkAvailable;
    }

    public MutableLiveData<NetType> getNetType() {
        return mNetType;
    }

    /**
     * 取消网络监听
     *
     * @param context
     */
    public void unregister(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            connManager.unregisterNetworkCallback(mCallback);
        } else {
            context.unregisterReceiver(mReceiver);
        }
    }

    /**
     * 网络状态回调
     */
    public interface NetworkCallback {
        /**
         * 网络可用
         */
        void onAvailable();

        /**
         * 使用WIFI网络
         */
        void onTransportWifi(boolean wifi);

        /**
         * 网络不可用，丢失连接
         */
        void onLost();
    }


    class NetworkBroadCastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {
                NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    mNetworkCallback.onAvailable();
                    int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE,
                            ConnectivityManager.TYPE_DUMMY);
                    mNetworkCallback.onTransportWifi(networkType == ConnectivityManager.TYPE_WIFI);
                } else {
                    mNetworkCallback.onLost();
                }
            }
        }
    }
}
