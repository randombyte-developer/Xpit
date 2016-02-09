package de.randombyte.xpit;

import android.content.Context;
import android.view.View;
import android.webkit.WebViewClient;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Multiple used code.
 */
public class Commons {

    public static ClassAndMethodHelper.HookMethod forumPost_toView;

    public static void initCommons(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        ClassAndMethodHelper helper = new ClassAndMethodHelper(loadPackageParam.classLoader);

        forumPost_toView = helper.findMethod(Constants.Classes.FORUM_POST, "toView", helper.classes(
                View.class, Context.class, View.OnClickListener.class, WebViewClient.class, boolean.class,
                boolean.class, Constants.Classes.FORUM_THREAD_ACTIVITY, int.class, int.class, int.class));
    }
}
