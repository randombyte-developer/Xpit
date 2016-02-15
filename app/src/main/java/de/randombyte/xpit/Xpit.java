package de.randombyte.xpit;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.randombyte.xpit.hooks.HookProvider;
import de.randombyte.xpit.hooks.ShowPostIndex;
import de.randombyte.xpit.hooks.ShowThanksCount;
import de.randombyte.xpit.hooks.ShowThreadAuthorInfo;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Entry point for Xposed.
 */
public class Xpit implements IXposedHookLoadPackage {

    public static final String THIS_PACKAGE = "de.randombyte.xpit";
    public static final String TARGET_PACKAGE_NAME = "de.androidpit.app";

    public static Context ownContext;

    List<HookProvider> hooks = new ArrayList<>(Arrays.asList(new ShowThreadAuthorInfo(),
            new ShowPostIndex(), new ShowThanksCount()));

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals(TARGET_PACKAGE_NAME)) {
            return;
        }

        //Thanks to theknut https://git.io/vgiku
        Object activityThread = XposedHelpers.callStaticMethod(
                XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
        Context context = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
        Xpit.ownContext = context.createPackageContext(THIS_PACKAGE, Context.CONTEXT_IGNORE_SECURITY);
        //Thanks to theknut https://git.io/vgiku

        Commons.initCommons(loadPackageParam);
        for (HookProvider hook : hooks) {
            hook.initHooks(loadPackageParam);
        }
    }
}