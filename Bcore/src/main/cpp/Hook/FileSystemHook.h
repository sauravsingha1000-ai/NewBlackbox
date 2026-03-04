#pragma once
#include "BaseHook.h"
#include <sys/stat.h>
#include <dirent.h>

/**
 * Hooks file system calls to implement virtual path redirection.
 */
class FileSystemHook : public BaseHook {
public:
    static bool install();
private:
    static int hook_open(const char* path, int flags, ...);
    static int hook_stat(const char* path, struct stat* buf);
    static int hook_access(const char* path, int mode);
    static DIR* hook_opendir(const char* path);
    static int hook_rename(const char* oldpath, const char* newpath);
    static int hook_unlink(const char* pathname);
    static int hook_mkdir(const char* pathname, mode_t mode);
    static int hook_rmdir(const char* pathname);
};
