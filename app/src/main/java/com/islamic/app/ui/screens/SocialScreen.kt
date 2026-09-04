package com.islamic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.pak.PakAudioPlayer
import com.islamic.app.social.NativeSocialEngine
import com.islamic.app.ui.components.PakImage
import com.islamic.app.ui.theme.*
import org.json.JSONArray

data class SocialPost(
    val id: String, val title: String, val channel: String,
    val views: String, val time: String, val duration: String,
    val image: String, val arabic: String, val translation: String,
    val audio: String
)

@Composable
fun SocialScreen(onOpenSettings: () -> Unit) {
    val context = LocalContext.current
    var posts by remember { mutableStateOf<List<SocialPost>>(emptyList()) }
    var selectedPost by remember { mutableStateOf<SocialPost?>(null) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val hasLib = NativeSocialEngine.loadNativeLib(context)
        if (hasLib) {
            try {
                val jsonStr = NativeSocialEngine.getSocialFeedJson()
                val array = JSONArray(jsonStr)
                val list = mutableListOf<SocialPost>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(SocialPost(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        channel = obj.getString("channel"),
                        views = obj.getString("views"),
                        time = obj.getString("time"),
                        duration = obj.getString("duration"),
                        image = obj.getString("image"),
                        arabic = obj.getString("arabic"),
                        translation = obj.getString("translation"),
                        audio = obj.getString("audio")
                    ))
                }
                posts = list
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = YoutubeRed, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Islamic Feed", color = TextPureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onOpenSettings, modifier = Modifier.clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkCardBorder, CircleShape)) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPureWhite)
                }
            }

            if (!NativeSocialEngine.isLibActive()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Native Extension Required", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Download 'libsos.so' in settings to unlock the full dynamic Islamic community feed.", color = TextMuted, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = onOpenSettings, colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)) {
                            Text("Open Settings to Download Lib", color = DarkOledBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // YouTube Style Feed List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(posts) { post ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPost = post }
                    ) {
                        // 16:9 Aspect Ratio Thumbnail
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(DarkSurfaceCard)) {
                            PakImage(path = post.image, contentDescription = post.title, modifier = Modifier.fillMaxSize())
                            // Duration Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkOledBlack.copy(alpha = 0.85f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(post.duration, color = TextPureWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        // Video Metadata
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(EmeraldPrimary.copy(alpha = 0.2f)).border(1.dp, EmeraldPrimary, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(post.title, color = TextPureWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("${post.channel} • ${post.views} • ${post.time}", color = TextMuted, fontSize = 12.sp)
                            }
                            IconButton(onClick = { selectedPost = post }) {
                                Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Post Details BottomSheet / Clean Modal
        selectedPost?.let { post ->
            Surface(
                modifier = Modifier.fillMaxSize().background(DarkOledBlack.copy(alpha = 0.96f)),
                color = DarkOledBlack.copy(alpha = 0.96f)
            ) {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            PakAudioPlayer.stop()
                            isPlayingAudio = false
                            selectedPost = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPureWhite)
                        }
                        Text("Post Details", color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            isPlayingAudio = true
                            PakAudioPlayer.play(context, post.audio) { isPlayingAudio = false }
                        }, modifier = Modifier.clip(CircleShape).background(if (isPlayingAudio) EmeraldPrimary else DarkSurfaceCard)) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Audio", tint = if (isPlayingAudio) DarkOledBlack else TextPureWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = SoftIllustrationBg)
                    ) {
                        PakImage(path = post.image, contentDescription = post.title, modifier = Modifier.fillMaxSize())
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(post.title, color = TextPureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${post.channel} • ${post.views} • ${post.time}", color = TextMuted, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Quranic Ayah / Zikr", color = MintSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                post.arabic,
                                color = TextPureWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 34.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = DarkCardBorder)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(post.translation, color = TextMuted, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }
    }
}
