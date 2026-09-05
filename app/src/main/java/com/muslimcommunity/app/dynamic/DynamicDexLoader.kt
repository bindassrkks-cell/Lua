package com.muslimcommunity.app.dynamic

import android.content.Context
import dalvik.system.DexClassLoader
import java.io.File

object DynamicDexLoader {
    fun loadPlugin(context: Context, dexFile: File, className: String): DynamicFeaturePlugin? {
        return try {
            val optDir = File(context.codeCacheDir, "opt_dex")
            if (!optDir.exists()) optDir.mkdirs()

            val classLoader = DexClassLoader(
                dexFile.absolutePath,
                optDir.absolutePath,
                null,
                context.classLoader
            )
            val loadedClass = classLoader.loadClass(className)
            loadedClass.getDeclaredConstructor().newInstance() as? DynamicFeaturePlugin
        } catch (e: Exception) {
            null
        }
    }
}