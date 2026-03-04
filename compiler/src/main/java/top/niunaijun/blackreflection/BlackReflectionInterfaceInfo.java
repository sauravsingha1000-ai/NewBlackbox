package top.niunaijun.blackreflection;

import java.util.List;

/**
 * Metadata for a single method in a BClass interface.
 */
public class BlackReflectionInterfaceInfo {
    public enum MethodType {
        METHOD, STATIC_METHOD, FIELD, STATIC_FIELD, CONSTRUCTOR, CLASS_NAME
    }

    public String methodName;
    public String realName;
    public String returnType;
    public List<String> paramTypes;
    public List<String> paramNames;
    public MethodType methodType;
    public boolean process = true;

    public BlackReflectionInterfaceInfo(String methodName, String realName,
                                         String returnType, List<String> paramTypes,
                                         List<String> paramNames, MethodType methodType) {
        this.methodName = methodName;
        this.realName = realName;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
        this.methodType = methodType;
    }
}
