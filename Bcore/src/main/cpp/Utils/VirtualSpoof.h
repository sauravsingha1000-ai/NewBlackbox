#pragma once
#include <string>
/**
 * Runtime spoofing of device identifiers (IMEI, Android ID, etc.).
 */
namespace VirtualSpoof {
    void setImei(const std::string& imei);
    void setAndroidId(const std::string& androidId);
    void setSerial(const std::string& serial);
    const std::string& getImei();
    const std::string& getAndroidId();
    const std::string& getSerial();
}
