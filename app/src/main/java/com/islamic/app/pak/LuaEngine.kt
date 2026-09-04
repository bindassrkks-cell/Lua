package com.islamic.app.pak

import android.util.Log
import org.luaj.vm2.Globals
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform

object LuaEngine {
    private var globals: Globals? = null

    class ScriptBridge {
        fun log(msg: String) {
            Log.d("LuaEngine", msg)
        }
    }

    fun initScript(scriptPath: String): Boolean {
        return try {
            if (!PakManager.exists(scriptPath)) return false
            val luaCode = PakManager.readText(scriptPath)
            globals = JsePlatform.standardGlobals().apply {
                set("Bridge", CoerceJavaToLua.coerce(ScriptBridge()))
                load(luaCode).call()
            }
            globals?.get("onInit")?.call()
            true
        } catch (e: Exception) {
            Log.e("LuaEngine", "Lua Init Error: ${e.message}")
            false
        }
    }

    fun callNextStep(): Int {
        return try {
            val func = globals?.get("nextStep")
            if (func != null && !func.isnil()) {
                func.call().toint()
            } else 1
        } catch (e: Exception) {
            Log.e("LuaEngine", "Lua execution failed: ${e.message}")
            1
        }
    }
}
