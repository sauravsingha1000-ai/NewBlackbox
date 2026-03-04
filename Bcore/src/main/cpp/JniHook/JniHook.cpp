#include "JniHook.h"
#include "../Log.h"

bool JniHook::hookMethod(JNIEnv* env, jobject method, void* replacement, void** original) {
    ArtMethod* am = ArtMethod::FromReflectedMethod(env, method);
    if (!am) {
        LOGE("JniHook: failed to get ArtMethod");
        return false;
    }
    if (original) *original = am->GetEntryPoint();
    am->SetEntryPoint(replacement);
    LOGD("JniHook: method hooked");
    return true;
}

bool JniHook::unhookMethod(JNIEnv* env, jobject method) {
    LOGD("JniHook: unhook");
    return true;
}

// ArtMethod implementations
ArtMethod* ArtMethod::FromReflectedMethod(JNIEnv* env, jobject method) {
    jlong artMethodPtr = env->CallLongMethod(method,
            env->GetMethodID(env->GetObjectClass(method), "getArtMethod", "()J"));
    return reinterpret_cast<ArtMethod*>(artMethodPtr);
}

void* ArtMethod::GetEntryPoint() const {
    return entry_point_from_quick_compiled_code_;
}

void ArtMethod::SetEntryPoint(void* ep) {
    entry_point_from_quick_compiled_code_ = ep;
}
