local zikrItems = {
    { id = "zikr_1", word = "SubhanAllah", count = 33, audio = "audio/takbir.ogg", desc = "Glory be to Allah" },
    { id = "zikr_2", word = "Alhamdulillah", count = 33, audio = "audio/takbir.ogg", desc = "All praise is for Allah" },
    { id = "zikr_3", word = "Allahu Akbar", count = 34, audio = "audio/takbir.ogg", desc = "Allah is the Greatest" },
    { id = "ayah_1", word = "Ayat-ul-Kursi", count = 1, audio = "audio/takbir.ogg", desc = "Surah Al-Baqarah (2:255)" },
    { id = "azan_1", word = "Adhan Al-Haram", count = 1, audio = "audio/azan.ogg", desc = "Makkah Mukarramah Azan" }
}

function onInit()
    Bridge:log("Lua Zikr and Ayah Controller Loaded successfully")
end

function getZikrData(index)
    local item = zikrItems[index]
    if item then
        return item.word, item.count, item.audio, item.desc
    end
    return "", 0, "", ""
end

function getTotalItems()
    return #zikrItems
end

function detectWord(inputWord)
    for i, v in ipairs(zikrItems) do
        if string.lower(v.word):find(string.lower(inputWord)) then
            Bridge:log("Word detected in Lua: " .. v.word)
            Bridge:playAudio(v.audio)
            return v.word
        end
    end
    return "Not Found"
end
