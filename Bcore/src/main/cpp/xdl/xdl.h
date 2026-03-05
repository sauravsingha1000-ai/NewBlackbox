#pragma once
#include <stdint.h>
#include <stddef.h>
#include <link.h>

#ifdef __cplusplus
extern "C" {
#endif

#define XDL_DEFAULT 0x00
#define XDL_RESOLVE_WEAK 0x01

void*  xdl_open(const char* filename, int flags);
void*  xdl_sym(void* handle, const char* symbol, size_t* size);
int    xdl_close(void* handle);
int    xdl_iterate_phdr(int (*callback)(struct dl_phdr_info*, size_t, void*), void* data);
void*  xdl_addr(void* addr, void** handle);
void   xdl_info(void* handle, int request, void* info);

#ifdef __cplusplus
}
#endif
