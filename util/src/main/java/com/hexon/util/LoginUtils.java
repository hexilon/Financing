package com.hexon.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.UUID;

/**
 * Copyright (C), 2020-2025
 * FileName: LoginUtils
 * Description:
 * Author: Hexon
 * Date: 2020/7/25 14:37
 * Version V1.0
 */
public class LoginUtils {
    private static LoginUtils sSelf;
    public static SharedPreferences sSp;
    private SharedPrefsUtils mSharedPrefsUtils;
    public static final String SP_NAME = "secret_login";
    private static final String KEY_LOGIN = "login";
    private static final long DEFAULT_EXPRIED = 7*24*3600*1000;//7 days
    private static Application sApp;

    class LoginInfo {
        String user;
        long time;
        String uuid;
        long versionCode;

        public boolean isValid() {
            if (user == null || user.isEmpty()) {
                return false;
            }

            if (time <= 0) {
                return false;
            }

            if (uuid == null || !uuid.equals(createDeviceUUID(sApp))) {
                return false;
            }

            return true;
        }

        public boolean isExpired() {
            Calendar calendar = Calendar.getInstance();
            if (calendar.getTimeInMillis() - time > DEFAULT_EXPRIED) {
                return true;
            }
            return false;
        }

        public boolean isUpgrade() {
            try {
                if (versionCode != sApp.getPackageManager()
                        .getPackageInfo(sApp.getPackageName(), 0)
                        .versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return false;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("LoginInfo user:" + user);
            buffer.append(" time:" + time);
            buffer.append(" uuid:" + uuid);
            buffer.append(" versionCode:" + versionCode);
            return buffer.toString();

        }
    }

    private LoginUtils(Application application) {
        sApp = application;
        initEncryptedSP(application);
    }

    public static LoginUtils getInstance(Application application) {
        if (sSelf == null) {
            synchronized (LoginUtils.class) {
                sSelf = new LoginUtils(application);
            }
        }

        return sSelf;
    }

    private void initEncryptedSP(Context context) {
        if (sSp == null) {
            String masterKeyAlias = null;
            try {
                masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                sSp = EncryptedSharedPreferences.create(
                        SP_NAME,
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mSharedPrefsUtils = SharedPrefsUtils.getInstance(sApp, SP_NAME);
    }

    public boolean isLogin() {
        LoginInfo loginInfo = (LoginInfo) mSharedPrefsUtils.getData(KEY_LOGIN, new LoginInfo());
        if (loginInfo.isValid()) {
            return true;
        }
        return false;
    }

    public boolean isNeedLogin() {
        LoginInfo loginInfo = (LoginInfo) mSharedPrefsUtils.getData(KEY_LOGIN, new LoginInfo());
        if (!isLogin()) {
            return true;
        }

        if (loginInfo.isExpired()) {
            return true;
        }

        return false;
    }

    public void login(String user) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.user = user;
        loginInfo.time = Calendar.getInstance().getTimeInMillis();
        loginInfo.uuid = createDeviceUUID(sApp);
        try {
            loginInfo.versionCode = sApp.getPackageManager()
                            .getPackageInfo(sApp.getPackageName(), 0)
                            .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mSharedPrefsUtils.putData(KEY_LOGIN, loginInfo);
    }

    public static String createDeviceUUID(Context context) {
        String androidId = getAndroidId(context);
        return new UUID(androidId.hashCode(), getDeviceInfo().hashCode()).toString();
    }

    public static String getDeviceInfo() {
        //选取一些系统不变参数参与计算uuid
        StringBuffer buildSB = new StringBuffer();
        buildSB.append(Build.BRAND).append("/");
        buildSB.append(Build.MODEL).append("/");
        buildSB.append(Build.PRODUCT).append("/");
        buildSB.append(Build.DEVICE).append("/");
        buildSB.append(Build.SERIAL).append("/");
        buildSB.append(Build.ID).append("/");
        buildSB.append(Build.VERSION.INCREMENTAL);
        return buildSB.toString();
    }

    public static String getAndroidId(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
