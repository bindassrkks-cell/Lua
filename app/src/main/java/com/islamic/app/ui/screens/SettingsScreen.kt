package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.islamic.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val downloader = remember { PakDownloader(context) }
    var statusState by remember { mutableStateOf<UpdateState>(UpdateState.Idle) }

    Column(modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding().padding(20.dp)) {
        Text("Content Updates", color = TextPureWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Binary Content Packages via GitHub Releases", color = TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("CorePatch", color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Version: 0.0.15.0", color = TextMuted, fontSize = 13.sp)
                    }
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = EmeraldPrimary)
                }
                Spacer(modifier = Modifier.height(16.dp))

                when (val st = statusState) {
                    is UpdateState.Idle -> {
                        Button(
                            onClick = { scope.launch { downloader.syncCorePatch().collect { statusState = it } } },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("Download & Mount PAK", color = DarkOledBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                    is UpdateState.Checking -> Text("Checking release manifest...", color = TextMuted, fontSize = 13.sp)
                    is UpdateState.Progress -> {
                        LinearProgressIndicator(progress = { st.percentage / 100f }, modifier = Modifier.fillMaxWidth(), color = EmeraldPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${st.percentage}% Downloaded", color = TextMuted, fontSize = 12.sp)
                    }
                    is UpdateState.Completed -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(st.message, color = EmeraldPrimary, fontSize = 14.sp)
                        }
                    }
                    is UpdateState.Error -> Text("Error: ${st.error}", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        }
    }
}
