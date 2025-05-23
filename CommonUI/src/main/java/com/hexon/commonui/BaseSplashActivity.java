package com.hexon.commonui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.hexon.mvvm.base.BaseActivity;
import com.hexon.util.SharedPrefsUtils;

public abstract class BaseSplashActivity extends BaseActivity {
    public static final String SP_KEY_FIRST_LAUNCH = "first_launch";
    Boolean mIsFirstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPrefsUtils sp = SharedPrefsUtils.getInstance(getApplication());
        mIsFirstLaunch = ((Boolean) sp.getData(SP_KEY_FIRST_LAUNCH, true));
        requestRunTimePermissions(getUsesPermission(), new PermissionCallback() {
            @Override
            public void requestSuccess() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startSeeding();
                    }
                }, 1000);
            }

            @Override
            public void refused() {
                finish();
            }
        });
    }

    protected abstract Class<? extends Activity> getTheFirstSeed();
    protected abstract Class<? extends Activity> getTheMainSeed();

    protected String[] getUsesPermission() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return null;
        }

        return packageInfo.requestedPermissions;
    }

    public void startSeeding() {
        Intent intent;
        if (mIsFirstLaunch) {
            intent = new Intent(this, getTheFirstSeed());
        } else {
            intent = new Intent(this, getTheMainSeed());
        }
        startActivity(intent);
        finish();
    }
}
