package com.hexon.financing;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;

import com.hexon.commonui.BaseSplashActivity;

public class SplashActivity extends BaseSplashActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Class<? extends Activity> getTheFirstSeed() {
        return WelcomeActivity.class;
    }

    @Override
    public Class<? extends Activity> getTheMainSeed() {
        return MainActivity.class;
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    public String[] getUsesPermission() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS};
    }
}
