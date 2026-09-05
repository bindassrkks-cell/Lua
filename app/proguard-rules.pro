-keepattributes *Annotation*
-keepclassmembers class * {
    native <methods>;
}
-keep class com.muslimcommunity.app.NativeCore { *; }
-keep class com.muslimcommunity.app.DynamicSyncManager { *; }