#pragma once
#include <jni.h>

/**
 * Access Android hidden APIs that are restricted by the greylist/blacklist
 * mechanism introduced in Android 9+.
 *
 * Strategy: use a crafted ClassLoader + Method.invoke bypass, or direct
 * ART symbol access via xdl.
 */
namespace HiddenApi {
    /**
     * Bypass hidden API restrictions for this process.
     * Must be called early in process startup.
     */
    void init(JNIEnv* env);

    /**
     * Call a hidden Java method by name.
     */
    jobject callMethod(JNIEnv* env, const char* className,
                       const char* methodName, const char* signature,
                       jobject obj, ...);
}
