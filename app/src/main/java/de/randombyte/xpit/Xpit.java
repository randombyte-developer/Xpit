package de.randombyte.xpit;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.randombyte.xpit.hooks.ActivatableHook;
import de.randombyte.xpit.hooks.ShowPostIndex;
import de.randombyte.xpit.hooks.ShowThanksCount;
import de.randombyte.xpit.hooks.ShowThreadAuthorInfo;
import de.randombyte.xpit.hooks.XpitSettings;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Entry point for Xposed.
 */
public class Xpit implements IXposedHookLoadPackage {

    public static final String THIS_PACKAGE = "de.randombyte.xpit";
    public static final String TARGET_PACKAGE_NAME = "de.androidpit.app";

    public static Context ownContext;

    private final List<ActivatableHook> hooks = new ArrayList<>();

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

        Helper.classLoader = loadPackageParam.classLoader;

        Commons.init();

        XSharedPreferences prefs = new XSharedPreferences(TARGET_PACKAGE_NAME);
        hooks.addAll(Arrays.asList(new ShowThreadAuthorInfo(), new ShowPostIndex(), new ShowThanksCount()));
        new XpitSettings().init(loadPackageParam, hooks);
        for (ActivatableHook hook : hooks) hook.readEnabled(prefs);
    }
}