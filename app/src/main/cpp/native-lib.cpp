#include <jni.h>
#include <string>
#include <dlfcn.h>
#include <cmath>

extern "C" JNIEXPORT jstring JNICALL
Java_com_muslimcommunity_app_NativeCore_getEngineInfo(JNIEnv* env, jobject) {
    return env->NewStringUTF("iOS UX Engine • VFX Native Shaders Active");
}

extern "C" JNIEXPORT jfloat JNICALL
Java_com_muslimcommunity_app_NativeCore_getVfxGlowIntensity(JNIEnv* env, jobject, jlong timeMillis) {
    float radians = (timeMillis % 3000) * (2.0f * 3.14159265f / 3000.0f);
    return 0.75f + 0.25f * sinf(radians);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_muslimcommunity_app_NativeCore_loadDynamicNativeLib(JNIEnv* env, jobject, jstring path) {
    const char* nativePath = env->GetStringUTFChars(path, nullptr);
    void* handle = dlopen(nativePath, RTLD_NOW);
    env->ReleaseStringUTFChars(path, nativePath);
    return handle != nullptr ? JNI_TRUE : JNI_FALSE;
}