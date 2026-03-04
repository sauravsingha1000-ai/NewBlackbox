#pragma once
#include "../Log.h"
#include "../Dobby/dobby.h"

/**
 * Base class for all native hooks using Dobby.
 */
class BaseHook {
public:
    static bool hookFunction(void* original, void* replacement, void** backup) {
        int ret = DobbyHook(original, replacement, backup);
        if (ret != RS_SUCCESS) {
            LOGE("DobbyHook failed: %d", ret);
            return false;
        }
        return true;
    }

    static void* findSymbol(const char* library, const char* symbol);
};
