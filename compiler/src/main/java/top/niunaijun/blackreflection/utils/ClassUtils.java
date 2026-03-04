package top.niunaijun.blackreflection.utils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Utilities for the annotation processor.
 */
public final class ClassUtils {
    private ClassUtils() {}

    public static String getPackageName(TypeElement element) {
        String fullName = element.getQualifiedName().toString();
        int lastDot = fullName.lastIndexOf('.');
        return lastDot > 0 ? fullName.substring(0, lastDot) : "";
    }

    public static String getSimpleName(TypeElement element) {
        return element.getSimpleName().toString();
    }

    public static boolean isPrimitive(TypeMirror type) {
        switch (type.getKind()) {
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                return true;
            default:
                return false;
        }
    }

    public static String boxedType(String primitive) {
        switch (primitive) {
            case "boolean": return "Boolean";
            case "byte":    return "Byte";
            case "char":    return "Character";
            case "double":  return "Double";
            case "float":   return "Float";
            case "int":     return "Integer";
            case "long":    return "Long";
            case "short":   return "Short";
            default: return primitive;
        }
    }
}
