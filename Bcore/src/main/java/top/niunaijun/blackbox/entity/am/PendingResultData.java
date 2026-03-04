package top.niunaijun.blackbox.entity.am;

import android.os.Parcel;
import android.os.Parcelable;

public class PendingResultData implements Parcelable {
    public int requestCode;
    public android.content.Intent data;
    public int resultCode;

    public PendingResultData() {}

    protected PendingResultData(Parcel in) {
        requestCode = in.readInt();
        data = in.readParcelable(android.content.Intent.class.getClassLoader());
        resultCode = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(requestCode);
        dest.writeParcelable(data, flags);
        dest.writeInt(resultCode);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<PendingResultData> CREATOR = new Creator<PendingResultData>() {
        @Override public PendingResultData createFromParcel(Parcel in) { return new PendingResultData(in); }
        @Override public PendingResultData[] newArray(int size) { return new PendingResultData[size]; }
    };
}
