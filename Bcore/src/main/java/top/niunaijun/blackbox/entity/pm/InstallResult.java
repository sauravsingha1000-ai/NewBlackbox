package top.niunaijun.blackbox.entity.pm;

import android.os.Parcel;
import android.os.Parcelable;

public class InstallResult implements Parcelable {
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_FAILED_DUPLICATE = -1;
    public static final int CODE_FAILED_INVALID_APK = -2;
    public static final int CODE_FAILED_STORAGE = -3;
    public static final int CODE_FAILED_UNKNOWN = -99;

    public boolean success;
    public int code;
    public String packageName;
    public String message;

    public InstallResult() {}

    public static InstallResult success(String packageName) {
        InstallResult r = new InstallResult();
        r.success = true;
        r.code = CODE_SUCCESS;
        r.packageName = packageName;
        return r;
    }

    public static InstallResult failed(int code, String message) {
        InstallResult r = new InstallResult();
        r.success = false;
        r.code = code;
        r.message = message;
        return r;
    }

    protected InstallResult(Parcel in) {
        success = in.readByte() != 0;
        code = in.readInt();
        packageName = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeInt(code);
        dest.writeString(packageName);
        dest.writeString(message);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<InstallResult> CREATOR = new Creator<InstallResult>() {
        @Override public InstallResult createFromParcel(Parcel in) { return new InstallResult(in); }
        @Override public InstallResult[] newArray(int size) { return new InstallResult[size]; }
    };
}
