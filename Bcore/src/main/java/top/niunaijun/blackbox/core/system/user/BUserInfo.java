package top.niunaijun.blackbox.core.system.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Information about a virtual user.
 */
public class BUserInfo implements Parcelable {
    public static final int FLAG_PRIMARY = 0x00000001;
    public static final int FLAG_ADMIN   = 0x00000002;
    public static final int FLAG_GUEST   = 0x00000004;

    public int id;
    public String name;
    public int flags;
    public boolean running;
    public long creationTime;

    public BUserInfo() {}

    public BUserInfo(int id, String name, int flags) {
        this.id = id;
        this.name = name;
        this.flags = flags;
        this.creationTime = System.currentTimeMillis();
    }

    public boolean isPrimary() { return (flags & FLAG_PRIMARY) != 0; }
    public boolean isAdmin()   { return (flags & FLAG_ADMIN) != 0; }
    public boolean isGuest()   { return (flags & FLAG_GUEST) != 0; }

    protected BUserInfo(Parcel in) {
        id = in.readInt();
        name = in.readString();
        flags = in.readInt();
        running = in.readByte() != 0;
        creationTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int f) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(flags);
        dest.writeByte((byte)(running ? 1 : 0));
        dest.writeLong(creationTime);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<BUserInfo> CREATOR = new Creator<BUserInfo>() {
        @Override public BUserInfo createFromParcel(Parcel in) { return new BUserInfo(in); }
        @Override public BUserInfo[] newArray(int size) { return new BUserInfo[size]; }
    };
}
