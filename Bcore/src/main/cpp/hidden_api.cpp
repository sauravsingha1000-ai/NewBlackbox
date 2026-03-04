#include "hidden_api.h"
#include "Log.h"

namespace HiddenApi {

void init(JNIEnv* env) {
    // Strategy 1: use unofficial exemption bypass via VMRuntime.setHiddenApiExemptions
    jclass vmRuntime = env->FindClass("dalvik/system/VMRuntime");
    if (!vmRuntime) {
        LOGW("VMRuntime class not found");
        return;
    }
    jmethodID getRuntime = env->GetStaticMethodID(
            vmRuntime, "getRuntime", "()Ldalvik/system/VMRuntime;");
    if (!getRuntime) { LOGW("getRuntime not found"); return; }

    jmethodID setExemptions = env->GetMethodID(
            vmRuntime, "setHiddenApiExemptions", "([Ljava/lang/String;)V");
    if (!setExemptions) { LOGW("setHiddenApiExemptions not found"); return; }

    jobject runtime = env->CallStaticObjectMethod(vmRuntime, getRuntime);

    // Exempt everything (prefix "L")
    jobjectArray arr = env->NewObjectArray(1, env->FindClass("java/lang/String"), nullptr);
    env->SetObjectArrayElement(arr, 0, env->NewStringUTF("L"));
    env->CallVoidMethod(runtime, setExemptions, arr);

    LOGI("HiddenApi: exemptions applied");
}

jobject callMethod(JNIEnv* env, const char* className, const char* methodName,
                   const char* signature, jobject obj, ...) {
    jclass clz = env->FindClass(className);
    if (!clz) return nullptr;

    jmethodID mid = obj
        ? env->GetMethodID(clz, methodName, signature)
        : env->GetStaticMethodID(clz, methodName, signature);
    if (!mid) return nullptr;

    // Simplified – varargs dispatch omitted for brevity
    return nullptr;
}

} // namespace HiddenApi
