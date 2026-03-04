package top.niunaijun.blackbox.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Per-app configuration stored in the virtual engine.
 */
public class AppConfig implements Parcelable {
    public String packageName;
    public boolean enableFakeLocation;
    public boolean enableDeviceSpoof;
    public boolean enableNetworkProxy;
    public String customDeviceId;
    public String customAndroidId;
    public int userId;

    public AppConfig() {}

    protected AppConfig(Parcel in) {
        packageName = in.readString();
        enableFakeLocation = in.readByte() != 0;
        enableDeviceSpoof = in.readByte() != 0;
        enableNetworkProxy = in.readByte() != 0;
        customDeviceId = in.readString();
        customAndroidId = in.readString();
        userId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeByte((byte) (enableFakeLocation ? 1 : 0));
        dest.writeByte((byte) (enableDeviceSpoof ? 1 : 0));
        dest.writeByte((byte) (enableNetworkProxy ? 1 : 0));
        dest.writeString(customDeviceId);
        dest.writeString(customAndroidId);
        dest.writeInt(userId);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<AppConfig> CREATOR = new Creator<AppConfig>() {
        @Override
        public AppConfig createFromParcel(Parcel in) { return new AppConfig(in); }
        @Override
        public AppConfig[] newArray(int size) { return new AppConfig[size]; }
    };
}
