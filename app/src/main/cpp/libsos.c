#include <jni.h>
#include <string.h>
extern const char g_islamic_core_payload[2500000];

// ---------- 15+ Social Feed Posts (Full Arabic & Translation) ----------
JNIEXPORT jstring JNICALL
Java_com_islamic_app_native_NativeEngine_getSocialFeedJson(JNIEnv* env, jobject thiz) {
    const char* json =
    "["
    "{"
    "  \"id\": \"post_1\","
    "  \"title\": \"Ayat‑ul‑Kursi – The Throne Verse (2:255)\","
    "  \"channel\": \"Al‑Quran Karim Official\","
    "  \"views\": \"4.1M views\","
    "  \"time\": \"1 hour ago\","
    "  \"duration\": \"02:15\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ\","
    "  \"translation\": \"Allah! There is no deity except Him, the Ever‑Living, the Self‑Sustaining. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass nothing of His knowledge except what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/255.mp3\""
    "},"
    "{"
    "  \"id\": \"post_2\","
    "  \"title\": \"Surah Al‑Fatiha – The Opening (1:1‑7)\","
    "  \"channel\": \"Quran Recitation Hub\","
    "  \"views\": \"2.8M views\","
    "  \"time\": \"3 hours ago\","
    "  \"duration\": \"01:30\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝ الرَّحْمَٰنِ الرَّحِيمِ ۝ مَالِكِ يَوْمِ الدِّينِ ۝ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ۝ اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ۝ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ\","
    "  \"translation\": \"In the name of Allah, the Entirely Merciful, the Especially Merciful. All praise is due to Allah, Lord of the worlds – the Entirely Merciful, the Especially Merciful – Sovereign of the Day of Recompense. It is You we worship and You we ask for help. Guide us to the straight path – the path of those upon whom You have bestowed favor, not of those who have evoked Your anger or of those who are astray.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3\""
    "},"
    "{"
    "  \"id\": \"post_3\","
    "  \"title\": \"Dua of Yunus (AS) – When in the Whale (21:87)\","
    "  \"channel\": \"Prophets' Duas\","
    "  \"views\": \"1.9M views\","
    "  \"time\": \"5 hours ago\","
    "  \"duration\": \"01:10\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"لَا إِلَٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ\","
    "  \"translation\": \"There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/21.mp3\""
    "},"
    "{"
    "  \"id\": \"post_4\","
    "  \"title\": \"Morning Adhkar – Fortress of the Muslim\","
    "  \"channel\": \"Daily Dhikr\","
    "  \"views\": \"1.5M views\","
    "  \"time\": \"8 hours ago\","
    "  \"duration\": \"03:45\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ. اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ هَذَا الْيَوْمِ وَخَيْرَ مَا فِيهِ، وَأَعُوذُ بِكَ مِنْ شَرِّ هَذَا الْيَوْمِ وَشَرِّ مَا فِيهِ.\","
    "  \"translation\": \"We have entered the morning and the dominion belongs to Allah, and all praise is for Allah. There is no god but Allah alone, He has no partner; to Him belongs the kingdom and to Him is all praise, and He is over all things competent. O Allah, I ask You for the good of this day and the good of what is in it, and I seek refuge in You from the evil of this day and the evil of what is in it.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/2.mp3\""
    "},"
    "{"
    "  \"id\": \"post_5\","
    "  \"title\": \"Evening Adhkar – Seeking Protection\","
    "  \"channel\": \"Daily Dhikr\","
    "  \"views\": \"1.2M views\","
    "  \"time\": \"12 hours ago\","
    "  \"duration\": \"04:10\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ. اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا فِيهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ هَذِهِ اللَّيْلَةِ وَشَرِّ مَا فِيهَا.\","
    "  \"translation\": \"We have entered the evening and the dominion belongs to Allah, and all praise is for Allah. There is no god but Allah alone, He has no partner; to Him belongs the kingdom and to Him is all praise, and He is over all things competent. O Allah, I ask You for the good of this night and the good of what is in it, and I seek refuge in You from the evil of this night and the evil of what is in it.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/3.mp3\""
    "},"
    "{"
    "  \"id\": \"post_6\","
    "  \"title\": \"Dua for Sleeping – With Dhikr\","
    "  \"channel\": \"Night Prayers\","
    "  \"views\": \"980K views\","
    "  \"time\": \"1 day ago\","
    "  \"duration\": \"02:20\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، إِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ\","
    "  \"translation\": \"In Your name, O Lord, I lay down my side, and by You I rise. If You take my soul, have mercy on it; if You release it, protect it as You protect Your righteous servants.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/4.mp3\""
    "},"
    "{"
    "  \"id\": \"post_7\","
    "  \"title\": \"Dua for Waking Up – Gratitude\","
    "  \"channel\": \"Morning Blessings\","
    "  \"views\": \"870K views\","
    "  \"time\": \"1 day ago\","
    "  \"duration\": \"01:45\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا، وَإِلَيْهِ النُّشُورُ\","
    "  \"translation\": \"All praise is for Allah who gave us life after He caused us to die, and to Him is the resurrection.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/5.mp3\""
    "},"
    "{"
    "  \"id\": \"post_8\","
    "  \"title\": \"Dua Before Eating – Barakah in Food\","
    "  \"channel\": \"Sunnah Supplications\","
    "  \"views\": \"760K views\","
    "  \"time\": \"2 days ago\","
    "  \"duration\": \"01:00\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"بِسْمِ اللَّهِ، وَعَلَى بَرَكَةِ اللَّهِ\","
    "  \"translation\": \"In the name of Allah and with the blessings of Allah.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/6.mp3\""
    "},"
    "{"
    "  \"id\": \"post_9\","
    "  \"title\": \"Dua After Eating – Praise and Gratitude\","
    "  \"channel\": \"Sunnah Supplications\","
    "  \"views\": \"690K views\","
    "  \"time\": \"2 days ago\","
    "  \"duration\": \"00:55\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ\","
    "  \"translation\": \"All praise is for Allah who fed me this and provided it to me without any might or power on my part.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/7.mp3\""
    "},"
    "{"
    "  \"id\": \"post_10\","
    "  \"title\": \"Dua for Travel – Safe Journey\","
    "  \"channel\": \"Traveler's Dhikr\","
    "  \"views\": \"620K views\","
    "  \"time\": \"3 days ago\","
    "  \"duration\": \"02:30\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ، وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ\","
    "  \"translation\": \"Glory be to Him who has subjected this (vehicle) to us while we were incapable of it, and indeed we are to return to our Lord.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/8.mp3\""
    "},"
    "{"
    "  \"id\": \"post_11\","
    "  \"title\": \"Dua for Protection from Harm\","
    "  \"channel\": \"Protective Adhkar\","
    "  \"views\": \"580K views\","
    "  \"time\": \"4 days ago\","
    "  \"duration\": \"01:50\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ، وَهُوَ السَّمِيعُ الْعَلِيمُ\","
    "  \"translation\": \"In the name of Allah, with whose name nothing in the earth or the heaven can cause harm, and He is the All‑Hearing, the All‑Knowing.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/9.mp3\""
    "},"
    "{"
    "  \"id\": \"post_12\","
    "  \"title\": \"Dua for Parents – Mercy and Forgiveness (17:24)\","
    "  \"channel\": \"Family Duas\","
    "  \"views\": \"540K views\","
    "  \"time\": \"5 days ago\","
    "  \"duration\": \"01:30\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا\","
    "  \"translation\": \"My Lord, have mercy upon them as they brought me up when I was small.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/17.mp3\""
    "},"
    "{"
    "  \"id\": \"post_13\","
    "  \"title\": \"Dua for Entering the Mosque\","
    "  \"channel\": \"Etiquettes of the Masjid\","
    "  \"views\": \"510K views\","
    "  \"time\": \"6 days ago\","
    "  \"duration\": \"01:10\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ\","
    "  \"translation\": \"O Allah, open for me the doors of Your mercy.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/10.mp3\""
    "},"
    "{"
    "  \"id\": \"post_14\","
    "  \"title\": \"Dua for Leaving the Mosque\","
    "  \"channel\": \"Etiquettes of the Masjid\","
    "  \"views\": \"480K views\","
    "  \"time\": \"6 days ago\","
    "  \"duration\": \"01:05\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ\","
    "  \"translation\": \"O Allah, I ask of You from Your bounty.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/11.mp3\""
    "},"
    "{"
    "  \"id\": \"post_15\","
    "  \"title\": \"SubhanAllah wa BiHamdihi – The Heaviest Deed\","
    "  \"channel\": \"Dhikr & Tasbih\","
    "  \"views\": \"450K views\","
    "  \"time\": \"1 week ago\","
    "  \"duration\": \"02:00\","
    "  \"imageUrl\": \"https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800&auto=format&fit=crop&q=60\","
    "  \"arabic\": \"سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ\","
    "  \"translation\": \"Glory be to Allah and praise be to Him, glory be to Allah the Supreme.\","
    "  \"audioUrl\": \"https://cdn.islamic.network/quran/audio/128/ar.alafasy/12.mp3\""
    "}"
    "]";
    return (*env)->NewStringUTF(env, json);
}

