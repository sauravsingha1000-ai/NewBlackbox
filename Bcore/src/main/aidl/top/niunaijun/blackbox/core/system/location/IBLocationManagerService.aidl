package top.niunaijun.blackbox.core.system.location;
import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.entity.location.BLocationConfig;

interface IBLocationManagerService {
    void setFakeLocation(in BLocation location, int userId);
    BLocation getFakeLocation(int userId);
    void setLocationConfig(in BLocationConfig config, int userId);
    BLocationConfig getLocationConfig(int userId);
    void clearFakeLocation(int userId);
}
