package top.niunaijun.blackbox.fake.hook;

import android.os.IBinder;
import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Binder-proxy hooks that intercept system service calls.
 */
public abstract class BinderInvocationStub implements InvocationHandler, IInjectHook {
    private static final String TAG = "BinderInvocationStub";
    protected Object mBase;
    protected final Map<String, MethodHook> mMethodHooks = new HashMap<>();

    protected abstract Object getBase() throws Throwable;
    protected abstract Class<?> getInterfaceClass();

    @Override
    public void inject() throws Throwable {
        mBase = getBase();
        if (mBase == null) {
            Log.w(TAG, "Base is null for " + getClass().getSimpleName());
            return;
        }
        Object proxy = Proxy.newProxyInstance(
                mBase.getClass().getClassLoader(),
                new Class<?>[]{getInterfaceClass()},
                this);
        replaceService(proxy);
    }

    protected void replaceService(Object proxy) throws Throwable {
        // Subclasses override to set the proxy into the appropriate field
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHook hook = mMethodHooks.get(method.getName());
        if (hook != null) {
            MethodHook.MethodHookParam param = new MethodHook.MethodHookParam();
            param.thisObject = mBase;
            param.args = args;
            hook.beforeHookedMethod(param);
            if (param.isReturnEarly()) return param.result;
        }

        Object result = method.invoke(mBase, args);

        if (hook != null) {
            MethodHook.MethodHookParam param = new MethodHook.MethodHookParam();
            param.thisObject = mBase;
            param.args = args;
            param.result = result;
            hook.afterHookedMethod(param);
            return param.result;
        }
        return result;
    }

    protected void addHook(String methodName, MethodHook hook) {
        mMethodHooks.put(methodName, hook);
    }
}
