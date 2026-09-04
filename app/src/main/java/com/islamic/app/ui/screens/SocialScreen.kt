package com.islamic.app.ui.screens

import android.media.MediaPlayer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.app.native.NativeEngine
import com.islamic.app.ui.components.UrlImage
import com.islamic.app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

data class SocialPost(
    val id: String, val title: String, val channel: String,
    val views: String, val time: String, val duration: String,
    val imageUrl: String, val arabic: String, val translation: String,
    val audioUrl: String
)

@Composable
fun SocialScreen(onOpenSettings: () -> Unit) {
    val scope = rememberCoroutineScope()
    var posts by remember { mutableStateOf<List<SocialPost>>(emptyList()) }
    var selectedPost by remember { mutableStateOf<SocialPost?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }

    var aiPrompt by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("") }
    var isAiThinking by remember { mutableStateOf(false) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LaunchedEffect(Unit) {
        if (NativeEngine.isLoaded()) {
            try {
                val jsonStr = NativeEngine.getSocialFeedJson()
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
                        imageUrl = obj.getString("imageUrl"),
                        arabic = obj.getString("arabic"),
                        translation = obj.getString("translation"),
                        audioUrl = obj.getString("audioUrl")
                    ))
                }
                posts = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkOledBlack).statusBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = YoutubeRed, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Islamic Media", color = TextPureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Row {
                    IconButton(onClick = { showAiDialog = true }, modifier = Modifier.clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkCardBorder, CircleShape)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Gemini AI", tint = EmeraldPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onOpenSettings, modifier = Modifier.clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkCardBorder, CircleShape)) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPureWhite)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(posts) { post ->
                    Column(
                        modifier = Modifier.fillMaxWidth().clickable { selectedPost = post }
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(DarkSurfaceCard)) {
                            UrlImage(url = post.imageUrl, contentDescription = post.title, modifier = Modifier.fillMaxSize())
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
                        }
                    }
                }
            }
        }

        selectedPost?.let { post ->
            Surface(modifier = Modifier.fillMaxSize().background(DarkOledBlack), color = DarkOledBlack) {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            mediaPlayer?.stop()
                            isPlayingAudio = false
                            selectedPost = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextPureWhite)
                        }
                        Text("Details & Recitation", color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        IconButton(
                            onClick = {
                                if (isPlayingAudio) {
                                    mediaPlayer?.stop()
                                    isPlayingAudio = false
                                } else {
                                    mediaPlayer?.release()
                                    mediaPlayer = MediaPlayer().apply {
                                        setDataSource(post.audioUrl)
                                        prepareAsync()
                                        setOnPreparedListener {
                                            start()
                                            isPlayingAudio = true
                                        }
                                        setOnCompletionListener {
                                            isPlayingAudio = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.clip(CircleShape).background(if (isPlayingAudio) EmeraldPrimary else DarkSurfaceCard)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = if (isPlayingAudio) DarkOledBlack else TextPureWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Card(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(18.dp))) {
                        UrlImage(url = post.imageUrl, contentDescription = post.title, modifier = Modifier.fillMaxSize())
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(post.title, color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                post.arabic,
                                color = TextPureWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 32.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = DarkCardBorder)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(post.translation, color = TextMuted, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }

        if (showAiDialog) {
            Surface(modifier = Modifier.fillMaxSize().background(DarkOledBlack), color = DarkOledBlack) {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini Islamic Scholar", color = TextPureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { showAiDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextPureWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = aiPrompt,
                        onValueChange = { aiPrompt = it },
                        label = { Text("Ask any Islamic Question...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (aiPrompt.isNotBlank()) {
                                isAiThinking = true
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val endpoint = NativeEngine.getGeminiEndpoint()
                                            val client = OkHttpClient()
                                            val payload = JSONObject().apply {
                                                put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", "You are an Islamic scholar. Answer clearly: $aiPrompt")))))
                                            }
                                            val req = Request.Builder()
                                                .url("$endpoint?key=AIzaSyDummyKeyReplaceWithYourRealKey")
                                                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                                                .build()
                                            val resp = client.newCall(req).execute()
                                            val resStr = resp.body?.string().orEmpty()
                                            val obj = JSONObject(resStr)
                                            val candidate = obj.getJSONArray("candidates").getJSONObject(0)
                                            aiResponse = candidate.getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                                        } catch (e: Exception) {
                                            aiResponse = "Knowledge Engine Output: Seek knowledge and ponder upon the signs of Allah."
                                        } finally {
                                            isAiThinking = false
                                        }
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isAiThinking) "Consulting AI..." else "Ask Gemini", color = DarkOledBlack, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().weight(1f).border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("AI Answer:", color = MintSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(aiResponse.ifBlank { "Type a query above and tap Ask Gemini." }, color = TextPureWhite, fontSize = 14.sp, lineHeight = 22.sp)
                        }
                    }
                }
            }
        }
    }
}
