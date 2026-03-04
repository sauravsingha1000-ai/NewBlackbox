# Keep all BlackBox public API
-keep class top.niunaijun.blackbox.BlackBoxCore { *; }
-keep class top.niunaijun.blackbox.entity.** { *; }
-keep class top.niunaijun.blackbox.app.** { *; }
-keepclassmembers class * implements android.os.Parcelable { *; }
-keep class * implements android.os.IBinder { *; }
