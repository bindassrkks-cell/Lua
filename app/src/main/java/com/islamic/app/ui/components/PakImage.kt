package com.islamic.app.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.islamic.app.pak.PakManager
import com.islamic.app.ui.theme.DarkCardBorder
import com.islamic.app.ui.theme.MintSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PakImage(path: String, contentDescription: String?, modifier: Modifier = Modifier) {
    var bitmap by remember(path) { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(path) {
        withContext(Dispatchers.IO) {
            try {
                if (PakManager.exists(path)) {
                    val bytes = PakManager.readBytes(path)
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    if (bitmap != null) {
        Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = contentDescription, modifier = modifier, contentScale = ContentScale.Crop)
    } else {
        Box(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(DarkCardBorder), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Mosque, contentDescription = null, tint = MintSecondary)
        }
    }
}
