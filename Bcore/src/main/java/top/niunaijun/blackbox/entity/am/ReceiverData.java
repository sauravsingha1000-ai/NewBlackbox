package top.niunaijun.blackbox.entity.am;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ReceiverData implements Parcelable {
    public Intent intent;
    public android.content.pm.ActivityInfo info;
    public Bundle extras;
    public int resultCode;
    public String resultData;

    public ReceiverData() {}

    protected ReceiverData(Parcel in) {
        intent = in.readParcelable(Intent.class.getClassLoader());
        info = in.readParcelable(android.content.pm.ActivityInfo.class.getClassLoader());
        extras = in.readBundle();
        resultCode = in.readInt();
        resultData = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(intent, flags);
        dest.writeParcelable(info, flags);
        dest.writeBundle(extras);
        dest.writeInt(resultCode);
        dest.writeString(resultData);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<ReceiverData> CREATOR = new Creator<ReceiverData>() {
        @Override public ReceiverData createFromParcel(Parcel in) { return new ReceiverData(in); }
        @Override public ReceiverData[] newArray(int size) { return new ReceiverData[size]; }
    };
}
