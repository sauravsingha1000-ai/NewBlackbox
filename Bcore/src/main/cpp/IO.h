#pragma once
#include <string>
#include <unordered_map>

namespace IO {
    void init(const std::string& packageName, const std::string& dataPath);
    void addRedirect(const std::string& source, const std::string& target);
    void removeRedirect(const std::string& source);
    std::string getRealPath(const std::string& virtualPath);
    std::string redirect(const std::string& path);
    bool isVirtualPath(const std::string& path);
}
