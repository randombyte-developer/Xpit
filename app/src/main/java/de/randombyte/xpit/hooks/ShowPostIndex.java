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
public class ShowPostIndex extends HookProvider {

    @Override
    public void initHooks(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super.initHooks(loadPackageParam);
        Commons.forumPost_toView.hook(new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int indexInThread = XposedHelpers.getIntField(param.thisObject, "iit");
                if (indexInThread == 0) {
                    return; //because it is the first post, it would have the index 0 which is strange
                }

                View resultView = (View) param.getResult();
                Object viewHolder = resultView.getTag();
                TextView authorTextView = (TextView) XposedHelpers.getObjectField(viewHolder, "info");
                authorTextView.setText("#" + indexInThread + " " + authorTextView.getText());
            }
        });
    }
}
