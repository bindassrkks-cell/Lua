package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.downloader.PakDownloader
import com.islamic.app.downloader.UpdateState
import com.islamic.app.social.NativeSocialEngine
import com.islamic.app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingsDialog(onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val downloader = remember { PakDownloader(context) }
    var pakStatus by remember { mutableStateOf<UpdateState>(UpdateState.Idle) }
    var libStatus by remember { mutableStateOf(if (NativeSocialEngine.isLibActive()) "Active" else "Not Installed") }
    var isDownloadingLib by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize().background(DarkOledBlack), color = DarkOledBlack) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Settings & Extensions", color = TextPureWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = null, tint = TextPureWhite) }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // libsos.so Dynamic Loader Card
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Native Lib: libsos.so", color = TextPureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Status: $libStatus", color = if (libStatus == "Active") EmeraldPrimary else TextMuted, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.Code, contentDescription = null, tint = EmeraldPrimary)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                isDownloadingLib = true
                                withContext(Dispatchers.IO) {
                                    try {
                                        val libDir = File(context.filesDir, "lib")
                                        if (!libDir.exists()) libDir.mkdirs()
                                        val targetFile = File(libDir, "libsos.so")

                                        val client = OkHttpClient()
                                        val req = Request.Builder().url("https://github.com/bindassrkks-cell/Lua/releases/download/v0.0.15.0/libsos.so").build()
                                        val resp = client.newCall(req).execute()
                                        if (resp.isSuccessful) {
                                            resp.body?.byteStream()?.use { input ->
                                                FileOutputStream(targetFile).use { out -> input.copyTo(out) }
                                            }
                                            NativeSocialEngine.loadNativeLib(context)
                                            libStatus = "Active"
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isDownloadingLib = false
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        enabled = !isDownloadingLib
                    ) {
                        Text(if (isDownloadingLib) "Downloading libsos.so..." else "Download & Attach libsos.so", color = DarkOledBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CorePatch PAK Card
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("CorePatch PAK 0.0.15.0", color = TextPureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Binary scripts, audios, and assets", color = TextMuted, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = EmeraldPrimary)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    when (val st = pakStatus) {
                        is UpdateState.Idle -> {
                            Button(
                                onClick = { scope.launch { downloader.syncCorePatch().collect { pakStatus = it } } },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Download & Mount PAK", color = DarkOledBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                        is UpdateState.Checking -> Text("Verifying SHA256...", color = TextMuted)
                        is UpdateState.Progress -> LinearProgressIndicator(progress = { st.percentage / 100f }, color = EmeraldPrimary, modifier = Modifier.fillMaxWidth())
                        is UpdateState.Completed -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(st.message, color = EmeraldPrimary)
                            }
                        }
                        is UpdateState.Error -> Text("Error: ${st.error}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
