package top.niunaijun.blackbox.entity.am;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;

public class RunningServiceInfo implements Parcelable {
    public ComponentName service;
    public int pid;
    public int uid;
    public String process;
    public boolean foreground;
    public int userId;

    public RunningServiceInfo() {}

    protected RunningServiceInfo(Parcel in) {
        service = in.readParcelable(ComponentName.class.getClassLoader());
        pid = in.readInt();
        uid = in.readInt();
        process = in.readString();
        foreground = in.readByte() != 0;
        userId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(service, flags);
        dest.writeInt(pid);
        dest.writeInt(uid);
        dest.writeString(process);
        dest.writeByte((byte) (foreground ? 1 : 0));
        dest.writeInt(userId);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<RunningServiceInfo> CREATOR =
            new Creator<RunningServiceInfo>() {
        @Override public RunningServiceInfo createFromParcel(Parcel in) {
            return new RunningServiceInfo(in);
        }
        @Override public RunningServiceInfo[] newArray(int size) {
            return new RunningServiceInfo[size];
        }
    };
}
