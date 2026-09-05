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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

enum class NavScreen { Home, Quran, Prayer, Settings, SurahDetail }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicSyncManager.init(this)
        setContent {
            MuslimCommunityApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        QuranRepository.stopRecitation()
    }
}

@Composable
fun MuslimCommunityApp() {
    var currentScreen by remember { mutableStateOf(NavScreen.Home) }
    var selectedSurah by remember { mutableStateOf<Surah?>(null) }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFF10B981),
        background = Color(0xFF090D16),
        surface = Color(0xFF131B2A)
    )) {
        Scaffold(
            topBar = {
                IosHeader(
                    title = if (currentScreen == NavScreen.SurahDetail) (selectedSurah?.englishName ?: "Surah") else "Muslim Community",
                    subtitle = if (currentScreen == NavScreen.Home) "As-Salamu Alaykum" else null,
                    canGoBack = currentScreen == NavScreen.Settings || currentScreen == NavScreen.SurahDetail,
                    onBack = { currentScreen = NavScreen.Home },
                    onOpenSettings = { currentScreen = NavScreen.Settings },
                    isSettingsOpen = currentScreen == NavScreen.Settings
                )
            },
            bottomBar = {
                if (currentScreen != NavScreen.Settings && currentScreen != NavScreen.SurahDetail) {
                    IosBottomBar(currentScreen) { currentScreen = it }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFF090D16))) {
                when (currentScreen) {
                    NavScreen.Home -> HomeScreen(onOpenSettings = { currentScreen = NavScreen.Settings })
                    NavScreen.Quran -> QuranScreen(onSelect = { surah ->
                        selectedSurah = surah
                        currentScreen = NavScreen.SurahDetail
                    })
                    NavScreen.Prayer -> PrayerScreen()
                    NavScreen.Settings -> SettingsScreen()
                    NavScreen.SurahDetail -> selectedSurah?.let { SurahDetailScreen(it) }
                }
            }
        }
    }
}

@Composable
fun IosHeader(
    title: String,
    subtitle: String?,
    canGoBack: Boolean,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    isSettingsOpen: Boolean
) {
    Surface(
        color = Color(0xFF101726).copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (canGoBack) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1B2438),
                        modifier = Modifier.size(38.dp).clickable { onBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column {
                    if (subtitle != null) {
                        Text(subtitle, color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text(title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (!isSettingsOpen) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1B2438),
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
fun IosBottomBar(current: NavScreen, onNavigate: (NavScreen) -> Unit) {
    Surface(
        color = Color(0xFF101726).copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceAround) {
            IosNavItem("Home", Icons.Default.Home, current == NavScreen.Home) { onNavigate(NavScreen.Home) }
            IosNavItem("Quran", Icons.Default.Star, current == NavScreen.Quran) { onNavigate(NavScreen.Quran) }
            IosNavItem("Prayer", Icons.Default.DateRange, current == NavScreen.Prayer) { onNavigate(NavScreen.Prayer) }
        }
    }
}

@Composable
fun IosNavItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onClick() }.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = title, tint = if (selected) Color(0xFF10B981) else Color(0xFF64748B), modifier = Modifier.size(22.dp))
        Text(title, color = if (selected) Color(0xFF10B981) else Color(0xFF64748B), fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun HomeScreen(onOpenSettings: () -> Unit) {
    val nativeText = remember { runCatching { NativeCore.getEngineInfo() }.getOrDefault("Native Engine Active") }
    var azkarCount by remember { mutableIntStateOf(0) }
    val azkarList = listOf("سُبْحَانَ اللَّهِ (SubhanAllah)", "الْحَمْدُ لِلَّهِ (Alhamdulillah)", "اللَّهُ أَكْبَرُ (Allahu Akbar)", "أَسْتَغْفِرُ اللَّهَ (Astaghfirullah)")
    var azkarIndex by remember { mutableIntStateOf(0) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // iOS VFX Banner Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.background(Brush.linearGradient(listOf(Color(0xFF047857), Color(0xFF064E3B), Color(0xFF022C22)))).padding(20.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFF10B981).copy(alpha = 0.2f), modifier = Modifier.size(8.dp)) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DAILY AYAH VFX POST", color = Color(0xFFA7F3D0), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("“Indeed, Allah is with those who are patient.”", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Surah Al-Baqarah 2:153", color = Color(0xFF6EE7B7), fontSize = 12.sp)
                    }
                }
            }
        }

        // Daily Azkar iOS Interactive Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF131B2A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Daily Azkar Counter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Target: 33x", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0B1019),
                        modifier = Modifier.fillMaxWidth().clickable {
                            azkarIndex = (azkarIndex + 1) % azkarList.size
                            azkarCount = 0
                        }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(azkarList[azkarIndex], color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Count: $azkarCount", color = Color(0xFF10B981), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        Button(
                            onClick = { azkarCount++ },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("+ Count Azkar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Native UX & Dynamic Engine Status
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF131B2A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth().clickable { onOpenSettings() }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Native VFX Lib & Architecture", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(nativeText, color = Color(0xFF10B981), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Repository: bindassrkks-cell/Lua • Tap to inspect modules", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var modules by remember { mutableStateOf(DynamicSyncManager.registry.toList()) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("GitHub Actions Build Info", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF131B2A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Repository: bindassrkks-cell/Lua", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    Text("Engine: Kotlin 2.0 • Compose BOM • NDK 26.3", color = Color.LightGray, fontSize = 12.sp)
                    Text("Output: 2-4 MB Ultra-Light Split Builds", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }
        item {
            Text("Dynamic Repository Modules (modules/)", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text("Persistent local storage. Never re-asks download once synced.", color = Color.Gray, fontSize = 12.sp)
        }
        items(modules) { mod ->
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF131B2A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mod.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Path: modules/${mod.type}/${mod.fileName}", color = Color(0xFF10B981), fontSize = 11.sp)
                        Text(if (mod.isSynced) "Status: Synced (Offline Safe)" else "Status: Ready to Sync", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                DynamicSyncManager.syncModule(context, mod, force = true)
                                modules = DynamicSyncManager.registry.toList()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (mod.isSynced) Color(0xFF059669) else Color(0xFF2563EB))
                    ) {
                        Text(if (mod.isSynced) "Synced" else "Sync", fontSize = 12.sp)
                    }
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
                    color = Color(0xFF131B2A),
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
    var playingNumber by remember { mutableStateOf<Int?>(null) }

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
                    color = Color(0xFF131B2A),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Ayah ${ayah.numberInSurah}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            if (ayah.audioUrl.isNotBlank()) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (playingNumber == ayah.numberInSurah) Color(0xFF10B981) else Color(0xFF1B2438),
                                    modifier = Modifier.size(34.dp).clickable {
                                        if (playingNumber == ayah.numberInSurah) {
                                            QuranRepository.stopRecitation()
                                            playingNumber = null
                                        } else {
                                            playingNumber = ayah.numberInSurah
                                            QuranRepository.playRecitation(ayah.audioUrl) {
                                                playingNumber = null
                                            }
                                        }
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            if (playingNumber == ayah.numberInSurah) Icons.Default.Check else Icons.Default.Star,
                                            contentDescription = "Audio Play",
                                            tint = if (playingNumber == ayah.numberInSurah) Color.Black else Color(0xFF10B981),
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
                color = Color(0xFF131B2A),
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