#pragma once
#include <jni.h>
#include "ArtMethod.h"

/**
 * Hooks JNI methods using ArtMethod entry-point replacement.
 */
class JniHook {
public:
    static bool hookMethod(JNIEnv* env, jobject method, void* replacement, void** original);
    static bool unhookMethod(JNIEnv* env, jobject method);
};
