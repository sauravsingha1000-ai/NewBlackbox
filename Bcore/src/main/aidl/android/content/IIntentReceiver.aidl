package android.content;
oneway interface IIntentReceiver {
    void performReceive(in Intent intent, int resultCode, String data,
        in Bundle extras, boolean ordered, boolean sticky, int sendingUser);
}
