package top.niunaijun.blackbox.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UnbindRecord implements Parcelable {
    public String packageName;
    public int userId;
    public android.content.Intent intent;

    public UnbindRecord() {}

    protected UnbindRecord(Parcel in) {
        packageName = in.readString();
        userId = in.readInt();
        intent = in.readParcelable(android.content.Intent.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeInt(userId);
        dest.writeParcelable(intent, flags);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<UnbindRecord> CREATOR = new Creator<UnbindRecord>() {
        @Override public UnbindRecord createFromParcel(Parcel in) { return new UnbindRecord(in); }
        @Override public UnbindRecord[] newArray(int size) { return new UnbindRecord[size]; }
    };
}
