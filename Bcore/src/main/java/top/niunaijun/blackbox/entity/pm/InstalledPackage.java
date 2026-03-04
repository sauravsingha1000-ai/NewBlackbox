package top.niunaijun.blackbox.entity.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class InstalledPackage implements Parcelable {
    public String packageName;
    public String apkPath;
    public String dataDir;
    public int userId;
    public long installTime;
    public long updateTime;
    public PackageInfo packageInfo;
    public ApplicationInfo applicationInfo;
    public int versionCode;
    public String versionName;

    public InstalledPackage() {}

    protected InstalledPackage(Parcel in) {
        packageName = in.readString();
        apkPath = in.readString();
        dataDir = in.readString();
        userId = in.readInt();
        installTime = in.readLong();
        updateTime = in.readLong();
        packageInfo = in.readParcelable(PackageInfo.class.getClassLoader());
        applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
        versionCode = in.readInt();
        versionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeString(apkPath);
        dest.writeString(dataDir);
        dest.writeInt(userId);
        dest.writeLong(installTime);
        dest.writeLong(updateTime);
        dest.writeParcelable(packageInfo, flags);
        dest.writeParcelable(applicationInfo, flags);
        dest.writeInt(versionCode);
        dest.writeString(versionName);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<InstalledPackage> CREATOR = new Creator<InstalledPackage>() {
        @Override public InstalledPackage createFromParcel(Parcel in) { return new InstalledPackage(in); }
        @Override public InstalledPackage[] newArray(int size) { return new InstalledPackage[size]; }
    };
}
