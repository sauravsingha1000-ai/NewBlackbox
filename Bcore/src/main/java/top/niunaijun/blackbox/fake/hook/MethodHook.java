package top.niunaijun.blackbox.fake.hook;

import android.util.Log;
import java.lang.reflect.Method;

/**
 * Represents a hooked method with before/after callbacks.
 */
public abstract class MethodHook {
    protected Method mOriginalMethod;

    public void beforeHookedMethod(MethodHookParam param) throws Throwable {}
    public void afterHookedMethod(MethodHookParam param) throws Throwable {}

    public static class MethodHookParam {
        public Object thisObject;
        public Object[] args;
        public Object result;
        public Throwable throwable;
        private boolean returnEarly;

        public void setResult(Object result) {
            this.result = result;
            this.returnEarly = true;
        }

        public boolean isReturnEarly() { return returnEarly; }
    }
}
