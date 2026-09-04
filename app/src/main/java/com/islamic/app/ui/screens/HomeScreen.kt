package com.islamic.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.prayer.CalculatedTimes
import com.islamic.app.prayer.LocationHelper
import com.islamic.app.prayer.PrayerEngine
import com.islamic.app.prayer.UserLocation
import com.islamic.app.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar

data class PrayerItemUi(val name: String, val rakahs: String, val time: String, val icon: ImageVector, val isCurrent: Boolean)

@Composable
fun HomeScreen(onOpenSettings: () -> Unit) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf(UserLocation("Dhanbad", 23.7957, 86.4304)) }
    var times by remember { mutableStateOf<CalculatedTimes?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        userLocation = LocationHelper.getDeviceLocation(context)
    }

    LaunchedEffect(userLocation) {
        while (true) {
            times = PrayerEngine.calculate(userLocation.latitude, userLocation.longitude, Calendar.getInstance())
            delay(1000L)
        }
    }

    val currentTimes = times ?: remember {
        PrayerEngine.calculate(userLocation.latitude, userLocation.longitude, Calendar.getInstance())
    }

    fun fmt(c: Calendar) = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))

    val prayerList = listOf(
        PrayerItemUi("Fajr", "2 Rakahs", fmt(currentTimes.fajr), Icons.Default.WbTwilight, currentTimes.currentPrayer == "Fajr"),
        PrayerItemUi("Dhuhr", "4 Rakahs", fmt(currentTimes.dhuhr), Icons.Default.WbSunny, currentTimes.currentPrayer == "Dhuhr"),
        PrayerItemUi("Asr", "4 Rakahs", fmt(currentTimes.asr), Icons.Default.FilterDrama, currentTimes.currentPrayer == "Asr"),
        PrayerItemUi("Maghrib", "3 Rakahs", fmt(currentTimes.maghrib), Icons.Default.NightsStay, currentTimes.currentPrayer == "Maghrib"),
        PrayerItemUi("Isha", "4 Rakahs", fmt(currentTimes.isha), Icons.Default.Bedtime, currentTimes.currentPrayer == "Isha")
    )

    Column(modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(50), color = DarkSurfaceCard, border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)) {
                Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(userLocation.city, color = TextPureWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                }
            }
            IconButton(onClick = onOpenSettings, modifier = Modifier.clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkCardBorder, CircleShape)) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPureWhite)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                if (currentTimes.currentPrayer == "Isha") Icons.Default.Bedtime else Icons.Default.FilterDrama,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("${currentTimes.currentPrayer} will end in", color = TextMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(currentTimes.remainingFormatted, color = TextPureWhite, fontSize = 38.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${currentTimes.nextPrayer} starts at ${currentTimes.nextPrayerTimeFormatted}", color = TextMuted, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(18.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            items(prayerList) { prayer ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    modifier = Modifier.fillMaxWidth().border(1.dp, if (prayer.isCurrent) EmeraldPrimary.copy(alpha = 0.7f) else DarkCardBorder, RoundedCornerShape(20.dp))
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
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
