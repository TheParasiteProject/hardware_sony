package com.xperia.settings.power.utils;

import android.content.Context;
import android.os.PowerManager;

import com.xperia.settings.power.Constants;

public class PowerUtils {
    public static boolean getPowerShareStatus() {
        return FileUtils.getFileValueAsBoolean(Constants.POWER_SHARE_NODE, false);
    }

    public static void setPowerShareStatus(boolean enabled) {
        FileUtils.writeLine(Constants.POWER_SHARE_NODE, enabled ? "1" : "0");
    }

    public static boolean isPowerSaveMode(Context context) {
        final PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return mPowerManager.isPowerSaveMode();
    }
}
