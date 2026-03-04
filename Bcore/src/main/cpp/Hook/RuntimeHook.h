#pragma once
#include "BaseHook.h"

/**
 * Hooks ART runtime methods (class loading, method invocation).
 */
class RuntimeHook : public BaseHook {
public:
    static bool install();
};
