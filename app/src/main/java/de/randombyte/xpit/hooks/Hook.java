package de.randombyte.xpit.hooks;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;

public class Hook {

    public final Method method;
    public final XC_MethodHook hook;

    public Hook(Method method, XC_MethodHook hook) {
        this.method = method;
        this.hook = hook;
    }
}
