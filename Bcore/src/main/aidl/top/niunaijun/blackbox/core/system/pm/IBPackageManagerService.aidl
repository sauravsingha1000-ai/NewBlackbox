package top.niunaijun.blackbox.core.system.pm;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.entity.pm.InstalledPackage;

interface IBPackageManagerService {
    InstallResult installPackageAsUser(in InstallOption option, int userId);
    void uninstallPackageAsUser(String packageName, int userId);
    List<InstalledPackage> getInstalledPackages(int flags, int userId);
    InstalledPackage getInstalledPackage(String packageName, int userId);
    boolean isInstalled(String packageName, int userId);
    List<String> getInstalledPackageNames(int userId);
    void clearPackage(String packageName, int userId);
}
