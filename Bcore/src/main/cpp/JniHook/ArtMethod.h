#pragma once
#include <jni.h>
#include <cstdint>

/**
 * Abstracted ArtMethod structure for JNI method hooking.
 * Offsets vary per Android version — must be resolved at runtime.
 */
struct ArtMethod {
    uint32_t declaring_class_;
    uint32_t access_flags_;
    uint32_t dex_code_item_offset_;
    uint32_t dex_method_index_;
    uint16_t method_index_;
    uint16_t hotness_count_;
    uint16_t imt_index_;
    void* data_;
    void* entry_point_from_quick_compiled_code_;

    static ArtMethod* FromReflectedMethod(JNIEnv* env, jobject method);
    void* GetEntryPoint() const;
    void SetEntryPoint(void* ep);
};
