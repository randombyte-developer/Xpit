package de.randombyte.xpit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.randombyte.xpit.hooks.ActivatableHook;
import de.randombyte.xpit.hooks.HideSignature;
import de.randombyte.xpit.hooks.HideThreads;
import de.randombyte.xpit.hooks.ShowPostIndex;
import de.randombyte.xpit.hooks.ShowThanksCount;
import de.randombyte.xpit.hooks.ShowThreadAuthorInfo;
import de.randombyte.xpit.hooks.XpitSettings;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Entry point for Xposed.
 */
public class Xpit implements IXposedHookLoadPackage {

    public static final String THIS_PACKAGE_NAME = "de.randombyte.xpit";
    public static final String TARGET_PACKAGE_NAME = "de.androidpit.app";

    public static SharedPreferences TARGET_PREFS;

    public static Context OWN_CONTEXT;
    public static Context TARGET_CONTEXT;

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
        Xpit.OWN_CONTEXT = context.createPackageContext(THIS_PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
        Xpit.TARGET_CONTEXT = context.createPackageContext(TARGET_PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
        //Thanks to theknut https://git.io/vgiku

        Commons.init(loadPackageParam);

        TARGET_PREFS = PreferenceManager.getDefaultSharedPreferences(TARGET_CONTEXT);
        hooks.addAll(Arrays.asList(new ShowThreadAuthorInfo(), new ShowPostIndex(), new ShowThanksCount(),
                new HideSignature()));
        new XpitSettings().init(loadPackageParam, hooks);
        new HideThreads().init(loadPackageParam);
        for (ActivatableHook hook : hooks) {
            hook.init(loadPackageParam);
            hook.readEnabled(TARGET_PREFS);
        }
    }
}