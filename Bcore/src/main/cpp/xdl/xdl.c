#include <link.h>
#include "xdl.h"
#include <dlfcn.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

void* xdl_open(const char* filename, int flags) {
    (void)flags;
    return dlopen(filename, RTLD_NOW | RTLD_GLOBAL);
}

void* xdl_sym(void* handle, const char* symbol, size_t* size) {
    (void)size;

    if (!handle || !symbol)
        return NULL;

    return dlsym(handle, symbol);
}

int xdl_close(void* handle) {
    if (!handle)
        return 0;

    return dlclose(handle);
}

int xdl_iterate_phdr(int (*callback)(struct dl_phdr_info*, size_t, void*), void* data) {
    return dl_iterate_phdr(callback, data);
}

void* xdl_addr(void* addr, void** handle) {
    Dl_info info;

    if (dladdr(addr, &info) == 0)
        return NULL;

    if (handle)
        *handle = dlopen(info.dli_fname, RTLD_NOLOAD);

    return (void*)info.dli_saddr;
}

void xdl_info(void* handle, int request, void* info) {
    (void)handle;
    (void)request;
    (void)info;
}
