package de.randombyte.xpit.hooks;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import de.randombyte.xpit.Settings;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * The user can change how many threads should be loaded at once.
 */
//Not used because the target's api sends max 20 threads regardless what number "max" is.
public class LoadThreadsAmount {

    public static final String PREF_KEY = "LoadThreadsAmount";

    public static void init(XC_LoadPackage.LoadPackageParam param) {
        /* Hook the invoke method of retrofit to hook into the wanted method before it actually
         * gets executed. */
        XposedHelpers.findAndHookMethod("retrofit.RestAdapter$RestHandler", param.classLoader,
                "invoke", Object.class, Method.class, Object[].class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Map map = (Map) ((Object[]) param.args[2])[1];
                        map.put("max", Settings.getLoadThreadsAmount());
                        XposedBridge.log(Arrays.toString(map.entrySet().toArray()));
                    }
                });
    }
}
