package com.islamic.app.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.islamic.app.ui.theme.DarkCardBorder
import com.islamic.app.ui.theme.MintSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

private val imageCache = android.util.LruCache<String, Bitmap>(20 * 1024 * 1024)
private val httpClient = OkHttpClient()

@Composable
fun UrlImage(url: String, contentDescription: String?, modifier: Modifier = Modifier) {
    var bmp by remember(url) { mutableStateOf<Bitmap?>(imageCache.get(url)) }

    LaunchedEffect(url) {
        if (bmp == null && url.isNotBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val req = Request.Builder().url(url).build()
                    val resp = httpClient.newCall(req).execute()
                    if (resp.isSuccessful) {
                        resp.body?.byteStream()?.use { input ->
                            val decoded = BitmapFactory.decodeStream(input)
                            if (decoded != null) {
                                imageCache.put(url, decoded)
                                bmp = decoded
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    if (bmp != null) {
        Image(bitmap = bmp!!.asImageBitmap(), contentDescription = contentDescription, modifier = modifier, contentScale = ContentScale.Crop)
    } else {
        Box(modifier = modifier.background(DarkCardBorder), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Mosque, contentDescription = null, tint = MintSecondary)
        }
    }
}
