package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.native.NativeEngine
import com.islamic.app.ui.components.UrlImage
import com.islamic.app.ui.theme.*
import org.json.JSONArray

data class SalahStepItem(val step: Int, val title: String, val arabic: String, val meaning: String, val imageUrl: String)

@Composable
fun SalahScreen(onBack: () -> Unit) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var steps by remember { mutableStateOf<List<SalahStepItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        if (NativeEngine.isLoaded()) {
            try {
                val arr = JSONArray(NativeEngine.getSalahGuideJson())
                val list = mutableListOf<SalahStepItem>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    list.add(SalahStepItem(o.getInt("step"), o.getString("title"), o.getString("arabic"), o.getString("meaning"), o.getString("imageUrl")))
                }
                steps = list
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    val current = steps.getOrNull(stepIndex) ?: SalahStepItem(1, "Takbir", "اللَّهُ أَكْبَرُ", "Allah is the Greatest", "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800")

    Column(modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPureWhite) }
            Text("Salah Guide", color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("Step ${current.step} of ${if (steps.isNotEmpty()) steps.size else 3}", color = MintSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(modifier = Modifier.fillMaxWidth().weight(1.3f).clip(RoundedCornerShape(26.dp))) {
            UrlImage(url = current.imageUrl, contentDescription = current.title, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.height(14.dp))
        Card(modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp)), colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(current.arabic, color = TextPureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(current.meaning, color = TextMuted, fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = DarkSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth().clickable {
                if (steps.isNotEmpty()) {
                    stepIndex = (stepIndex + 1) % steps.size
                }
            }
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, tint = MintSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Next Posture", color = TextPureWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
            }
        }
    }
}
