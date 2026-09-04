package com.islamic.app.downloader

import android.content.Context
import com.islamic.app.native.NativeEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

sealed class DownloadState {
    object Idle : DownloadState()
    object Connecting : DownloadState()
    data class Running(val downloadedBytes: Long, val totalBytes: Long, val percent: Int) : DownloadState()
    data class Success(val sizeFormatted: String) : DownloadState()
    data class Failed(val error: String) : DownloadState()
}

class LibSosDownloader(private val context: Context) {
    private val client = OkHttpClient()
    private val downloadUrl = "https://github.com/bindassrkks-cell/Lua/releases/download/v0.0.15.0/libsos.so"

    fun downloadAndAttach(): Flow<DownloadState> = flow {
        emit(DownloadState.Connecting)
        val req = Request.Builder().url(downloadUrl).build()
        val resp = client.newCall(req).execute()

        if (!resp.isSuccessful) {
            emit(DownloadState.Failed("HTTP ${resp.code} from release server"))
            return@flow
        }

        val body = resp.body ?: run {
            emit(DownloadState.Failed("Payload is empty"))
            return@flow
        }

        val total = body.contentLength()
        val libDir = File(context.filesDir, "lib")
        if (!libDir.exists()) libDir.mkdirs()

        val partFile = File(libDir, "libsos.so.part")
        val destFile = File(libDir, "libsos.so")

        val input = body.byteStream()
        val output = FileOutputStream(partFile)
        val buffer = ByteArray(8192)
        var curBytes = 0L

        try {
            var read = input.read(buffer)
            while (read != -1) {
                output.write(buffer, 0, read)
                curBytes += read
                val pct = if (total > 0) ((curBytes * 100) / total).toInt() else 0
                emit(DownloadState.Running(curBytes, total, pct))
                read = input.read(buffer)
            }
            output.flush()
        } finally {
            output.close()
            input.close()
        }

        if (destFile.exists()) destFile.delete()
        if (partFile.renameTo(destFile)) {
            NativeEngine.load(context)
            val mb = String.format("%.2f MB", destFile.length() / (1024.0 * 1024.0))
            emit(DownloadState.Success(mb))
        } else {
            emit(DownloadState.Failed("Mount error"))
        }
    }.flowOn(Dispatchers.IO)
}
