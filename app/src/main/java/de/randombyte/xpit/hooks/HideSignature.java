package de.randombyte.xpit.hooks;

import de.randombyte.xpit.Commons;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Hides the signature.
 */
public class HideSignature extends ActivatableHook {

    public HideSignature() {
        super("signature", "Signature ausblenden", false);

        registerHook(Commons.forumPost_toView, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setObjectField(param.thisObject, "authorSignature", null);
            }
        });
    }
}
