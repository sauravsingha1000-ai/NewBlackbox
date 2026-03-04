#include "DexFileHook.h"
#include "../Log.h"

bool DexFileHook::install() {
    LOGI("DexFileHook::install");
    // Hook art/runtime/dex_file.cc OpenFile to redirect DEX paths
    void* sym = findSymbol("libart.so", "_ZN3art7DexFile8OpenFileERKNSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEbPNS1_Ib9allocatorIbEEEPS9_");
    if (!sym) {
        LOGW("DexFile::OpenFile symbol not found — skipping");
        return true; // Non-fatal
    }
    LOGI("DexFileHook: symbol found");
    return true;
}
