#pragma once
#include <jni.h>
#include <string>

/**
 * Main entry points for the BoxCore native engine.
 */
namespace BoxCore {

/**
 * Initialize the engine for a virtual process.
 *
 * @param selfPath   Path to the host APK
 * @param pkgName    Virtual package name
 * @param dataPath   Virtual data directory
 * @param sdkInt     Host Android SDK version
 * @return 0 on success, negative on failure
 */
int init(JNIEnv* env, const std::string& selfPath,
         const std::string& pkgName,
         const std::string& dataPath,
         int sdkInt);

/**
 * Install all IO hooks (file/path redirection).
 */
bool installIOHooks();

/**
 * Install all Binder/JNI hooks.
 */
bool installHooks();

/**
 * Set the virtual package name for this process.
 */
bool setPackageName(JNIEnv* env, const std::string& pkgName);

/**
 * Add an IO redirect: source -> target.
 */
bool addIORedirect(const std::string& source, const std::string& target);

/**
 * Remove an IO redirect.
 */
bool removeIORedirect(const std::string& source);

} // namespace BoxCore

// JNI exports
extern "C" {
JNIEXPORT jint JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_nativeInit(
    JNIEnv* env, jclass clz,
    jstring selfPath, jstring packageName,
    jstring dataPath, jint sdkInt);

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_installIOHooks(
    JNIEnv* env, jclass clz,
    jstring sourcePath, jstring targetPath);

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_installBinderHooks(
    JNIEnv* env, jclass clz);

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_setPackageName(
    JNIEnv* env, jclass clz, jstring packageName);

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_addIORedirect(
    JNIEnv* env, jclass clz,
    jstring sourcePath, jstring targetPath);

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_removeIORedirect(
    JNIEnv* env, jclass clz, jstring sourcePath);

JNIEXPORT jstring JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_getRealPath(
    JNIEnv* env, jclass clz, jstring virtualPath);

JNIEXPORT jboolean JNICALL
Java_top_niunaijun_blackbox_core_NativeCore_areHooksInstalled(
    JNIEnv* env, jclass clz);
}
