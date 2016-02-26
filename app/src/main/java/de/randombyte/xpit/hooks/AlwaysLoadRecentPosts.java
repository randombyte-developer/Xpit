package de.randombyte.xpit.hooks;

import de.randombyte.xpit.Commons;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Let the app always load recent posts.
 */
public class AlwaysLoadRecentPosts {

    public static void init(XC_LoadPackage.LoadPackageParam param) {
        XposedBridge.hookMethod(Commons.abstractThreadList_retrofitSuccess, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setBooleanField(XposedHelpers.getSurroundingThis(param.thisObject),
                        "mHasMoreResults", true);
            }
        });
    }

}
