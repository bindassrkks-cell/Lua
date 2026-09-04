package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.islamic.app.ui.components.UrlImage
import com.islamic.app.ui.theme.*

data class WudhuStep(val title: String, val desc: String, val url: String)

@Composable
fun WudhuScreen(onBack: () -> Unit) {
    val steps = listOf(
        WudhuStep("Niyyah (Intention)", "Make the intention in your heart to perform wudhu for prayer.", "https://images.unsplash.com/photo-1542838132-92c53300491e?w=300"),
        WudhuStep("Washing Hands", "Wash both hands up to the wrists thoroughly three times.", "https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=300"),
        WudhuStep("Rinsing Mouth", "Rinse the mouth with water three times ensuring complete purity.", "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=300")
    )

    Column(modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPureWhite) }
            Text("Wudhu Guide", color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(vertical = 10.dp)) {
            itemsIndexed(steps) { index, step ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp))) {
                            UrlImage(url = step.url, contentDescription = step.title, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(step.title, color = TextPureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(step.desc, color = TextMuted, fontSize = 13.sp, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}
