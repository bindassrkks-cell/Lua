package com.islamic.app.pak

import android.content.Context
import android.util.Log
import org.luaj.vm2.Globals
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform

object LuaEngine {
    private var globals: Globals? = null
    private var appContext: Context? = null

    class ScriptBridge {
        fun log(msg: String) {
            Log.d("LuaEngine", msg)
        }
        fun playAudio(audioFile: String) {
            appContext?.let {
                PakAudioPlayer.play(it, audioFile)
            }
        }
    }

    fun init(context: Context) {
        this.appContext = context
        try {
            if (PakManager.exists("scripts/zikr.lua")) {
                val luaCode = PakManager.readText("scripts/zikr.lua")
                globals = JsePlatform.standardGlobals().apply {
                    set("Bridge", CoerceJavaToLua.coerce(ScriptBridge()))
                    load(luaCode).call()
                }
                globals?.get("onInit")?.call()
            }
        } catch (e: Exception) {
            Log.e("LuaEngine", "Zikr Lua error: ${e.message}")
        }
    }

    fun detectAndPlay(word: String): String {
        return try {
            val func = globals?.get("detectWord")
            if (func != null && !func.isnil()) {
                func.call(org.luaj.vm2.LuaValue.valueOf(word)).tojstring()
            } else "Script uninitialized"
        } catch (e: Exception) {
            e.message ?: "Error"
        }
    }
}
