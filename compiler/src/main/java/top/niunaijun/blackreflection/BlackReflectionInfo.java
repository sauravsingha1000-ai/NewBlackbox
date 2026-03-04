package top.niunaijun.blackreflection;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds metadata for one BClass-annotated interface during code generation.
 */
public class BlackReflectionInfo {
    public String interfaceClassName;
    public String realClassName;
    public String packageName;
    public String simpleInterfaceName;
    public List<BlackReflectionInterfaceInfo> methods = new ArrayList<>();

    public BlackReflectionInfo(String interfaceClassName, String realClassName,
                                String packageName, String simpleInterfaceName) {
        this.interfaceClassName = interfaceClassName;
        this.realClassName = realClassName;
        this.packageName = packageName;
        this.simpleInterfaceName = simpleInterfaceName;
    }

    public String getGeneratedClassName() {
        return simpleInterfaceName + "Impl";
    }
}
