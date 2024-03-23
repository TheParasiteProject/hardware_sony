/*
 * Copyright (C) 2020 The LineageOS Project
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
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import androidx.preference.PreferenceFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.xperia.settings.power.R;
import com.xperia.settings.power.utils.PowerUtils;

public class PowerSettingsFragment extends PreferenceFragment {

    private SharedPreferences sharedPrefs;

    private SwitchPreferenceCompat mPrefPowerShare;

    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)) {
                updateSwitch();
            } else if (intent.getAction().equals(Constants.ACTION_POWERSHARE_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                mPrefPowerShare.setChecked(intent.getBooleanExtra(Constants.POWERSHARE_STATE, false));
            }
        }
    };

    private void sendBroadcast(boolean enabled) {
        mSelfChange = true;
        final Intent intent = new Intent(Constants.ACTION_POWERSHARE_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra(Constants.POWERSHARE_STATE, enabled);
        getContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private void updateSwitch() {
        if (PowerUtils.isPowerSaveMode(getContext())) {
            mPrefPowerShare.setEnabled(false);
            mPrefPowerShare.setSummary(R.string.powershare_mode_disabled_summary);
        } else {
            mPrefPowerShare.setEnabled(true);
            mPrefPowerShare.setChecked(PowerUtils.getPowerShareStatus());
            mPrefPowerShare.setSummary(R.string.powershare_mode_summary);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        addPreferencesFromResource(R.xml.power_settings);

        mPrefPowerShare = (SwitchPreferenceCompat) findPreference(Constants.KEY_POWER_SHARE);
        mPrefPowerShare.setOnPreferenceChangeListener(PrefListener);
        updateSwitch();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_POWERSHARE_SETTING_CHANGED);
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        getContext().registerReceiver(stateReceiver, filter);
    }

    private Preference.OnPreferenceChangeListener PrefListener =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                final String key = preference.getKey();

                if (Constants.KEY_POWER_SHARE.equals(key)) {
                    PowerUtils.setPowerShareStatus((boolean) value);
                    sendBroadcast((boolean) value);
                }

                return true;
            }
        };

    @Override
    public void onResume() {
        super.onResume();
        updateSwitch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(stateReceiver);
    }
}
