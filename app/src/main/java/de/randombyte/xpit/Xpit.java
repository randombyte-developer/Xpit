package de.randombyte.xpit;

import de.randombyte.xpit.hooks.ShowPostIndex;
import de.randombyte.xpit.hooks.ShowThreadAuthorInfo;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Entry point for Xposed.
 */
public class Xpit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage (XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("de.androidpit.app")) {
            Commons.initCommons(loadPackageParam);

            new ShowThreadAuthorInfo(loadPackageParam).initHooks();
            new ShowPostIndex(loadPackageParam).initHooks();
        }
    }

}