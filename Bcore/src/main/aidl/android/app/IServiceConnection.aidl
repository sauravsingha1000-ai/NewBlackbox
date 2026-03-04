package android.app;
oneway interface IServiceConnection {
    void connected(in ComponentName name, IBinder service, boolean dead);
}
