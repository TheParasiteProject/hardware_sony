/*
 * Copyright (C) 2023 XperiaLabs Project
 * Copyright (C) 2022 Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xperia.settings

import android.os.Bundle
import androidx.preference.*
import android.content.pm.PackageManager

import com.xperia.settings.R
import com.xperia.settings.XperiaSettingsPackage

class XperiaSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xperia_settings, rootKey)

        val xperiaSettingsPackage = XperiaSettingsPackage(this)
        // Display
        xperiaSettingsPackage.setupSettings(
            "com.xperia.settings.display", // packageName
            "com.xperia.settings.display.DisplaySettingsActivity", // className
            "display", // categName
            "display_settings" // prefName
        )
        // Power
        xperiaSettingsPackage.setupSettings(
            "com.xperia.settings.power", // packageName
            "com.xperia.settings.power.PowerSettingsActivity", // className
            "power", // categName
            "power_settings" // prefName
        )
        // Audio
        xperiaSettingsPackage.setupSettings(
            "com.xperia.settings.audio", // packageName
            "com.xperia.settings.audio.AudioSettingsActivity", // className
            "sound", // categName
            "audio_settings" // prefName
        )
        // USB External Monitor
        xperiaSettingsPackage.setupSettings(
            "com.sonymobile.extmonitorapp", // packageName
            "com.sonymobile.extmonitorapp.settings.SettingsAppLauncherActivity", // className
            "usb", // categName
            "usb_extmon_settings" // prefName
        )
        // USB Audio
        xperiaSettingsPackage.setupSettings(
            "jp.co.sony.mc.usbextoutaudio", // packageName
            "jp.co.sony.mc.usbextoutaudio.AudioSettingsActivity", // className
            "usb", // categName
            "usb_audio_settings" // prefName
        )
        // Dual Shock
        xperiaSettingsPackage.setupSettings(
            "com.sonymobile.dualshockmanager", // packageName
            "com.sonymobile.dualshockmanager.Ds4SetUpActivity", // className
            "gaming", // categName
            "dsm_settings" // prefName
        )
    }
}
