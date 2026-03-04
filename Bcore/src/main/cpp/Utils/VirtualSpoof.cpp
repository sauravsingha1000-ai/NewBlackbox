#include "VirtualSpoof.h"
#include "../Log.h"
#include <mutex>

namespace VirtualSpoof {

static std::string gImei;
static std::string gAndroidId;
static std::string gSerial;
static std::mutex gMutex;

void setImei(const std::string& imei) {
    std::lock_guard<std::mutex> l(gMutex);
    gImei = imei;
    LOGD("Spoof IMEI: %s", imei.c_str());
}

void setAndroidId(const std::string& id) {
    std::lock_guard<std::mutex> l(gMutex);
    gAndroidId = id;
}

void setSerial(const std::string& serial) {
    std::lock_guard<std::mutex> l(gMutex);
    gSerial = serial;
}

const std::string& getImei() { return gImei; }
const std::string& getAndroidId() { return gAndroidId; }
const std::string& getSerial() { return gSerial; }

} // namespace VirtualSpoof
