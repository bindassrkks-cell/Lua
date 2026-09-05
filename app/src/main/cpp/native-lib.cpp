#include <jni.h>
#include <string>
#include <dlfcn.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_muslimcommunity_app_NativeCore_getEngineInfo(JNIEnv* env, jobject) {
    return env->NewStringUTF("C++20 NDK Engine • Dynamic Bridge Active");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_muslimcommunity_app_NativeCore_loadNativeModule(JNIEnv* env, jobject, jstring path) {
    const char* nativePath = env->GetStringUTFChars(path, nullptr);
    void* handle = dlopen(nativePath, RTLD_NOW);
    env->ReleaseStringUTFChars(path, nativePath);
    return handle != nullptr ? JNI_TRUE : JNI_FALSE;
}