// ---------- Salah Guide (9 Steps) ----------
JNIEXPORT jstring JNICALL
Java_com_islamic_app_native_NativeEngine_getSalahGuideJson(JNIEnv* env, jobject thiz) {
    const char* json =
    "["
    "{\"step\":1,\"title\":\"Takbiratul Ihram\",\"arabic\":\"اللَّهُ أَكْبَرُ\",\"meaning\":\"Allah is the Greatest\",\"imageUrl\":\"https://images.unsplash.com/photo-1542838132-92c53300491e?w=800\"},"
    "{\"step\":2,\"title\":\"Qiyam (Al-Fatiha)\",\"arabic\":\"الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ...\",\"meaning\":\"Recite Al-Fatiha & a portion\",\"imageUrl\":\"https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=800\"},"
    "{\"step\":3,\"title\":\"Ruku\",\"arabic\":\"سُبْحَانَ رَبِّيَ الْعَظِيمِ\",\"meaning\":\"Glory be to my Lord the Supreme (x3)\",\"imageUrl\":\"https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800\"},"
    "{\"step\":4,\"title\":\"Rising from Ruku\",\"arabic\":\"سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ\",\"meaning\":\"Allah hears those who praise Him\",\"imageUrl\":\"https://images.unsplash.com/photo-1542838132-92c53300491e?w=800\"},"
    "{\"step\":5,\"title\":\"Sujud\",\"arabic\":\"سُبْحَانَ رَبِّيَ الأَعْلَى\",\"meaning\":\"Glory be to my Lord Most High (x3)\",\"imageUrl\":\"https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800\"},"
    "{\"step\":6,\"title\":\"Sitting between Sujud\",\"arabic\":\"رَبِّ اغْفِرْ لِي وَارْحَمْنِي\",\"meaning\":\"My Lord, forgive me and have mercy\",\"imageUrl\":\"https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800\"},"
    "{\"step\":7,\"title\":\"Second Sujud\",\"arabic\":\"سُبْحَانَ رَبِّيَ الأَعْلَى\",\"meaning\":\"Glory be to my Lord Most High (x3)\",\"imageUrl\":\"https://images.unsplash.com/photo-1542838132-92c53300491e?w=800\"},"
    "{\"step\":8,\"title\":\"Tashahhud\",\"arabic\":\"التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ ...\",\"meaning\":\"All greetings, prayers and pure words are for Allah...\",\"imageUrl\":\"https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=800\"},"
    "{\"step\":9,\"title\":\"Taslim\",\"arabic\":\"السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ\",\"meaning\":\"Peace be upon you (right then left)\",\"imageUrl\":\"https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800\"}"
    "]";
    return (*env)->NewStringUTF(env, json);
}

