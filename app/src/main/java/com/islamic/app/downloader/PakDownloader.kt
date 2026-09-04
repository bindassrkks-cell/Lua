package com.islamic.app.downloader

import android.content.Context
import com.islamic.app.pak.PakManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class Progress(val percentage: Int) : UpdateState()
    data class Completed(val message: String) : UpdateState()
    data class Error(val error: String) : UpdateState()
}

class PakDownloader(private val context: Context) {
    private val client = OkHttpClient()
    private val manifestUrl = "https://github.com/bindassrkks-cell/Lua/releases/download/v0.0.15.0/manifest.json"
    private val pakBaseUrl = "https://github.com/bindassrkks-cell/Lua/releases/download/v0.0.15.0/CorePatch_0.0.15.0.pak"

    fun syncCorePatch(): Flow<UpdateState> = flow {
        emit(UpdateState.Checking)
        val manifestRequest = Request.Builder().url(manifestUrl).build()
        val manifestResponse = client.newCall(manifestRequest).execute()

        if (!manifestResponse.isSuccessful) {
            emit(UpdateState.Error("HTTP Error: ${manifestResponse.code}"))
            return@flow
        }

        val jsonStr = manifestResponse.body?.string().orEmpty()
        val manifestObj = JSONObject(jsonStr)
        val pkg = manifestObj.getJSONArray("packages").getJSONObject(0)
        val fileName = pkg.getString("file_name")
        val expectedSha = pkg.getString("sha256")

        val targetDir = PakManager.getPakDirectory(context)
        val targetFile = File(targetDir, fileName)
        val partFile = File(targetDir, "$fileName.part")

        val pakResponse = client.newCall(Request.Builder().url(pakBaseUrl).build()).execute()
        val body = pakResponse.body ?: run {
            emit(UpdateState.Error("Empty payload"))
            return@flow
        }

        val totalSize = body.contentLength()
        val input = body.byteStream()
        val output = FileOutputStream(partFile)
        val buffer = ByteArray(8192)
        var read: Int
        var downloaded = 0L

        try {
            while (input.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
                downloaded += read
                emit(UpdateState.Progress(if (totalSize > 0) ((downloaded * 100) / totalSize).toInt() else 0))
            }
            output.flush()
        } finally {
            output.close()
            input.close()
        }

        val md = MessageDigest.getInstance("SHA-256")
        val calculatedSha = md.digest(partFile.readBytes()).joinToString("") { "%02x".format(it) }

        if (!calculatedSha.equals(expectedSha, ignoreCase = true)) {
            partFile.delete()
            emit(UpdateState.Error("SHA-256 verification failed"))
            return@flow
        }

        if (targetFile.exists()) targetFile.delete()
        if (partFile.renameTo(targetFile)) {
            PakManager.refreshMountedPackages(context)
            emit(UpdateState.Completed("CorePatch 0.0.15.0 Active"))
        } else {
            emit(UpdateState.Error("Mount failed"))
        }
    }.flowOn(Dispatchers.IO)
}
