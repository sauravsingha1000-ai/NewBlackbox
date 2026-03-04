package top.niunaijun.blackboxa.util;

public class MathUtil {
    public static float dp2px(android.content.Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float px2dp(android.content.Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float sp2px(android.content.Context context, float sp) {
        return sp * context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
