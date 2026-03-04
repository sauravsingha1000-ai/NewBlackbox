package top.niunaijun.blackbox.entity.pm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents an Xposed module installed in the virtual environment.
 */
public class InstalledModule implements Parcelable {
    public String packageName;
    public String name;
    public String description;
    public String apkPath;
    public boolean enabled;
    public int minVersion;
    public int version;

    public InstalledModule() {}

    protected InstalledModule(Parcel in) {
        packageName = in.readString();
        name = in.readString();
        description = in.readString();
        apkPath = in.readString();
        enabled = in.readByte() != 0;
        minVersion = in.readInt();
        version = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(apkPath);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeInt(minVersion);
        dest.writeInt(version);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<InstalledModule> CREATOR = new Creator<InstalledModule>() {
        @Override public InstalledModule createFromParcel(Parcel in) { return new InstalledModule(in); }
        @Override public InstalledModule[] newArray(int size) { return new InstalledModule[size]; }
    };
}
