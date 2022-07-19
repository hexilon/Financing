package com.hexon.repository.base;

import android.app.Application;

import com.hexon.repository.Constants;
import com.hexon.util.SharedPrefsUtils;
import com.hexon.util.constant.TimeConstants;

public class BaseRepository {
    public final static int DEFAULT_CONNECT_TIMEOUT = 5;//5s
    public final static int UPDATE_PERIOD_WIFI = 5*TimeConstants.SEC;//5s
    public final static int UPDATE_PERIOD_MOBILE = 20*TimeConstants.SEC;//20s
}
