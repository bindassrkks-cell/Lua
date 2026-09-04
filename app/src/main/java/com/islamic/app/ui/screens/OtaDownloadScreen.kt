package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.downloader.DownloadState
import com.islamic.app.downloader.LibSosDownloader
import com.islamic.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OtaDownloadScreen(downloader: LibSosDownloader, onReady: () -> Unit) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf<DownloadState>(DownloadState.Idle) }

    LaunchedEffect(Unit) {
        scope.launch {
            downloader.downloadAndAttach().collect {
                state = it
                if (it is DownloadState.Success) onReady()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkOledBlack).padding(24.dp), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
        ) {
            Column(modifier = Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(18.dp))
                Text("Installing Core Engine", color = TextPureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("libsos.so (~2.5 MB) required for Islamic feeds, audio reciters, and Gemini AI.", color = TextMuted, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(24.dp))

                when (val st = state) {
                    is DownloadState.Idle, is DownloadState.Connecting -> {
                        LinearProgressIndicator(color = EmeraldPrimary, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Connecting to release server...", color = TextMuted, fontSize = 12.sp)
                    }
                    is DownloadState.Running -> {
                        LinearProgressIndicator(progress = { st.percent / 100f }, color = EmeraldPrimary, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        val downMb = String.format("%.2f", st.downloadedBytes / (1024.0 * 1024.0))
                        val totalMb = String.format("%.2f", st.totalBytes / (1024.0 * 1024.0))
                        Text("${st.percent}% ($downMb MB / $totalMb MB)", color = TextPureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    is DownloadState.Success -> {
                        Text("Installed Successfully (${st.sizeFormatted})", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                    is DownloadState.Failed -> {
                        Text("Error: ${st.error}", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    downloader.downloadAndAttach().collect {
                                        state = it
                                        if (it is DownloadState.Success) onReady()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("Retry Download", color = DarkOledBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
