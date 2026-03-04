package top.niunaijun.blackbox.entity.location;

import android.os.Parcel;
import android.os.Parcelable;

public class BCell implements Parcelable {
    public int mcc;
    public int mnc;
    public int lac;
    public int cid;
    public int signalStrength;

    public BCell() {}

    protected BCell(Parcel in) {
        mcc = in.readInt();
        mnc = in.readInt();
        lac = in.readInt();
        cid = in.readInt();
        signalStrength = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mcc);
        dest.writeInt(mnc);
        dest.writeInt(lac);
        dest.writeInt(cid);
        dest.writeInt(signalStrength);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<BCell> CREATOR = new Creator<BCell>() {
        @Override public BCell createFromParcel(Parcel in) { return new BCell(in); }
        @Override public BCell[] newArray(int size) { return new BCell[size]; }
    };
}
