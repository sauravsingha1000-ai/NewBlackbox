package top.niunaijun.blackbox.app.configuration;

/**
 * Per-client configuration for the virtual engine.
 */
public class ClientConfiguration {
    private boolean mEnableGms = false;
    private boolean mEnableFakeLocation = false;
    private boolean mEnableDeviceSpoof = false;
    private boolean mEnableMultiUser = true;

    public static ClientConfiguration defaults() {
        return new ClientConfiguration();
    }

    public ClientConfiguration enableGms(boolean enable) { mEnableGms = enable; return this; }
    public ClientConfiguration enableFakeLocation(boolean e) { mEnableFakeLocation = e; return this; }
    public ClientConfiguration enableDeviceSpoof(boolean e) { mEnableDeviceSpoof = e; return this; }
    public ClientConfiguration enableMultiUser(boolean e) { mEnableMultiUser = e; return this; }

    public boolean isGmsEnabled()         { return mEnableGms; }
    public boolean isFakeLocationEnabled(){ return mEnableFakeLocation; }
    public boolean isDeviceSpoofEnabled() { return mEnableDeviceSpoof; }
    public boolean isMultiUserEnabled()   { return mEnableMultiUser; }
}
