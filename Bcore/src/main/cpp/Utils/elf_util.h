#pragma once
#include <string>
#include <cstdint>

/**
 * ELF binary utility for symbol lookup in loaded libraries.
 */
namespace ElfUtil {
    void* findSymbol(const std::string& libraryPath, const std::string& symbolName);
    uintptr_t getLibraryBase(const std::string& libraryName);
}
