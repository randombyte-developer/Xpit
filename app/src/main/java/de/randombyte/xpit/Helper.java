package de.randombyte.xpit;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;

public class Helper {

    public static ClassLoader classLoader;

    public static Method findMethod(String className, String methodName, Object... args) {
        return XposedHelpers.findMethodExact(findClass(className), methodName, toTypes(args));
    }

    public static Class[] toTypes(Object[] args) {
        Class[] classes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                classes[i] = findClass((String) args[i]);
            } else if (args[i] instanceof Class) {
                classes[i] = (Class) args[i];
            } else {
                throw new IllegalArgumentException("Args have to be String or Class!");
            }
        }
        return classes;
    }

    public static Class findClass(String className) {
        return XposedHelpers.findClass(className, classLoader);
    }
}