// ---------- Quran Surah List (All 114) – abbreviated for brevity, include all ----------
JNIEXPORT jstring JNICALL
Java_com_islamic_app_native_NativeEngine_getQuranSurahListJson(JNIEnv* env, jobject thiz) {
    // For production, include all 114. Here's a snippet – you can expand.
    const char* json =
    "["
    "{\"number\":1,\"name\":\"الفاتحة\",\"englishName\":\"Al-Fatiha\",\"revelationType\":\"Meccan\",\"ayahCount\":7},"
    "{\"number\":2,\"name\":\"البقرة\",\"englishName\":\"Al-Baqarah\",\"revelationType\":\"Medinan\",\"ayahCount\":286},"
    "{\"number\":3,\"name\":\"آل عمران\",\"englishName\":\"Aal-Imran\",\"revelationType\":\"Medinan\",\"ayahCount\":200},"
    "{\"number\":4,\"name\":\"النساء\",\"englishName\":\"An-Nisa\",\"revelationType\":\"Medinan\",\"ayahCount\":176},"
    "{\"number\":5,\"name\":\"المائدة\",\"englishName\":\"Al-Ma'idah\",\"revelationType\":\"Medinan\",\"ayahCount\":120},"
    "{\"number\":6,\"name\":\"الأنعام\",\"englishName\":\"Al-An'am\",\"revelationType\":\"Meccan\",\"ayahCount\":165},"
    "{\"number\":7,\"name\":\"الأعراف\",\"englishName\":\"Al-A'raf\",\"revelationType\":\"Meccan\",\"ayahCount\":206},"
    "{\"number\":8,\"name\":\"الأنفال\",\"englishName\":\"Al-Anfal\",\"revelationType\":\"Medinan\",\"ayahCount\":75},"
    "{\"number\":9,\"name\":\"التوبة\",\"englishName\":\"At-Tawbah\",\"revelationType\":\"Medinan\",\"ayahCount\":129},"
    "{\"number\":10,\"name\":\"يونس\",\"englishName\":\"Yunus\",\"revelationType\":\"Meccan\",\"ayahCount\":109}"
    // ... add remaining 104 surahs similarly.
    "]";
    return (*env)->NewStringUTF(env, json);
}

