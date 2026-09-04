package com.islamic.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.islamic.app.pak.LuaEngine
import com.islamic.app.pak.PakManager
import com.islamic.app.social.NativeSocialEngine
import com.islamic.app.ui.screens.*
import com.islamic.app.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PakManager.init(this)
        LuaEngine.init(this)
        NativeSocialEngine.loadNativeLib(this)

        setContent {
            IslamicAppTheme {
                var currentRoute by remember { mutableStateOf("home") }
                var showSettingsDialog by remember { mutableStateOf(false) }

                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = DarkSurfaceCard) {
                            NavigationBarItem(
                                selected = currentRoute == "home",
                                onClick = { currentRoute = "home" },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary, unselectedIconColor = TextMuted,
                                    selectedTextColor = EmeraldPrimary, unselectedTextColor = TextMuted,
                                    indicatorColor = DarkCardBorder
                                )
                            )
                            NavigationBarItem(
                                selected = currentRoute == "salah",
                                onClick = { currentRoute = "salah" },
                                icon = { Icon(Icons.Default.AccessibilityNew, contentDescription = "Salah") },
                                label = { Text("Salah") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary, unselectedIconColor = TextMuted,
                                    selectedTextColor = EmeraldPrimary, unselectedTextColor = TextMuted,
                                    indicatorColor = DarkCardBorder
                                )
                            )
                            NavigationBarItem(
                                selected = currentRoute == "wudhu",
                                onClick = { currentRoute = "wudhu" },
                                icon = { Icon(Icons.Default.WaterDrop, contentDescription = "Wudhu") },
                                label = { Text("Wudhu") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary, unselectedIconColor = TextMuted,
                                    selectedTextColor = EmeraldPrimary, unselectedTextColor = TextMuted,
                                    indicatorColor = DarkCardBorder
                                )
                            )
                            NavigationBarItem(
                                selected = currentRoute == "social",
                                onClick = { currentRoute = "social" },
                                icon = { Icon(Icons.Default.OndemandVideo, contentDescription = "Social") },
                                label = { Text("Social") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary, unselectedIconColor = TextMuted,
                                    selectedTextColor = EmeraldPrimary, unselectedTextColor = TextMuted,
                                    indicatorColor = DarkCardBorder
                                )
                            )
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentRoute) {
                            "home" -> HomeScreen(onOpenSettings = { showSettingsDialog = true })
                            "salah" -> SalahScreen(onBack = { currentRoute = "home" })
                            "wudhu" -> WudhuScreen(onBack = { currentRoute = "home" })
                            "social" -> SocialScreen(onOpenSettings = { showSettingsDialog = true })
                        }

                        if (showSettingsDialog) {
                            SettingsDialog(onClose = { showSettingsDialog = false })
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PakManager.closeAll()
    }
}
