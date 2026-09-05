package com.muslimcommunity.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class IosScreen { Home, Quran, Prayer, Settings, SurahDetail }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WasmEngine.loadAndInitialize(this)
        setContent {
            MuslimCommunityApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        QuranRepository.stopAudio()
    }
}

@Composable
fun MuslimCommunityApp() {
    var currentScreen by remember { mutableStateOf(IosScreen.Home) }
    var activeSurah by remember { mutableStateOf<Surah?>(null) }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFF10B981),
        background = Color(0xFF090D16),
        surface = Color(0xFF121B2B)
    )) {
        Scaffold(
            topBar = {
                IosTopBar(
                    title = if (currentScreen == IosScreen.SurahDetail) (activeSurah?.englishName ?: "Surah") else "Muslim Community",
                    showBack = currentScreen == IosScreen.Settings || currentScreen == IosScreen.SurahDetail,
                    onBack = { currentScreen = IosScreen.Home },
                    onOpenSettings = { currentScreen = IosScreen.Settings },
                    isSettings = currentScreen == IosScreen.Settings
                )
            },
            bottomBar = {
                if (currentScreen != IosScreen.Settings && currentScreen != IosScreen.SurahDetail) {
                    IosBottomBar(currentScreen) { currentScreen = it }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize().background(Color(0xFF090D16))) {
                when (currentScreen) {
                    IosScreen.Home -> HomeScreen(onOpenSettings = { currentScreen = IosScreen.Settings })
                    IosScreen.Quran -> QuranScreen(onSelect = {
                        activeSurah = it
                        currentScreen = IosScreen.SurahDetail
                    })
                    IosScreen.Prayer -> PrayerScreen()
                    IosScreen.Settings -> SettingsScreen()
                    IosScreen.SurahDetail -> activeSurah?.let { SurahDetailScreen(it) }
                }
            }
        }
    }
}

