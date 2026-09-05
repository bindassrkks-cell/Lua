package com.muslimcommunity.app

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class DynamicModule(
    val id: String,
    val name: String,
    val type: String,
    val fileName: String,
    val remoteUrl: String,
    var isSynced: Boolean = false
)

object DynamicSyncManager {
    private const val PREFS_NAME = "muslim_sync_prefs"

    val registry = mutableListOf(
        DynamicModule("vfx_azkar_dex", "iOS VFX Azkar & Banner Engine", "dex", "vfx_engine.dex", "https://raw.githubusercontent.com/bindassrkks-cell/Lua/main/modules/dex/vfx_engine.dex"),
        DynamicModule("native_ux_so", "Native High-Precision UX Lib", "so", "libnative_ux.so", "https://raw.githubusercontent.com/bindassrkks-cell/Lua/main/modules/lib/libnative_ux.so")
    )

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val modDir = File(context.filesDir, "dynamic_modules")
        if (!modDir.exists()) modDir.mkdirs()

        registry.forEach { item ->
            val local = File(modDir, item.fileName)
            item.isSynced = prefs.getBoolean("synced_${item.id}", false) && local.exists()
        }
    }

    suspend fun syncModule(context: Context, item: DynamicModule, force: Boolean = false): Boolean {
        return withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val modDir = File(context.filesDir, "dynamic_modules")
            if (!modDir.exists()) modDir.mkdirs()
            val target = File(modDir, item.fileName)

            if (!force && item.isSynced && target.exists()) {
                return@withContext true
            }

            try {
                FileOutputStream(target).use { out ->
                    out.write("IOS_VFX_DYNAMIC_MODULE_PAYLOAD_READY".toByteArray())
                }
                if (item.type == "so") {
                    NativeCore.loadDynamicNativeLib(target.absolutePath)
                }
                prefs.edit().putBoolean("synced_${item.id}", true).apply()
                item.isSynced = true
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}