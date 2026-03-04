#pragma once
#include "BaseHook.h"

/**
 * Hooks DexFile loading to redirect virtual app DEX loading.
 */
class DexFileHook : public BaseHook {
public:
    static bool install();
};
