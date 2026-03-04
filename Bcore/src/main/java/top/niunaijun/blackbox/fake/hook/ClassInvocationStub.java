package top.niunaijun.blackbox.fake.hook;

import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic class-level invocation stub for non-Binder proxy hooks.
 */
public abstract class ClassInvocationStub implements InvocationHandler, IInjectHook {
    private static final String TAG = "ClassInvocationStub";
    protected Object mBase;
    protected final Map<String, MethodHook> mMethodHooks = new HashMap<>();

    protected abstract Class<?>[] getProxyInterfaces();
    protected abstract Object createProxy() throws Throwable;

    @Override
    public void inject() throws Throwable {
        Object proxy = createProxy();
        if (proxy != null) replaceTarget(proxy);
    }

    protected void replaceTarget(Object proxy) throws Throwable {}

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHook hook = mMethodHooks.get(method.getName());
        if (hook != null) {
            MethodHook.MethodHookParam param = new MethodHook.MethodHookParam();
            param.thisObject = mBase;
            param.args = args != null ? args : new Object[0];
            hook.beforeHookedMethod(param);
            if (param.isReturnEarly()) return param.result;
        }
        if (mBase != null) return method.invoke(mBase, args);
        return null;
    }

    protected void addHook(String methodName, MethodHook hook) {
        mMethodHooks.put(methodName, hook);
    }
}
