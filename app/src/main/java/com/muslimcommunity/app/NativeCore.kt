package com.muslimcommunity.app

object NativeCore {
    init {
        System.loadLibrary("native-lib")
    }
    external fun getEngineInfo(): String
    external fun loadNativeModule(path: String): Boolean
}