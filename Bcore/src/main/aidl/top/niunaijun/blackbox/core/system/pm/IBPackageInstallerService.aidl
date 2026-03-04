package top.niunaijun.blackbox.core.system.pm;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;

interface IBPackageInstallerService {
    InstallResult install(in InstallOption option, int userId);
    void uninstall(String packageName, int userId);
}