// ---------- Ayah Detail ----------
JNIEXPORT jstring JNICALL
Java_com_islamic_app_native_NativeEngine_getAyahDetailJson(JNIEnv* env, jobject thiz, jint surah, jint ayah) {
    char buffer[2048] = "{";
    char temp[256];
    if (surah == 1 && ayah == 1) {
        strcat(buffer, "\"arabic\":\"بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\",");
        strcat(buffer, "\"translation\":\"In the name of Allah, the Entirely Merciful, the Especially Merciful.\"");
    } else if (surah == 2 && ayah == 255) {
        strcat(buffer, "\"arabic\":\"اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ... (Ayat-ul-Kursi)\",");
        strcat(buffer, "\"translation\":\"Allah! There is no deity except Him, the Ever-Living, the Self-Sustaining...\"");
    } else if (surah == 21 && ayah == 87) {
        strcat(buffer, "\"arabic\":\"لَا إِلَٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ\",");
        strcat(buffer, "\"translation\":\"There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.\"");
    } else {
        strcat(buffer, "\"arabic\":\"سورة \");");
        sprintf(temp, "%d آية %d\",", surah, ayah);
        strcat(buffer, temp);
        strcat(buffer, "\"translation\":\"Please use a Quran API for full text.\"");
    }
    strcat(buffer, "}");
    return (*env)->NewStringUTF(env, buffer);
}

// ---------- Gemini Endpoint ----------
JNIEXPORT jstring JNICALL
Java_com_islamic_app_native_NativeEngine_getGeminiEndpoint(JNIEnv* env, jobject thiz) {
    return (*env)->NewStringUTF(env, "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent");
}

// ---------- Payload Size ----------
JNIEXPORT jlong JNICALL
Java_com_islamic_app_native_NativeEngine_getCorePayloadSize(JNIEnv* env, jobject thiz) {
    return (jlong)sizeof(g_islamic_core_payload);
}
