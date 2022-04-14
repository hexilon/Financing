package com.hexon.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * Copyright (C), 2022-2030
 * ClassName: UIUtils
 * Description:
 * Author: Hexon
 * Date: 2022/4/14 11:22
 * Version V1.0
 */

public class UIUtils {
    public static void setTransparentStatusBar(@NonNull Activity activity, boolean lightMode) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        ViewGroup contentView = activity.findViewById(Window.ID_ANDROID_CONTENT);
        View firstChildView = contentView.getChildAt(0);
        if (firstChildView != null) {
            firstChildView.setFitsSystemWindows(false);
        }
        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (lightMode) {
                window.getDecorView().setSystemUiVisibility(
                        systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                window.getDecorView().setSystemUiVisibility(
                        systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }
}
