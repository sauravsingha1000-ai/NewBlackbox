package top.niunaijun.blackbox.core.system.os;
interface IBStorageManagerService {
    String getVirtualStoragePath(String packageName, int userId);
    void mkdirPackageStorage(String packageName, int userId);
    void removePackageStorage(String packageName, int userId);
}
