/*
 * Copyright (C) 2023 XperiaLabs Project
 * Copyright (C) 2022 Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xperia.settings

import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import androidx.preference.*
import androidx.core.content.pm.PackageInfoCompat
 
import com.xperia.settings.R
 
class XperiaSettingsPackage(private val fragment: PreferenceFragmentCompat) {
    private val pm = fragment.activity?.packageManager

    fun setupSettings(packageName: String, className: String, categName: String, prefName: String) {
        try {
            val packageInfo = pm?.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            if (packageInfo != null && PackageInfoCompat.getLongVersionCode(packageInfo) >= 1) {
                // Package exists and has a version code greater than or equal to 1, set preference to visible
                fragment.findPreference<Preference>(prefName)?.isVisible = true
                val intent = Intent().apply {
                    setClassName(packageName, className)
                }
                fragment.findPreference<Preference>(prefName)?.intent = intent
            } else {
                // Package does not exist or has a version code less than 1, set preference to hidden
                val category = fragment.findPreference<PreferenceCategory>(categName)
                fragment.findPreference<Preference>(prefName)?.isVisible = false
                category?.isVisible = false
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // Package does not exist, set preference to hidden
            val category = fragment.findPreference<PreferenceCategory>(categName)
            fragment.findPreference<Preference>(prefName)?.isVisible = false
            category?.isVisible = false
        }
    }
}
