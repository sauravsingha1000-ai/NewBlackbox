package android.accounts;
oneway interface IAccountAuthenticatorResponse {
    void onResult(in Bundle value);
    void onRequestContinued();
    void onError(int errorCode, String errorMessage);
}
