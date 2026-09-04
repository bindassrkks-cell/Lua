package com.islamic.app.social

import android.content.Context
import android.util.Log
import java.io.File

object NativeSocialEngine {
    private var isLoaded = false

    external fun getSocialFeedJson(): String

    fun loadNativeLib(context: Context): Boolean {
        if (isLoaded) return true

        // 1. Try loading from dynamic download location
        val dynamicLib = File(context.filesDir, "lib/libsos.so")
        if (dynamicLib.exists()) {
            try {
                System.load(dynamicLib.absolutePath)
                isLoaded = true
                Log.d("NativeSocialEngine", "Loaded dynamic libsos.so from ${dynamicLib.absolutePath}")
                return true
            } catch (e: Throwable) {
                Log.e("NativeSocialEngine", "Failed loading dynamic lib: ${e.message}")
            }
        }

        // 2. Fallback to APK bundled library
        try {
            System.loadLibrary("sos")
            isLoaded = true
            Log.d("NativeSocialEngine", "Loaded bundled libsos")
            return true
        } catch (e: Throwable) {
            Log.w("NativeSocialEngine", "Bundled libsos not found: ${e.message}")
        }

        return false
    }

    fun isLibActive() = isLoaded
}
