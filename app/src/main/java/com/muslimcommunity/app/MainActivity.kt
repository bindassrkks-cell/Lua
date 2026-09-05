package com.muslimcommunity.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcommunity.app.dynamic.DynamicDependency
import com.muslimcommunity.app.dynamic.DynamicDependencyManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicDependencyManager.init(this)
        setContent {
            MuslimCommunityApp()
        }
    }
}

@Composable
fun MuslimCommunityApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val nativeText = remember { runCatching { NativeCore.getEngineInfo() }.getOrDefault("Native Core Loaded") }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFF10B981),
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B)
    )) {
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    val items = listOf("Home", "Dynamic Libs", "Quran", "Prayer")
                    val icons = listOf(Icons.Default.Home, Icons.Default.Refresh, Icons.Default.Star, Icons.Default.DateRange)
                    items.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(icons[index], contentDescription = title) },
                            label = { Text(title) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                when (selectedTab) {
                    0 -> HomeTab(nativeText)
                    1 -> DynamicLibsTab()
                    2 -> QuranTab()
                    3 -> PrayerTab()
                }
            }
        }
    }
}

@Composable
fun HomeTab(nativeText: String) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Daily Ayat", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("“Indeed, Allah is with those who are patient.”", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Surah Al-Baqarah 2:153", color = Color(0xFFA7F3D0), fontSize = 12.sp)
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dynamic Architecture Status", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(nativeText, color = Color(0xFF10B981), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Base APK: ~2.5 MB • Dependencies: Dynamic Loaded", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DynamicLibsTab() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var dependencies by remember { mutableStateOf(DynamicDependencyManager.registry.toList()) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Dynamic Library Manager", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Modules and dependencies load dynamically on-demand", color = Color.Gray, fontSize = 12.sp)
        }
        items(dependencies) { dep ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dep.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(
                            if (dep.isNative) "Native .SO Library" else "Dynamic DEX Module",
                            color = Color(0xFF10B981),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                DynamicDependencyManager.downloadAndLoad(context, dep)
                                dependencies = DynamicDependencyManager.registry.toList()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (dep.isInstalled) Color(0xFF047857) else Color(0xFF2563EB)
                        )
                    ) {
                        Text(if (dep.isInstalled) "Loaded" else "Load Lib", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuranTab() {
    val surahs = listOf("1. Al-Fatihah", "2. Al-Baqarah", "36. Ya-Sin", "55. Ar-Rahman", "67. Al-Mulk")
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(surahs) { surah ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                Text(surah, modifier = Modifier.padding(16.dp), color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun PrayerTab() {
    val prayers = listOf("Fajr" to "05:00 AM", "Dhuhr" to "12:15 PM", "Asr" to "03:45 PM", "Maghrib" to "06:10 PM", "Isha" to "07:30 PM")
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(prayers) { (name, time) ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(time, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}