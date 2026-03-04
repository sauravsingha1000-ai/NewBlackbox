package top.niunaijun.blackbox.core.system;

/**
 * Configuration for JAR files bundled with the virtual engine.
 */
public class JarConfig {
    public static final String EMPTY_JAR = "empty.jar";
    public static final String JUNIT_JAR = "junit.jar";

    public final String name;
    public final String assetPath;
    public boolean extracted;

    public JarConfig(String name, String assetPath) {
        this.name = name;
        this.assetPath = assetPath;
    }
}
