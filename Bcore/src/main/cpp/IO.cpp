#include "IO.h"
#include "Log.h"
#include <mutex>
#include <algorithm>

namespace IO {

static std::unordered_map<std::string, std::string> gRedirects;
static std::mutex gMutex;
static std::string gPackageName;
static std::string gDataPath;

void init(const std::string& packageName, const std::string& dataPath) {
    std::lock_guard<std::mutex> lock(gMutex);
    gPackageName = packageName;
    gDataPath    = dataPath;
    LOGD("IO::init pkg=%s data=%s", packageName.c_str(), dataPath.c_str());
}

void addRedirect(const std::string& source, const std::string& target) {
    std::lock_guard<std::mutex> lock(gMutex);
    gRedirects[source] = target;
    LOGD("IO redirect: %s -> %s", source.c_str(), target.c_str());
}

void removeRedirect(const std::string& source) {
    std::lock_guard<std::mutex> lock(gMutex);
    gRedirects.erase(source);
}

std::string getRealPath(const std::string& virtualPath) {
    return redirect(virtualPath);
}

std::string redirect(const std::string& path) {
    std::lock_guard<std::mutex> lock(gMutex);
    for (const auto& kv : gRedirects) {
        if (path.find(kv.first) == 0) {
            return kv.second + path.substr(kv.first.length());
        }
    }
    return path;
}

bool isVirtualPath(const std::string& path) {
    std::lock_guard<std::mutex> lock(gMutex);
    for (const auto& kv : gRedirects) {
        if (path.find(kv.second) == 0) return true;
    }
    return false;
}

} // namespace IO
