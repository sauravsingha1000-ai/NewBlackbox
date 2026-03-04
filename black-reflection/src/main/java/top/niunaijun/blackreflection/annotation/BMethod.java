package top.niunaijun.blackreflection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method proxy that delegates to a hidden/internal Android method via reflection.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BMethod {
    /** The real method name. Defaults to the annotated method name. */
    String value() default "";
}
