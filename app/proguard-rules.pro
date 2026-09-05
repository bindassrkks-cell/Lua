-keepattributes *Annotation*
-keepattributes Signature
-keepclassmembers class * {
    native <methods>;
}
# Keep dynamic loader classes and interfaces for reflection
-keep class com.muslimcommunity.app.dynamic.** { *; }
-keep interface com.muslimcommunity.app.dynamic.** { *; }
-keep class com.muslimcommunity.app.NativeCore { *; }