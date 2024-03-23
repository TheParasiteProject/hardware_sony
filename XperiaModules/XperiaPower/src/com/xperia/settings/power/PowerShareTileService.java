/*
 * Copyright (C) 2021 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xperia.settings.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import com.xperia.settings.power.Constants;
import com.xperia.settings.power.R;
import com.xperia.settings.power.utils.PowerUtils;

public class PowerShareTileService extends TileService {

    private SharedPreferences sharedPrefs;

    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)) {
                updateTile(PowerUtils.getPowerShareStatus());
            } else if (intent.getAction().equals(Constants.ACTION_POWERSHARE_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                updateTile(intent.getBooleanExtra(Constants.POWERSHARE_STATE, false));
            }
        }
    };

    private void sendBroadcast(boolean enabled) {
        mSelfChange = true;
        final Intent intent = new Intent(Constants.ACTION_POWERSHARE_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra(Constants.POWERSHARE_STATE, enabled);
        getApplicationContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private void updateTile(boolean enabled) {
        final Tile tile = getQsTile();
        if (PowerUtils.isPowerSaveMode(getApplicationContext())) {
            tile.setState(Tile.STATE_UNAVAILABLE);
        } else {
            tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        }
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();

        updateTile(PowerUtils.getPowerShareStatus());

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_POWERSHARE_SETTING_CHANGED);
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        registerReceiver(stateReceiver, filter);
    }

    @Override
    public void onStopListening() {
        unregisterReceiver(stateReceiver);
        super.onStopListening();
    }

    @Override
    public void onClick() {
        if (PowerUtils.isPowerSaveMode(getApplicationContext())) {
            return;
        }

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean enabled = !(sharedPrefs.getBoolean(Constants.KEY_POWER_SHARE, false));
        PowerUtils.setPowerShareStatus(enabled);
        sharedPrefs.edit().putBoolean(Constants.KEY_POWER_SHARE, enabled).commit();
        sendBroadcast(enabled);
        updateTile(enabled);
        super.onClick();
    }
}
