package com.muslimcommunity.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

enum class ActiveScreen { Home, Quran, Prayer, Settings, SurahDetail }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicSyncManager.initStatus(this)
        setContent {
            MuslimApp()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        QuranRepository.stopAudio()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuslimApp() {
    var currentScreen by remember { mutableStateOf(ActiveScreen.Home) }
    var selectedSurah by remember { mutableStateOf<Surah?>(null) }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFF10B981),
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B)
    )) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (currentScreen == ActiveScreen.SurahDetail) (selectedSurah?.englishName ?: "Surah") else "Muslim Community",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        if (currentScreen == ActiveScreen.Settings || currentScreen == ActiveScreen.SurahDetail) {
                            IconButton(onClick = { currentScreen = ActiveScreen.Home }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    },
                    actions = {
                        if (currentScreen != ActiveScreen.Settings) {
                            IconButton(onClick = { currentScreen = ActiveScreen.Settings }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF10B981))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            bottomBar = {
                if (currentScreen != ActiveScreen.Settings && currentScreen != ActiveScreen.SurahDetail) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        NavigationBarItem(
                            selected = currentScreen == ActiveScreen.Home,
                            onClick = { currentScreen = ActiveScreen.Home },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == ActiveScreen.Quran,
                            onClick = { currentScreen = ActiveScreen.Quran },
                            icon = { Icon(Icons.Default.Star, contentDescription = "Quran") },
                            label = { Text("Quran API") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == ActiveScreen.Prayer,
                            onClick = { currentScreen = ActiveScreen.Prayer },
                            icon = { Icon(Icons.Default.DateRange, contentDescription = "Prayer") },
                            label = { Text("Prayer") }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                when (currentScreen) {
                    ActiveScreen.Home -> HomeScreen(onOpenSettings = { currentScreen = ActiveScreen.Settings })
                    ActiveScreen.Quran -> QuranScreen(onSelectSurah = { surah ->
                        selectedSurah = surah
                        currentScreen = ActiveScreen.SurahDetail
                    })
                    ActiveScreen.Prayer -> PrayerScreen()
                    ActiveScreen.Settings -> SettingsScreen()
                    ActiveScreen.SurahDetail -> selectedSurah?.let { SurahDetailScreen(it) }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onOpenSettings: () -> Unit) {
    val nativeText = remember { runCatching { NativeCore.getEngineInfo() }.getOrDefault("Native Ready") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Ayat of the Day", color = Color(0xFFA7F3D0), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("“Indeed, Allah is with those who are patient.”", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Surah Al-Baqarah 2:153", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().clickable { onOpenSettings() }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("GitHub Dynamic Architecture", color = Color.White, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF10B981))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(nativeText, color = Color(0xFF10B981), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tap to view repo modules, build metadata & sync status", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var modules by remember { mutableStateOf(DynamicSyncManager.moduleList.toList()) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("GitHub Actions Build Info", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Repository: bindassrkks-cell/Lua", color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
                    Text("Active Engine: Kotlin 2.0 + Compose BOM + NDK 26.3", color = Color.LightGray, fontSize = 12.sp)
                    Text("Build System: Split-ABI Ultra-Light (2-4 MB)", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }
        item {
            Text("Dynamic Repository Modules (modules/)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Downloaded once and preserved locally. Never re-asks download.", color = Color.Gray, fontSize = 12.sp)
        }
        items(modules) { mod ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mod.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text("Path: modules/${mod.type}/${mod.fileName}", color = Color(0xFF10B981), fontSize = 12.sp)
                        Text(if (mod.isSynced) "Status: Synced & Verified (Offline Safe)" else "Status: Pending Sync", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                DynamicSyncManager.syncModule(context, mod, force = true)
                                modules = DynamicSyncManager.moduleList.toList()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mod.isSynced) Color(0xFF047857) else Color(0xFF2563EB)
                        )
                    ) {
                        Text(if (mod.isSynced) "Synced" else "Sync", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuranScreen(onSelectSurah: (Surah) -> Unit) {
    var surahs by remember { mutableStateOf<List<Surah>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        surahs = QuranRepository.fetchSurahList()
        loading = false
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF10B981))
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(surahs) { surah ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onSelectSurah(surah) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${surah.number}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(surah.englishName, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("${surah.verses} Verses", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        Text(surah.name, color = Color(0xFF10B981), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SurahDetailScreen(surah: Surah) {
    var ayahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var playingIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(surah.number) {
        ayahs = QuranRepository.fetchSurahDetails(surah.number)
        loading = false
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF10B981))
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF047857)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(surah.name, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text(surah.englishName, color = Color(0xFFA7F3D0), fontSize = 14.sp)
                    }
                }
            }
            items(ayahs) { ayah ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Ayah ${ayah.numberInSurah}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (ayah.audioUrl.isNotBlank()) {
                                IconButton(onClick = {
                                    if (playingIndex == ayah.numberInSurah) {
                                        QuranRepository.stopAudio()
                                        playingIndex = null
                                    } else {
                                        playingIndex = ayah.numberInSurah
                                        QuranRepository.playAudio(ayah.audioUrl) {
                                            playingIndex = null
                                        }
                                    }
                                }) {
                                    Icon(
                                        if (playingIndex == ayah.numberInSurah) Icons.Default.Check else Icons.Default.Star,
                                        contentDescription = "Audio Play",
                                        tint = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ayah.text,
                            color = Color.White,
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerScreen() {
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