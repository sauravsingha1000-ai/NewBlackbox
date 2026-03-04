#pragma once
/**
 * Dobby - A lightweight, multi-platform, multi-architecture hook framework.
 * This is the public API header.
 */

#ifdef __cplusplus
extern "C" {
#endif

#define RS_SUCCESS 0
#define RS_FAILED  -1

/**
 * Hook a function at the given address.
 *
 * @param address     Address of the function to hook
 * @param fake_func   Address of the replacement function
 * @param orig_func   Output: pointer to original function trampoline
 * @return RS_SUCCESS or RS_FAILED
 */
int DobbyHook(void* address, void* fake_func, void** orig_func);

/**
 * Unhook a previously hooked function.
 */
int DobbyDestroy(void* address);

/**
 * Commit pending hook patches (required on some versions).
 */
void DobbyCommit();

#ifdef __cplusplus
}
#endif
