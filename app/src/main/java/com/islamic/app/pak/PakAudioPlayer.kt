package com.islamic.app.pak

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object PakAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, audioPath: String, onComplete: () -> Unit = {}) {
        stop()
        if (!PakManager.exists(audioPath)) {
            Log.w("PakAudioPlayer", "Audio not found in PAK: $audioPath")
            return
        }

        try {
            val bytes = PakManager.readBytes(audioPath)
            val tempAudio = File(context.cacheDir, "temp_stream.ogg")
            FileOutputStream(tempAudio).use { it.write(bytes) }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempAudio.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    stop()
                    tempAudio.delete()
                    onComplete()
                }
            }
        } catch (e: Exception) {
            Log.e("PakAudioPlayer", "Audio error: ${e.message}")
        }
    }

    fun stop() {
        mediaPlayer?.run {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }
}
