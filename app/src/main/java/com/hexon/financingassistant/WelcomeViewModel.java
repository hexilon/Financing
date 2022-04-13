package com.hexon.financingassistant;

import static com.hexon.commonui.BaseSplashActivity.SP_KEY_FIRST_LAUNCH;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hexon.mvvm.base.BaseViewModel;
import com.hexon.util.SharedPrefsUtils;

/**
 * Copyright (C), 2020-2025
 * FileName    : WelcomeViewModel
 * Description :
 * Author      : Hexon
 * Date        : 2020-08-01 21:40
 * Version     : V1.0
 */
public class WelcomeViewModel extends BaseViewModel {
    public WelcomeViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onStop() {
        SharedPrefsUtils sp = SharedPrefsUtils.getInstance(getApplication());
        sp.putData(SP_KEY_FIRST_LAUNCH, false);
    }
}