@Composable
fun IosTopBar(title: String, showBack: Boolean, onBack: () -> Unit, onOpenSettings: () -> Unit, isSettings: Boolean) {
    Surface(
        color = Color(0xFF0F1726).copy(alpha = 0.96f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBack) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1C263A),
                        modifier = Modifier.size(38.dp).clickable { onBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column {
                    Text("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (!isSettings) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1C263A),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
                    modifier = Modifier.size(40.dp).clickable { onOpenSettings() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun IosBottomBar(current: IosScreen, onSelect: (IosScreen) -> Unit) {
    Surface(
        color = Color(0xFF0F1726).copy(alpha = 0.96f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IosBarItem("Home", Icons.Default.Home, current == IosScreen.Home) { onSelect(IosScreen.Home) }
            IosBarItem("Quran", Icons.Default.Star, current == IosScreen.Quran) { onSelect(IosScreen.Quran) }
            IosBarItem("Prayer", Icons.Default.DateRange, current == IosScreen.Prayer) { onSelect(IosScreen.Prayer) }
        }
    }
}

@Composable
fun IosBarItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(14.dp)).clickable { onClick() }.padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = if (selected) Color(0xFF10B981) else Color(0xFF64748B), modifier = Modifier.size(22.dp))
        Text(label, color = if (selected) Color(0xFF10B981) else Color(0xFF64748B), fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun HomeScreen(onOpenSettings: () -> Unit) {
    val wasmInfo = WasmEngine.moduleInfo
    var azkarTaps by remember { mutableIntStateOf(0) }
    val azkars = listOf("سُبْحَانَ اللَّهِ (SubhanAllah)", "الْحَمْدُ لِلَّهِ (Alhamdulillah)", "اللَّهُ أَكْبَرُ (Allahu Akbar)", "لَا إِلَٰهَ إِلَّا ٱللَّٰهُ (La ilaha illallah)")
    var azkarIdx by remember { mutableIntStateOf(0) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // iOS Glassmorphic VFX Banner
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.background(Brush.linearGradient(listOf(Color(0xFF047857), Color(0xFF064E3B), Color(0xFF022C22)))).padding(20.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFF10B981).copy(alpha = 0.3f), modifier = Modifier.size(8.dp)) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WASM RUNTIME POWERED", color = Color(0xFFA7F3D0), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("“So remember Me; I will remember you.”", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Surah Al-Baqarah 2:152", color = Color(0xFF6EE7B7), fontSize = 12.sp)
                    }
                }
            }
        }

        // iOS Mobile-Friendly Azkar Counter
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF121B2B),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Daily Azkar Counter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Daily Goal: 33x", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0A0F19),
                        modifier = Modifier.fillMaxWidth().clickable {
                            azkarIdx = (azkarIdx + 1) % azkars.size
                            azkarTaps = 0
                        }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(azkars[azkarIdx], color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("$azkarTaps", color = Color(0xFF10B981), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Button(
                            onClick = { azkarTaps++ },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("+ Count Azkar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Wasm Embedded Engine Status Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF121B2B),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth().clickable { onOpenSettings() }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Embedded .WASM Binary", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        if (wasmInfo?.isVerified == true) "Valid Wasm Magic Verified (Bytecode Loaded)" else "Native Engine Initialized",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Self-Contained • All Modules Packed Inside APK", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val info = WasmEngine.moduleInfo

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("WebAssembly (.wasm) System Details", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF121B2B),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Binary: ${info?.name ?: "muslim_core.wasm"}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    Text("Payload Size: ${info?.byteSize ?: 0} Bytes (Embedded in Assets)", color = Color.LightGray, fontSize = 12.sp)
                    Text("Magic Verification: ${if (info?.isVerified == true) "PASSED (Valid Wasm Binary)" else "OK"}", color = Color.LightGray, fontSize = 12.sp)
                    Text("Storage Mode: Zero External Download (Offline-Ready)", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }

        item {
            Text("Features Packed Inside .WASM", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        items(info?.packedFeatures ?: emptyList()) { feature ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF121B2B),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color(0xFF10B981).copy(alpha = 0.2f), modifier = Modifier.size(8.dp)) {}
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(feature, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun QuranScreen(onSelect: (Surah) -> Unit) {
    var surahs by remember { mutableStateOf<List<Surah>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        surahs = QuranRepository.getSurahs()
        loading = false
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF10B981))
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(surahs) { surah ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF121B2B),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(surah) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF090D16),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${surah.number}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(surah.englishName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
    var currentPlaying by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(surah.number) {
        ayahs = QuranRepository.getSurahAyahs(surah.number)
        loading = false
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF10B981))
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.background(Brush.linearGradient(listOf(Color(0xFF047857), Color(0xFF064E3B)))).padding(20.dp)) {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(surah.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(surah.englishName, color = Color(0xFFA7F3D0), fontSize = 14.sp)
                        }
                    }
                }
            }
            items(ayahs) { ayah ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF121B2B),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Ayah ${ayah.numberInSurah}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            if (ayah.audioUrl.isNotBlank()) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (currentPlaying == ayah.numberInSurah) Color(0xFF10B981) else Color(0xFF1C263A),
                                    modifier = Modifier.size(34.dp).clickable {
                                        if (currentPlaying == ayah.numberInSurah) {
                                            QuranRepository.stopAudio()
                                            currentPlaying = null
                                        } else {
                                            currentPlaying = ayah.numberInSurah
                                            QuranRepository.playAudio(ayah.audioUrl) {
                                                currentPlaying = null
                                            }
                                        }
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            if (currentPlaying == ayah.numberInSurah) Icons.Default.Check else Icons.Default.Star,
                                            contentDescription = "Recitation",
                                            tint = if (currentPlaying == ayah.numberInSurah) Color.Black else Color(0xFF10B981),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = ayah.text,
                            color = Color.White,
                            fontSize = 20.sp,
                            lineHeight = 34.sp,
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
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF121B2B),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(time, color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}