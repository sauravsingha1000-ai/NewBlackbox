#pragma once
#include "BaseHook.h"

/**
 * Hooks Android Binder IPC calls to intercept service calls.
 */
class BinderHook : public BaseHook {
public:
    static bool install();
private:
    static int hook_ioctl(int fd, unsigned long request, ...);
};
