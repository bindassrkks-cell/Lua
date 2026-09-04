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
import com.islamic.app.ui.components.PakImage
import com.islamic.app.ui.theme.*

data class WudhuStepUi(val title: String, val desc: String, val imagePath: String)

@Composable
fun WudhuScreen(onBack: () -> Unit) {
    val steps = listOf(
        WudhuStepUi("Niyyah (Intention)", "The intention for performing wudu is made in the heart...", "images/wudhu/step1.webp"),
        WudhuStepUi("Wash hands three times", "First wash the right hand, then the left, up to wrists...", "images/wudhu/step1.webp"),
        WudhuStepUi("Rinse mouth three times", "Rinse mouth thoroughly using clean water...", "images/wudhu/step1.webp")
    )

    Column(modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPureWhite) }
            Text("Wudhu Guide", color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(vertical = 10.dp)) {
            itemsIndexed(steps) { index, step ->
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(68.dp).clip(RoundedCornerShape(18.dp)).background(DarkCardBorder), contentAlignment = Alignment.Center) {
                                PakImage(path = step.imagePath, contentDescription = step.title, modifier = Modifier.size(54.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(step.title, color = TextPureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(step.desc, color = TextMuted, fontSize = 13.sp, maxLines = 2)
                            }
                        }
                    }
                    if (index < steps.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkCardBorder, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
