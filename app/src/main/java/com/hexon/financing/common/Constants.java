package com.hexon.financing.common;

import com.hexon.util.constant.TimeConstants;

import java.io.Serializable;

/**
 * @author Hexh
 * @date 2019-06-19 16:54
 */
public class Constants {
    public static final long APP_RECORD_CYCLE = 10 * TimeConstants.SEC;//10sec

    public static final String LAST_LOGIN_USER = "last_login_user";
    public static final String IDENTITY = "identity";
    public static final String UUID = "uuid";

    public static final int ADVERTISE_DURATION = 1 * TimeConstants.SEC;

    public static final String COS_PATH_DEVICE = "devices";

    public static final String INTENT_EXTRA_BANK = "bank";
    public static final String INTENT_EXTRA_METAL_TYPE = "metal_type";
    public static final String INTENT_EXTRA_METAL_PERIOD = "metal_period";

    /* shared preference key */
    public static final String SP_KEY_FIRST_LAUNCH = "first_launch";
    public static final String SP_KEY_DETECT_METALS = "detect_list";

    // 记录使用时间点
    public static final String SP_KEY_APP_RECORD = "app_record";
    // 使用习惯
    public static final String SP_KEY_APP_USAGE_HABIT = "usage_habit";
    // 主页显示的ICBC品种列表
    public static final String SP_KEY_ICBC_CUSTOM_METALS = "icbc_custom_metals";

    public static final String SP_KEY_DARK_UI_MODE = "dark_ui_mode";

    public static final String LOGIN_RECORD_FILE = "login_record.txt";
}
