package com.muslimcommunity.app

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class Surah(val number: Int, val name: String, val englishName: String, val verses: Int)
data class Ayah(val numberInSurah: Int, val text: String, val audioUrl: String)

object QuranRepository {
    private var player: MediaPlayer? = null
    var playingAudioUrl: String? = null

    suspend fun getSurahs(): List<Surah> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Surah>()
        try {
            val url = URL("https://api.alquran.cloud/v1/surah")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            if (conn.responseCode == 200) {
                val json = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                val data = json.getJSONArray("data")
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    list.add(Surah(item.getInt("number"), item.getString("name"), item.getString("englishName"), item.getInt("numberOfAyahs")))
                }
            }
        } catch (e: Exception) {
            list.addAll(listOf(
                Surah(1, "الفَاتِحة", "Al-Fatihah", 7),
                Surah(2, "البَقَرَة", "Al-Baqarah", 286),
                Surah(36, "يس", "Ya-Sin", 83),
                Surah(55, "الرَّحْمَٰن", "Ar-Rahman", 78),
                Surah(67, "المُلْك", "Al-Mulk", 30),
                Surah(112, "الإِخْلَاص", "Al-Ikhlas", 4),
                Surah(114, "النَّاس", "An-Nas", 6)
            ))
        }
        list
    }

    suspend fun getSurahAyahs(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Ayah>()
        try {
            val url = URL("https://api.alquran.cloud/v1/surah/$surahNumber/ar.alafasy")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            if (conn.responseCode == 200) {
                val json = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                val ayahs = json.getJSONObject("data").getJSONArray("ayahs")
                for (i in 0 until ayahs.length()) {
                    val item = ayahs.getJSONObject(i)
                    list.add(Ayah(item.getInt("numberInSurah"), item.getString("text"), item.optString("audio", "")))
                }
            }
        } catch (e: Exception) {
            list.add(Ayah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", ""))
            list.add(Ayah(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", ""))
        }
        list
    }

    fun playAudio(url: String, onFinished: () -> Unit) {
        stopAudio()
        if (url.isBlank()) return
        try {
            player = MediaPlayer().apply {
                setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build())
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    playingAudioUrl = url
                    start()
                }
                setOnCompletionListener {
                    playingAudioUrl = null
                    onFinished()
                }
            }
        } catch (e: Exception) {
            playingAudioUrl = null
        }
    }

    fun stopAudio() {
        player?.stop()
        player?.release()
        player = null
        playingAudioUrl = null
    }
}