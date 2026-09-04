package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.ui.theme.*

data class PrayerInfo(val name: String, val rakahs: String, val time: String, val icon: ImageVector, val isCurrent: Boolean = false)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val prayers = listOf(
        PrayerInfo("Fajr", "2 Rakahs", "05:39", Icons.Default.WbTwilight),
        PrayerInfo("Dhuhr", "4 Rakahs", "11:48", Icons.Default.WbSunny),
        PrayerInfo("Asr", "4 Rakahs", "13:39", Icons.Default.FilterDrama, isCurrent = true),
        PrayerInfo("Maghrib", "3 Rakahs", "15:57", Icons.Default.NightsStay),
        PrayerInfo("Isha", "4 Rakahs", "17:28", Icons.Default.Bedtime)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding().padding(horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(50), color = DarkSurfaceCard, border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)) {
                Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("London", color = TextPureWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                }
            }
            IconButton(onClick = { onNavigate("settings") }, modifier = Modifier.clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkCardBorder, CircleShape)) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPureWhite)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.FilterDrama, contentDescription = null, tint = TextMuted, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text("Asr will end in", color = TextMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text("01h:42m", color = TextPureWhite, fontSize = 42.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Maghrib starts at 15:57", color = TextMuted, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            items(prayers) { prayer ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    modifier = Modifier.fillMaxWidth().border(1.dp, if (prayer.isCurrent) EmeraldPrimary.copy(alpha = 0.6f) else DarkCardBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Icon(prayer.icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                            if (prayer.isCurrent) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(DarkActiveBadgeBg).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("Now", color = DarkActiveBadge, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(prayer.name, color = TextPureWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(prayer.rakahs, color = TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(prayer.time, color = TextPureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
