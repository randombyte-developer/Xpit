package de.randombyte.xpit.hooks;

import de.randombyte.xpit.Commons;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Hides the signature.
 */
public class HideSignature extends ActivatableHook {

    public HideSignature() {
        super("signature", "Signature ausblenden", false);
    }

    @Override
    public void init(XC_LoadPackage.LoadPackageParam param) {
        registerHook(Commons.forumPost_toView, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setObjectField(param.thisObject, "authorSignature", null);
            }
        });
    }
}
