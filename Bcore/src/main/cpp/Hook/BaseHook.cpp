#include "BaseHook.h"
#include "../xdl/xdl.h"
#include "../Log.h"

void* BaseHook::findSymbol(const char* library, const char* symbol) {
    void* handle = xdl_open(library, XDL_DEFAULT);
    if (!handle) {
        LOGE("xdl_open failed for %s", library);
        return nullptr;
    }
    void* sym = xdl_sym(handle, symbol, nullptr);
    if (!sym) {
        LOGE("Symbol not found: %s in %s", symbol, library);
    }
    xdl_close(handle);
    return sym;
}
