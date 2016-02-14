package de.randombyte.xpit.xposed.hooks;

import de.randombyte.xpit.xposed.ClassAndMethodHelper;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.randombyte.xpit.xposed.ClassAndMethodHelper.*;

/**
 * All hooks should extend this class to initialize their hooks and get some helper methods.
 */
public abstract class HookProvider {

    protected XC_LoadPackage.LoadPackageParam loadPackageParam;
    private ClassAndMethodHelper helper;

    public void initHooks(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;
        helper = new ClassAndMethodHelper(loadPackageParam.classLoader);
    }

    //Shadows of ClassAndMethodHelper
    public final HookMethod findMethod(String className, String methodName, Class... parameterTypes) {
        return helper.findMethod(className, methodName, parameterTypes);
    }

    public final Class findClass(String fullyQualifiedClassName) throws XposedHelpers.ClassNotFoundError {
        return helper.findClass(fullyQualifiedClassName);
    }

    public final Class[] classes(Object... classesOrClassNames) {
        return helper.classes(classesOrClassNames);
    }
}
