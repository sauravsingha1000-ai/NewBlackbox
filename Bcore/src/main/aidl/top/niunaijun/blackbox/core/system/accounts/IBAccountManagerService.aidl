package top.niunaijun.blackbox.core.system.accounts;
interface IBAccountManagerService {
    Account[] getAccountsByType(String type, int userId);
    boolean addAccount(in Account account, String password, in Bundle extras, int userId);
    boolean removeAccount(in Account account, int userId);
    String getPassword(in Account account, int userId);
    void setPassword(in Account account, String password, int userId);
    Bundle getAuthToken(in Account account, String authTokenType, int userId);
    void invalidateAuthToken(String accountType, String authToken, int userId);
}
