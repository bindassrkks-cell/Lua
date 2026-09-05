#include <jni.h>
#include <string.h>

extern const char g_islamic_core_payload[2500000];

JNIEXPORT jstring JNICALL
Java_com_islamic_app_native_NativeEngine_getSocialFeedJson(JNIEnv* env, jobject thiz) {
    const char* json = "["
    "{"
    "  \"id\": \"post_1\","
    "  \"title\": \"Surah Al-Baqarah (2:255) - Ayat-ul-Kursi in Full HD\","
    "  \"channel\": \"Al-Quran Karim Official\","
    "  \"views\": \"3.2M views\","
    "  \"time\": \"2 hours ago\","
    "  \"duration\": \"04:22\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ\","
    "  \"translation\": \"Allah! There is no deity except Him, the Ever-Living, the Sustainer of all existence.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3\""
    "},"
    "{"
    "  \"id\": \"post_2\","
    "  \"title\": \"Makkah Al-Mukarramah Live Fajr & Night Prayers\","
    "  \"channel\": \"Makkah Live Stream\","
    "  \"views\": \"1.1M views\","
    "  \"time\": \"5 hours ago\","
    "  \"duration\": \"03:45\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"حَيَّ عَلَى الصَّلَاةِ - حَيَّ عَلَى الْفَلَاحِ\","
    "  \"translation\": \"Come to prayer, come to success. Prayer is better than sleep.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3\""
    "},"
    "{"
    "  \"id\": \"post_3\","
    "  \"title\": \"Daily Authentic Azkar for Evening & Night Barakah\","
    "  \"channel\": \"Darussalam Official\","
    "  \"views\": \"650K views\","
    "  \"time\": \"1 day ago\","
    "  \"duration\": \"06:12\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ\","
    "  \"translation\": \"Glory be to Allah and His is the praise, glory be to Allah the Magnificent.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/2.mp3\""
    "}"
    "]";
    return (*env)->NewStringUTF(env, json);
}

JNIEXPORT jstring JNICALL
Java_com_islamic_app_native_NativeEngine_getSalahGuideJson(JNIEnv* env, jobject thiz) {
    const char* json = "["
    "{\"step\": 1, \"title\": \"Takbiratul Ihram\", \"arabic\": \"اللَّهُ أَكْبَرُ\", \"meaning\": \"Allah is the Greatest\", \"imageUrl\": \"https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&auto=format&fit=crop&q=60\"},"
    "{\"step\": 2, \"title\": \"Ruku (Bowing)\", \"arabic\": \"سُبْحَانَ رَبِّيَ الْعَظِيمِ\", \"meaning\": \"Glory be to my Lord the Supreme (x3)\", \"imageUrl\": \"https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=800&auto=format&fit=crop&q=60\"},"
    "{\"step\": 3, \"title\": \"Sujud (Prostration)\", \"arabic\": \"سُبْحَانَ رَبِّيَ الأَعْلَى\", \"meaning\": \"Glory be to my Lord Most High (x3)\", \"imageUrl\": \"https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800&auto=format&fit=crop&q=60\"}"
    "]";
    return (*env)->NewStringUTF(env, json);
}

JNIEXPORT jstring JNICALL
Java_com_islamic_app_native_NativeEngine_getGeminiEndpoint(JNIEnv* env, jobject thiz) {
    return (*env)->NewStringUTF(env, "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent");
}

JNIEXPORT jlong JNICALL
Java_com_islamic_app_native_NativeEngine_getCorePayloadSize(JNIEnv* env, jobject thiz) {
    return (jlong)sizeof(g_islamic_core_payload);
}
