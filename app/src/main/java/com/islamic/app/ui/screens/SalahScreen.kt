package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.pak.LuaEngine
import com.islamic.app.pak.PakAudioPlayer
import com.islamic.app.ui.components.PakImage
import com.islamic.app.ui.theme.*

@Composable
fun SalahScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var stepIndex by remember { mutableIntStateOf(1) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPureWhite) }
            Text("Salah Guide", color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("Step $stepIndex", color = MintSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth().weight(1.3f), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = SoftIllustrationBg)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val img = if (stepIndex % 2 == 1) "images/salah/ruku.webp" else "images/salah/takbir.webp"
                PakImage(path = img, contentDescription = null, modifier = Modifier.fillMaxSize().padding(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Card(modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp)), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (stepIndex % 2 == 1) "SUBHAANA RABBIYAL\n'AZEEM (x3)" else "ALLAHU AKBAR", color = TextPureWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)
                    IconButton(
                        onClick = {
                            isPlayingAudio = true
                            PakAudioPlayer.play(context, "audio/takbir.ogg") { isPlayingAudio = false }
                        },
                        modifier = Modifier.clip(CircleShape).background(if (isPlayingAudio) EmeraldPrimary else DarkCardBorder)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = if (isPlayingAudio) DarkOledBlack else TextPureWhite)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Controlled dynamically via CorePatch PAK binary.", color = TextMuted, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = DarkSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth().clickable {
                stepIndex = if (stepIndex >= 4) 1 else stepIndex + 1
                LuaEngine.detectAndPlay(if (stepIndex % 2 == 1) "SubhanAllah" else "Allahu Akbar")
            }
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, tint = MintSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Next Posture (Run Lua Zikr)", color = TextPureWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
            }
        }
    }
}
