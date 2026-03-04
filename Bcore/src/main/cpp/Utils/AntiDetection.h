#pragma once
/**
 * Techniques to prevent virtual environment detection by guest apps.
 */
namespace AntiDetection {
    void init();
    void patchMaps();
    void patchProcStatus();
    bool isMapsPatched();
}
