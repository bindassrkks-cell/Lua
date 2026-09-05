#include <jni.h>
#include <string>
#include <dlfcn.h>
#include <android/log.h>

#define LOG_TAG "NativeDynamicLoader"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_muslimcommunity_app_NativeCore_getEngineInfo(JNIEnv* env, jobject) {
    return env->NewStringUTF("Core Engine 2.0 (Dynamic SO Loader Ready)");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_muslimcommunity_app_NativeCore_loadDynamicNativeLib(JNIEnv* env, jobject, jstring libPath) {
    const char* path = env->GetStringUTFChars(libPath, nullptr);
    void* handle = dlopen(path, RTLD_NOW);
    env->ReleaseStringUTFChars(libPath, path);
    if (!handle) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}