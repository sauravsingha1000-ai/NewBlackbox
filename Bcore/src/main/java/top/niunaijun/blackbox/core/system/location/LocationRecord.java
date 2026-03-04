package top.niunaijun.blackbox.core.system.location;

import top.niunaijun.blackbox.entity.location.BLocation;

/**
 * Tracks a location update subscription for a virtual app.
 */
public class LocationRecord {
    public String packageName;
    public int userId;
    public String provider;
    public long minTime;
    public float minDistance;
    public BLocation lastLocation;

    public LocationRecord(String packageName, int userId, String provider) {
        this.packageName = packageName;
        this.userId = userId;
        this.provider = provider;
    }
}
