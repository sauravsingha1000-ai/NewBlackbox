package top.niunaijun.blackbox.core.system.user;
import top.niunaijun.blackbox.core.system.user.BUserInfo;

interface IBUserManagerService {
    BUserInfo createUser(String name, int flags);
    boolean removeUser(int userId);
    List<BUserInfo> getUsers(boolean excludeDying);
    BUserInfo getUserInfo(int userId);
    boolean isUserRunning(int userId);
    boolean isUserExists(int userId);
}
