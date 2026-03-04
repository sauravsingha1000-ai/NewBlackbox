package top.niunaijun.blackbox.entity.location;

import android.os.Parcel;
import android.os.Parcelable;

public class BLocation implements Parcelable {
    public double latitude;
    public double longitude;
    public double altitude;
    public float speed;
    public float bearing;
    public float accuracy;
    public long time;
    public String provider;

    public BLocation() {
        provider = "gps";
        time = System.currentTimeMillis();
    }

    public BLocation(double latitude, double longitude) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected BLocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        speed = in.readFloat();
        bearing = in.readFloat();
        accuracy = in.readFloat();
        time = in.readLong();
        provider = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeFloat(speed);
        dest.writeFloat(bearing);
        dest.writeFloat(accuracy);
        dest.writeLong(time);
        dest.writeString(provider);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<BLocation> CREATOR = new Creator<BLocation>() {
        @Override public BLocation createFromParcel(Parcel in) { return new BLocation(in); }
        @Override public BLocation[] newArray(int size) { return new BLocation[size]; }
    };

    public android.location.Location toAndroidLocation() {
        android.location.Location loc = new android.location.Location(provider);
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        loc.setAltitude(altitude);
        loc.setSpeed(speed);
        loc.setBearing(bearing);
        loc.setAccuracy(accuracy);
        loc.setTime(time);
        return loc;
    }
}
