package com.islamic.app.pak

import android.content.Context
import android.util.LruCache
import java.io.File

object PakManager {
    private val mountedPaks = mutableListOf<PakReader>()
    private val memoryCache = LruCache<String, ByteArray>(15 * 1024 * 1024)

    fun init(context: Context) {
        val dir = getPakDirectory(context)
        if (!dir.exists()) dir.mkdirs()
        refreshMountedPackages(context)
    }

    fun getPakDirectory(context: Context) = File(context.filesDir, "content/pak")

    @Synchronized
    fun refreshMountedPackages(context: Context) {
        closeAll()
        val dir = getPakDirectory(context)
        val paks = dir.listFiles { f -> f.isFile && f.extension == "pak" } ?: emptyArray()
        for (f in paks.sortedBy { it.name }) {
            try { mountedPaks.add(PakReader(f)) } catch (e: Exception) { e.printStackTrace() }
        }
    }

    @Synchronized
    fun exists(path: String) = mountedPaks.any { it.exists(path) }

    @Synchronized
    fun readBytes(path: String): ByteArray {
        val cached = memoryCache.get(path)
        if (cached != null) return cached
        for (pak in mountedPaks) {
            if (pak.exists(path)) {
                val data = pak.readBytes(path)
                memoryCache.put(path, data)
                return data
            }
        }
        throw NoSuchElementException("Asset missing: $path")
    }

    fun readText(path: String) = String(readBytes(path), Charsets.UTF_8)

    @Synchronized
    fun closeAll() {
        memoryCache.evictAll()
        mountedPaks.forEach { it.close() }
        mountedPaks.clear()
    }
}
