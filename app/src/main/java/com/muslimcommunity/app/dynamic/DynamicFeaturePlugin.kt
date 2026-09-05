package com.muslimcommunity.app.dynamic

import android.content.Context

interface DynamicFeaturePlugin {
    val moduleId: String
    val moduleName: String
    val version: String
    fun executeFeature(context: Context, action: String, payload: Map<String, Any>): Map<String, Any>
}