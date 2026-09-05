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
    private var mediaPlayer: MediaPlayer? = null
    var currentlyPlayingUrl: String? = null

    suspend fun fetchSurahList(): List<Surah> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Surah>()
        try {
            val url = URL("https://api.alquran.cloud/v1/surah")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 7000
            conn.readTimeout = 7000

            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val json = JSONObject(reader.readText())
                val data = json.getJSONArray("data")
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    list.add(Surah(
                        item.getInt("number"),
                        item.getString("name"),
                        item.getString("englishName"),
                        item.getInt("numberOfAyahs")
                    ))
                }
            }
        } catch (e: Exception) {
            // Offline Safe Fallback
            list.addAll(listOf(
                Surah(1, "الفَاتِحة", "Al-Fatihah", 7),
                Surah(2, "البَقَرَة", "Al-Baqarah", 286),
                Surah(36, "يس", "Ya-Sin", 83),
                Surah(55, "الرَّحْمَٰن", "Ar-Rahman", 78),
                Surah(67, "المُلْك", "Al-Mulk", 30),
                Surah(112, "الإِخْلَاص", "Al-Ikhlas", 4)
            ))
        }
        list
    }

    suspend fun fetchSurahDetails(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        val ayahs = mutableListOf<Ayah>()
        try {
            val url = URL("https://api.alquran.cloud/v1/surah/$surahNumber/ar.alafasy")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val json = JSONObject(reader.readText())
                val data = json.getJSONObject("data")
                val ayahsArray = data.getJSONArray("ayahs")
                for (i in 0 until ayahsArray.length()) {
                    val a = ayahsArray.getJSONObject(i)
                    ayahs.add(Ayah(
                        a.getInt("numberInSurah"),
                        a.getString("text"),
                        a.optString("audio", "")
                    ))
                }
            }
        } catch (e: Exception) {
            ayahs.add(Ayah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", ""))
            ayahs.add(Ayah(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", ""))
        }
        ayahs
    }

    fun playAudio(url: String, onComplete: () -> Unit) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            if (url.isBlank()) return

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    currentlyPlayingUrl = url
                    start()
                }
                setOnCompletionListener {
                    currentlyPlayingUrl = null
                    onComplete()
                }
            }
        } catch (e: Exception) {
            currentlyPlayingUrl = null
        }
    }

    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentlyPlayingUrl = null
    }
}