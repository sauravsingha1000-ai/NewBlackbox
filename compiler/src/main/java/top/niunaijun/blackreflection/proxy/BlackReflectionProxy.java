package top.niunaijun.blackreflection.proxy;

/**
 * Base class for all generated black-reflection implementation classes.
 */
public abstract class BlackReflectionProxy {
    protected Object mReceiver;

    protected BlackReflectionProxy(Object receiver) {
        this.mReceiver = receiver;
    }

    public Object getReceiver() {
        return mReceiver;
    }

    public void setReceiver(Object receiver) {
        this.mReceiver = receiver;
    }
}
