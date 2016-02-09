package de.randombyte.xpit.hooks;

import de.randombyte.xpit.ClassAndMethodHelper;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.randombyte.xpit.ClassAndMethodHelper.*;

/**
 * All hooks should extend this class to initialize their hooks and get some helper methods.
 */
public abstract class HookProvider {

    protected final XC_LoadPackage.LoadPackageParam loadPackageParam;
    private final ClassAndMethodHelper helper;

    public HookProvider(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;
        helper = new ClassAndMethodHelper(loadPackageParam.classLoader);
    }

    /**
     * Called once when the package of the target app is loaded.
     */
    public abstract void initHooks();

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
