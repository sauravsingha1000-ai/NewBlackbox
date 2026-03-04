package top.niunaijun.blackbox.entity.location;

import android.os.Parcel;
import android.os.Parcelable;

public class BLocationConfig implements Parcelable {
    public boolean enabled;
    public boolean followRealLocation;
    public double offsetLat;
    public double offsetLng;

    public BLocationConfig() {}

    protected BLocationConfig(Parcel in) {
        enabled = in.readByte() != 0;
        followRealLocation = in.readByte() != 0;
        offsetLat = in.readDouble();
        offsetLng = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeByte((byte) (followRealLocation ? 1 : 0));
        dest.writeDouble(offsetLat);
        dest.writeDouble(offsetLng);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<BLocationConfig> CREATOR = new Creator<BLocationConfig>() {
        @Override public BLocationConfig createFromParcel(Parcel in) { return new BLocationConfig(in); }
        @Override public BLocationConfig[] newArray(int size) { return new BLocationConfig[size]; }
    };
}
