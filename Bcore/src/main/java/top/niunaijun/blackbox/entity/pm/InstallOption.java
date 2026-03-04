package top.niunaijun.blackbox.entity.pm;

import android.os.Parcel;
import android.os.Parcelable;

public class InstallOption implements Parcelable {
    public static final int FLAG_INSTALL_SYSTEM = 1;
    public static final int FLAG_INSTALL_SILENTLY = 1 << 1;

    public String apkPath;
    public int userId;
    public int flags;
    public boolean overrideInstall;

    public InstallOption() {}

    public InstallOption(String apkPath, int userId) {
        this.apkPath = apkPath;
        this.userId = userId;
    }

    protected InstallOption(Parcel in) {
        apkPath = in.readString();
        userId = in.readInt();
        flags = in.readInt();
        overrideInstall = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apkPath);
        dest.writeInt(userId);
        dest.writeInt(this.flags);
        dest.writeByte((byte) (overrideInstall ? 1 : 0));
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<InstallOption> CREATOR = new Creator<InstallOption>() {
        @Override public InstallOption createFromParcel(Parcel in) { return new InstallOption(in); }
        @Override public InstallOption[] newArray(int size) { return new InstallOption[size]; }
    };
}
