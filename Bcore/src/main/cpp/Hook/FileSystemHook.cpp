#include "FileSystemHook.h"
#include "../IO.h"
#include "../Log.h"

#include <fcntl.h>
#include <unistd.h>
#include <dirent.h>
#include <dlfcn.h>
#include <sys/stat.h>

#include <cstdarg>
#include <cerrno>

// Original function pointers
static int (*orig_open)(const char*, int, ...) = nullptr;
static int (*orig_stat)(const char*, struct stat*) = nullptr;
static int (*orig_access)(const char*, int) = nullptr;
static DIR* (*orig_opendir)(const char*) = nullptr;
static int (*orig_rename)(const char*, const char*) = nullptr;
static int (*orig_unlink)(const char*) = nullptr;
static int (*orig_mkdir)(const char*, mode_t) = nullptr;
static int (*orig_rmdir)(const char*) = nullptr;

int FileSystemHook::hook_open(const char* path, int flags, ...) {
    std::string redirected = IO::redirect(path ? path : "");

    mode_t mode = 0;
    if (flags & O_CREAT) {
        va_list args;
        va_start(args, flags);
        mode = va_arg(args, mode_t);
        va_end(args);
    }

    return orig_open ? orig_open(redirected.c_str(), flags, mode) : -1;
}

int FileSystemHook::hook_stat(const char* path, struct stat* buf) {
    std::string redirected = IO::redirect(path ? path : "");
    return orig_stat ? orig_stat(redirected.c_str(), buf) : -1;
}

int FileSystemHook::hook_access(const char* path, int mode) {
    std::string redirected = IO::redirect(path ? path : "");
    return orig_access ? orig_access(redirected.c_str(), mode) : -1;
}

DIR* FileSystemHook::hook_opendir(const char* path) {
    std::string redirected = IO::redirect(path ? path : "");
    return orig_opendir ? orig_opendir(redirected.c_str()) : nullptr;
}

int FileSystemHook::hook_rename(const char* oldpath, const char* newpath) {
    std::string redOld = IO::redirect(oldpath ? oldpath : "");
    std::string redNew = IO::redirect(newpath ? newpath : "");
    return orig_rename ? orig_rename(redOld.c_str(), redNew.c_str()) : -1;
}

int FileSystemHook::hook_unlink(const char* pathname) {
    std::string redirected = IO::redirect(pathname ? pathname : "");
    return orig_unlink ? orig_unlink(redirected.c_str()) : -1;
}

int FileSystemHook::hook_mkdir(const char* pathname, mode_t mode) {
    std::string redirected = IO::redirect(pathname ? pathname : "");
    return orig_mkdir ? orig_mkdir(redirected.c_str(), mode) : -1;
}

int FileSystemHook::hook_rmdir(const char* pathname) {
    std::string redirected = IO::redirect(pathname ? pathname : "");
    return orig_rmdir ? orig_rmdir(redirected.c_str()) : -1;
}

bool FileSystemHook::install() {
    bool ok = true;

    void* libc = dlopen("libc.so", RTLD_NOW);

    ok &= hookFunction(dlsym(libc, "open"),    (void*)hook_open,    (void**)&orig_open);
    ok &= hookFunction(dlsym(libc, "stat"),    (void*)hook_stat,    (void**)&orig_stat);
    ok &= hookFunction(dlsym(libc, "access"),  (void*)hook_access,  (void**)&orig_access);
    ok &= hookFunction(dlsym(libc, "opendir"), (void*)hook_opendir, (void**)&orig_opendir);
    ok &= hookFunction(dlsym(libc, "rename"),  (void*)hook_rename,  (void**)&orig_rename);
    ok &= hookFunction(dlsym(libc, "unlink"),  (void*)hook_unlink,  (void**)&orig_unlink);
    ok &= hookFunction(dlsym(libc, "mkdir"),   (void*)hook_mkdir,   (void**)&orig_mkdir);
    ok &= hookFunction(dlsym(libc, "rmdir"),   (void*)hook_rmdir,   (void**)&orig_rmdir);

    LOGI("FileSystemHook::install %s", ok ? "OK" : "partial");

    return ok;
}
