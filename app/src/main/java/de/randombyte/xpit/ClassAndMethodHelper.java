package de.randombyte.xpit;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Some methods for making things easier.
 */
public class ClassAndMethodHelper {

    private final ClassLoader classLoader;

    public ClassAndMethodHelper(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public final HookMethod findMethod(String className, String methodName, Class... parameterTypes) {
        return new HookMethod(XposedHelpers.findMethodExact(findClass(className), methodName, parameterTypes));
    }

    /**
     * Tries to find the class with the provided class name.
     * @param fullyQualifiedClassName The fully qualified class name of the class to be found
     * @return The class corresponding to the given class name
     * @throws XposedHelpers.ClassNotFoundError Thrown if the class couldn't be found
     */
    public final Class findClass(String fullyQualifiedClassName) throws XposedHelpers.ClassNotFoundError {
        return XposedHelpers.findClass(fullyQualifiedClassName, classLoader);
    }

    public final Class[] classes(Object... classesOrClassNames) {
        Class[] classes = new Class[classesOrClassNames.length];
        for (int i = 0; i < classesOrClassNames.length; i++) {
            if (classesOrClassNames[i] instanceof String) {
                classes[i] = findClass((String) classesOrClassNames[i]);
            } else if (classesOrClassNames[i] instanceof Class) {
                classes[i] = (Class) classesOrClassNames[i];
            } else {
                throw new IllegalArgumentException("Parameters have to be an instance of String or Class!");
            }
        }
        return classes;
    }

    public static class HookMethod {

        private final Method hookMethod;

        public HookMethod(Method method) {
            this.hookMethod = method;
        }

        public void hook(XC_MethodHook methodHook) {
            XposedBridge.hookMethod(hookMethod, methodHook);
        }

        public Method getMethod() {
            return hookMethod;
        }
    }
}
