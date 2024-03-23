package com.xperia.settings.power;

public class Constants {
    /* Intent Actions and Extras */
    public static final String ACTION_POWERSHARE_SETTING_CHANGED = "xperiasettings.intent.action.powershare_setting_changed";
    public static final String POWERSHARE_STATE = "xperiasettings.extra.powershare.STATE";

    /* Power Share */
    public static final String KEY_POWER_SHARE = "pref_power_share";
    public static final String POWER_SHARE_NODE = "/sys/class/qcom-battery/wireless_boost_en";
}
