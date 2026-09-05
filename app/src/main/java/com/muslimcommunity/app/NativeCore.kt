package com.muslimcommunity.app

object NativeCore {
    init {
        System.loadLibrary("native-lib")
    }
    external fun getEngineInfo(): String
    external fun getVfxGlowIntensity(timeMillis: Long): Float
    external fun loadDynamicNativeLib(path: String): Boolean
}