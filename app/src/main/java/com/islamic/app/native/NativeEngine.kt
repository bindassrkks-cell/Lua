package com.islamic.app.native

import android.content.Context
import android.util.Log
import java.io.File

object NativeEngine {
    private var loaded = false

    external fun getSocialFeedJson(): String
    external fun getSalahGuideJson(): String
    external fun getGeminiEndpoint(): String
    external fun getCorePayloadSize(): Long

    fun load(context: Context): Boolean {
        if (loaded) return true

        // 1. Check dynamic OTA downloaded lib first
        val dynamicFile = File(context.filesDir, "lib/libsos.so")
        if (dynamicFile.exists() && dynamicFile.length() > 1024 * 1024) {
            try {
                System.load(dynamicFile.absolutePath)
                loaded = true
                Log.d("NativeEngine", "Loaded dynamic libsos.so (${dynamicFile.length()} bytes)")
                return true
            } catch (e: Throwable) {
                Log.e("NativeEngine", "Dynamic load error: ${e.message}")
            }
        }

        // 2. Fallback to bundled library
        try {
            System.loadLibrary("sos")
            loaded = true
            Log.d("NativeEngine", "Loaded bundled libsos")
            return true
        } catch (e: Throwable) {
            Log.w("NativeEngine", "Bundled libsos not available: ${e.message}")
        }

        return false
    }

    fun isLoaded() = loaded
    fun getInstalledFile(context: Context) = File(context.filesDir, "lib/libsos.so")
}
