package top.niunaijun.blackbox.entity.am;

import android.os.Parcel;
import android.os.Parcelable;

public class RunningAppProcessInfo implements Parcelable {
    public String processName;
    public int pid;
    public int uid;
    public String[] pkgList;
    public int userId;

    public RunningAppProcessInfo() {}

    protected RunningAppProcessInfo(Parcel in) {
        processName = in.readString();
        pid = in.readInt();
        uid = in.readInt();
        pkgList = in.createStringArray();
        userId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(processName);
        dest.writeInt(pid);
        dest.writeInt(uid);
        dest.writeStringArray(pkgList);
        dest.writeInt(userId);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<RunningAppProcessInfo> CREATOR =
            new Creator<RunningAppProcessInfo>() {
        @Override public RunningAppProcessInfo createFromParcel(Parcel in) {
            return new RunningAppProcessInfo(in);
        }
        @Override public RunningAppProcessInfo[] newArray(int size) {
            return new RunningAppProcessInfo[size];
        }
    };
}
