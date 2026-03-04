#pragma once
#include "BaseHook.h"

/** Hooks Unix-level FS calls (lstat, lstat64, etc.) */
class UnixFileSystemHook : public BaseHook {
public:
    static bool install();
};
