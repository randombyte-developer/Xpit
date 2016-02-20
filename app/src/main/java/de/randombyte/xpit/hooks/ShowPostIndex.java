package de.randombyte.xpit.hooks;

import android.view.View;
import android.widget.TextView;

import de.randombyte.xpit.Commons;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Shows the post index in a thread.
 */
public class ShowPostIndex extends ActivatableHook {

    public ShowPostIndex() {
        super("postIndex", "Post-Nummer", true);
    }

    @Override
    public void init(XC_LoadPackage.LoadPackageParam param) {
        registerHook(Commons.forumPost_toView, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int indexInThread = XposedHelpers.getIntField(param.thisObject, "iit");
                if (indexInThread == 0) {
                    return; //because it is the first post(index is 0), don't show index
                }

                View resultView = (View) param.getResult();
                Object viewHolder = resultView.getTag();
                TextView authorTextView = (TextView) XposedHelpers.getObjectField(viewHolder, "info");
                authorTextView.setText("#" + indexInThread + " " + authorTextView.getText());
            }
        });
    }
}
