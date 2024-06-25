/*
 * Copyright (C) 2022 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xperia.settings.display

import android.app.ActivityTaskManager
import android.content.Context
import android.os.RemoteException
import android.provider.Settings
import android.util.Log
import android.view.View

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.core.content.ContextCompat
import android.hardware.display.ColorDisplayManager
import com.android.server.LocalServices;
import com.android.server.display.color.DisplayTransformManager

import vendor.semc.hardware.display.V2_0.IDisplay
import vendor.semc.hardware.display.V2_0.IDisplayCallback
import vendor.semc.hardware.display.V2_0.PccMatrix

class CreatorModeUtils(private val context: Context) : IDisplayCallback.Stub() {
    private val colorDisplayManager: ColorDisplayManager =
            context.getSystemService(ColorDisplayManager::class.java)
                    ?: throw Exception("Display manager is NULL")
    private val semcDisplayService: IDisplay by lazy {
        val service = IDisplay.getService() ?: throw Exception("SEMC Display HIDL not found")
        service.setup()
        service
    }

    /**
     * Color transform level used by Creator Mode
     */
    private val LEVEL_COLOR_MATRIX_CREATOR_MODE = 569

    val isEnabled: Boolean
        get() = Settings.Secure.getInt(context.contentResolver, CREATOR_MODE_ENABLE, 0) != 0

    fun setMode(enabled: Boolean) {
        semcDisplayService.set_sspp_color_mode(if (enabled) 0 else 1)
        colorDisplayManager.setColorMode(if (enabled) 0 else 3)
        semcDisplayService.set_color_mode(if (enabled) 0 else 1)

        Settings.Secure.putInt(context.contentResolver, CREATOR_MODE_ENABLE, if (enabled) 1 else 0)
    }

    fun initialize() {
        Log.i(TAG, "Creator Mode controller setup")
        try {
            // Check if color enhancement is already enabled or not
            if (!isEnabled) {
                semcDisplayService.set_sspp_color_mode(1)
                colorDisplayManager.setColorMode(3)
                semcDisplayService.set_color_mode(1)
            }

            // Register itself as callback for HIDL
            semcDisplayService.registerCallback(this)
    
            // Don't apply anything if the setting is disabled
            if (isEnabled) {
                setMode(true)
            }
        } catch (e: Exception) {
        }
    }

    override fun onWhiteBalanceMatrixChanged(matrix: PccMatrix) {
        try {
            DisplayTransformManager.setColorMatrix(LEVEL_COLOR_MATRIX_CREATOR_MODE,
                floatArrayOf(
                        matrix.red, matrix.green, matrix.blue, 0f,
                        matrix.red, matrix.green, matrix.blue, 0f,
                        matrix.red, matrix.green, matrix.blue, 0f,
                        0f, 0f, 0f, 1f
                    ))
        } catch (e: Exception) {
            Log.e(TAG, "Could not apply setColorMatrix", e)
            return;
        }

        updateConfiguration()

        Log.i(TAG, "New white balance: ${matrix.red}, ${matrix.green}, ${matrix.blue}")
    }

    fun updateConfiguration() {
        try {
            ActivityTaskManager.getService().updateConfiguration(null)
        } catch (e: RemoteException) {
            Log.e(TAG, "Could not update configuration", e)
        }
    }

    companion object {
        private const val TAG = "CreatorModeUtils"
        private const val CREATOR_MODE_ENABLE = "cm_enable"
    }
}
