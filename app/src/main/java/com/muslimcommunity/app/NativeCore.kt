package com.muslimcommunity.app

object NativeCore {
    init {
        System.loadLibrary("native-lib")
    }
    external fun verifyWasmBinary(bytes: ByteArray): Boolean
    external fun getEngineStatus(): String
}