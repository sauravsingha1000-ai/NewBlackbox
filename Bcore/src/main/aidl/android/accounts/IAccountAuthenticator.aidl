package android.accounts;
oneway interface IAccountAuthenticator {
    void addAccount(in IBinder response, String accountType, String authTokenType,
        in String[] requiredFeatures, in Bundle options);
    void confirmCredentials(in IBinder response, in Account account, in Bundle options);
    void getAuthToken(in IBinder response, in Account account, String authTokenType,
        in Bundle loginOptions);
    void getAuthTokenLabel(in IBinder response, String authTokenType);
    void updateCredentials(in IBinder response, in Account account, String authTokenType,
        in Bundle loginOptions);
    void hasFeatures(in IBinder response, in Account account, in String[] features);
    void getAccountRemovalAllowed(in IBinder response, in Account account);
    void getAccountCredentialsForCloning(in IBinder response, in Account account);
    void addAccountFromCredentials(in IBinder response, in Account account,
        in Bundle accountCredentials);
    void startAddAccountSession(in IBinder response, String accountType, String authTokenType,
        in String[] requiredFeatures, in Bundle options);
    void startUpdateCredentialsSession(in IBinder response, in Account account,
        String authTokenType, in Bundle loginOptions);
    void finishSession(in IBinder response, String accountType, in Bundle sessionBundle);
    void isCredentialsUpdateSuggested(in IBinder response, in Account account,
        String statusToken);
}
