#pragma once
#include "BaseHook.h"

/**
 * Hooks VMClassLoader to intercept class loading for virtual apps.
 */
class VMClassLoaderHook : public BaseHook {
public:
    static bool install();
};
