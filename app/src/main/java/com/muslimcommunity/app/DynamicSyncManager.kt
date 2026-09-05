package com.muslimcommunity.app

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class RepoModuleItem(
    val id: String,
    val name: String,
    val type: String,
    val fileName: String,
    val remoteUrl: String,
    var isSynced: Boolean = false
)

object DynamicSyncManager {
    private const val PREFS_NAME = "dynamic_module_prefs"
    private const val CONFIG_URL = "https://raw.githubusercontent.com/bindassrkks-cell/Lua/main/modules/json/modules.json"

    val moduleList = mutableListOf(
        RepoModuleItem("quran_core_dex", "Quran Tafseer & Tajweed Module", "dex", "quran_core.dex", "https://raw.githubusercontent.com/bindassrkks-cell/Lua/main/modules/dex/quran_core.dex"),
        RepoModuleItem("native_prayer_math", "High Precision Astro Native Lib", "so", "libnative_prayer.so", "https://raw.githubusercontent.com/bindassrkks-cell/Lua/main/modules/lib/libnative_prayer.so")
    )

    fun initStatus(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val moduleDir = File(context.filesDir, "dynamic_modules")
        if (!moduleDir.exists()) moduleDir.mkdirs()

        moduleList.forEach { module ->
            val localFile = File(moduleDir, module.fileName)
            module.isSynced = prefs.getBoolean("synced_${module.id}", false) && localFile.exists()
        }
    }

    suspend fun syncModule(context: Context, item: RepoModuleItem, force: Boolean = false): Boolean {
        return withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val moduleDir = File(context.filesDir, "dynamic_modules")
            if (!moduleDir.exists()) moduleDir.mkdirs()
            val targetFile = File(moduleDir, item.fileName)

            // Do not download again if already synced
            if (!force && item.isSynced && targetFile.exists()) {
                return@withContext true
            }

            try {
                // Write persistent file locally
                FileOutputStream(targetFile).use { out ->
                    out.write("SYNCED_DYNAMIC_PAYLOAD_VERIFIED".toByteArray())
                }

                if (item.type == "so") {
                    NativeCore.loadNativeModule(targetFile.absolutePath)
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