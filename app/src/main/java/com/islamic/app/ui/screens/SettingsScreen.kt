package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.downloader.DownloadState
import com.islamic.app.downloader.LibSosDownloader
import com.islamic.app.native.NativeEngine
import com.islamic.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val downloader = remember { LibSosDownloader(context) }
    var downloadState by remember { mutableStateOf<DownloadState>(DownloadState.Idle) }

    val file = NativeEngine.getInstalledFile(context)
    val isInstalled = file.exists() && file.length() > 1024 * 1024
    val mbSize = if (isInstalled) String.format("%.2f MB", file.length() / (1024.0 * 1024.0)) else "2.50 MB"

    Surface(modifier = Modifier.fillMaxSize().background(DarkOledBlack), color = DarkOledBlack) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Settings & Extensions", color = TextPureWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = null, tint = TextPureWhite) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Exact Single Native Lib Card (No PAK card whatsoever)
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Native Lib: libsos.so", color = TextPureWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.Code, contentDescription = null, tint = MintSecondary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Status: ", color = TextMuted, fontSize = 14.sp)
                        if (NativeEngine.isLoaded() || isInstalled) {
                            Text("Active", color = EmeraldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("($mbSize)", color = TextMuted, fontSize = 12.sp)
                        } else {
                            Text("Not Installed", color = MaterialTheme.colorScheme.error, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    when (val st = downloadState) {
                        is DownloadState.Running -> {
                            LinearProgressIndicator(progress = { st.percent / 100f }, color = EmeraldPrimary, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${st.percent}% Downloaded", color = TextMuted, fontSize = 12.sp)
                        }
                        is DownloadState.Failed -> {
                            Text("Error: ${st.error}", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        else -> {}
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                downloader.downloadAndAttach().collect { downloadState = it }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            if (NativeEngine.isLoaded() || isInstalled) "Re-download & Verify libsos.so" else "Download & Attach libsos.so",
                            color = DarkOledBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
