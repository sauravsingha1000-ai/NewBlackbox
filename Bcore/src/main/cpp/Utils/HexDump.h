#pragma once
#include <string>
#include <cstdint>

namespace HexDump {
    std::string dump(const void* data, size_t len, size_t bytesPerLine = 16);
}
