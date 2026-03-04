#include "BoxCore.h"
#include "IO.h"
#include "Log.h"
#include "hidden_api.h"
#include "Hook/FileSystemHook.h"
#include "Hook/BinderHook.h"
#include "Hook/DexFileHook.h"
#include "Hook/RuntimeHook.h"
#include "Hook/VMClassLoaderHook.h"
#include "JniHook/JniHook.h"
#include "Utils/AntiDetection.h"

#include <string>
#include <unistd.h>
#include <sys/types.h>

// Global engine state
static bool gHooksInstalled = false;
static std::string gPackageName;
static std::string gDataPath;
static int gSdkInt = 0;

namespace BoxCore {

int init(JNIEnv* env, const std::string& selfPath,
         const std::string& pkgName,
         const std::string& dataPath,
         int sdkInt) {
    LOGI("BoxCore::init pkg=%s sdk=%d", pkgName.c_str(), sdkInt);
    gPackageName = pkgName;
    gDataPath    = dataPath;
    gSdkInt      = sdkInt;

    IO::init(pkgName, dataPath);

    if (!installIOHooks()) {
        LOGE("Failed to install IO hooks");
        return -1;
    }
    if (!installHooks()) {
        LOGE("Failed to install general hooks");
        return -2;
    }

    AntiDetection::init();
    gHooksInstalled = true;
    LOGI("BoxCore initialized successfully");
    return 0;
}

bool installIOHooks() {
    bool ok = true;
    ok &= FileSystemHook::install();
    LOGI("FileSystemHook %s", ok ? "OK" : "FAILED");
    return ok;
}

bool installHooks() {
    bool ok = true;
    ok &= BinderHook::install();
    ok &= DexFileHook::install();
    ok &= RuntimeHook::install();
    ok &= VMClassLoaderHook::install();
    return ok;
}

bool setPackageName(JNIEnv* env, const std::string& pkgName) {
    gPackageName = pkgName;
    return true;
}

bool addIORedirect(const std::string& source, const std::string& target) {
    IO::addRedirect(source, target);
    return true;
}

bool removeIORedirect(const std::string& source) {
    IO::removeRedirect(source);
    return true;
}

} // namespace BoxCore

// ── JNI bridge ────────────────────────────────────────────────────────────────

static std::string jstring2str(JNIEnv* env, jstring js) {
    if (!js) return "";
    const char* chars = env->GetStringUTFChars(js, nullptr);
    std::string result(chars);
    env->ReleaseStringUTFChars(js, chars);
    return result;
}

extern "C" {

JNIEXPORT jint JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_nativeInit(
        JNIEnv* env, jclass,
        jstring selfPath, jstring packageName,
        jstring dataPath, jint sdkInt) {
    return (jint)BoxCore::init(env,
            jstring2str(env, selfPath),
            jstring2str(env, packageName),
            jstring2str(env, dataPath),
            (int)sdkInt);
}

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_installIOHooks(
        JNIEnv* env, jclass,
        jstring sourcePath, jstring targetPath) {
    std::string src = jstring2str(env, sourcePath);
    std::string tgt = jstring2str(env, targetPath);
    IO::addRedirect(src, tgt);
    return (jboolean)BoxCore::installIOHooks();
}

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_installBinderHooks(
        JNIEnv* env, jclass) {
    return (jboolean)BinderHook::install();
}

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_setPackageName(
        JNIEnv* env, jclass, jstring packageName) {
    return (jboolean)BoxCore::setPackageName(env, jstring2str(env, packageName));
}

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_addIORedirect(
        JNIEnv* env, jclass,
        jstring sourcePath, jstring targetPath) {
    IO::addRedirect(jstring2str(env, sourcePath), jstring2str(env, targetPath));
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_removeIORedirect(
        JNIEnv* env, jclass, jstring sourcePath) {
    IO::removeRedirect(jstring2str(env, sourcePath));
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_getRealPath(
        JNIEnv* env, jclass, jstring virtualPath) {
    std::string vp = jstring2str(env, virtualPath);
    std::string rp = IO::getRealPath(vp);
    return env->NewStringUTF(rp.c_str());
}

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_areHooksInstalled(
        JNIEnv* env, jclass) {
    return (jboolean)gHooksInstalled;
}

} // extern "C"
