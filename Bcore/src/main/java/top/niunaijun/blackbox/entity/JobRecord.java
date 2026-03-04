package top.niunaijun.blackbox.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class JobRecord implements Parcelable {
    public int jobId;
    public String packageName;
    public String serviceClass;
    public int userId;
    public long latency;
    public long deadline;
    public boolean requiresCharging;
    public boolean requiresNetwork;
    public boolean requiresIdle;

    public JobRecord() {}

    protected JobRecord(Parcel in) {
        jobId = in.readInt();
        packageName = in.readString();
        serviceClass = in.readString();
        userId = in.readInt();
        latency = in.readLong();
        deadline = in.readLong();
        requiresCharging = in.readByte() != 0;
        requiresNetwork = in.readByte() != 0;
        requiresIdle = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(jobId);
        dest.writeString(packageName);
        dest.writeString(serviceClass);
        dest.writeInt(userId);
        dest.writeLong(latency);
        dest.writeLong(deadline);
        dest.writeByte((byte) (requiresCharging ? 1 : 0));
        dest.writeByte((byte) (requiresNetwork ? 1 : 0));
        dest.writeByte((byte) (requiresIdle ? 1 : 0));
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<JobRecord> CREATOR = new Creator<JobRecord>() {
        @Override public JobRecord createFromParcel(Parcel in) { return new JobRecord(in); }
        @Override public JobRecord[] newArray(int size) { return new JobRecord[size]; }
    };
}
