#include "elf_util.h"
#include "../Log.h"
#include "../xdl/xdl.h"

namespace ElfUtil {

void* findSymbol(const std::string& libraryPath, const std::string& symbolName) {
    void* handle = xdl_open(libraryPath.c_str(), XDL_DEFAULT);
    if (!handle) {
        LOGE("ElfUtil: Cannot open library %s", libraryPath.c_str());
        return nullptr;
    }
    void* sym = xdl_sym(handle, symbolName.c_str(), nullptr);
    xdl_close(handle);
    return sym;
}

uintptr_t getLibraryBase(const std::string& libraryName) {
    // Parse /proc/self/maps to find base address
    FILE* f = fopen("/proc/self/maps", "r");
    if (!f) return 0;
    char line[512];
    uintptr_t base = 0;
    while (fgets(line, sizeof(line), f)) {
        if (strstr(line, libraryName.c_str())) {
            sscanf(line, "%x-", &base);
            break;
        }
    }
    fclose(f);
    return base;
}

} // namespace ElfUtil
