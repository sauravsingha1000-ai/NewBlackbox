#include "HexDump.h"
#include <sstream>
#include <iomanip>
#include <cctype>

namespace HexDump {

std::string dump(const void* data, size_t len, size_t bytesPerLine) {
    const uint8_t* buf = static_cast<const uint8_t*>(data);
    std::ostringstream oss;
    for (size_t i = 0; i < len; i += bytesPerLine) {
        oss << std::hex << std::setw(8) << std::setfill('0') << i << "  ";
        for (size_t j = 0; j < bytesPerLine; ++j) {
            if (i + j < len)
                oss << std::hex << std::setw(2) << std::setfill('0')
                    << (int)buf[i + j] << " ";
            else oss << "   ";
        }
        oss << " |";
        for (size_t j = 0; j < bytesPerLine && i + j < len; ++j)
            oss << (char)(std::isprint(buf[i + j]) ? buf[i + j] : '.');
        oss << "|\n";
    }
    return oss.str();
}

} // namespace HexDump
