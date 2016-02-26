package de.randombyte.xpit;

import android.content.Context;
import android.view.View;
import android.webkit.WebViewClient;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Multiple used code.
 */
public class Commons {

    public static Method forumPost_toView;
    public static Method abstractThreadList_retrofitSuccess;

    public static void init(XC_LoadPackage.LoadPackageParam param) {
        forumPost_toView = XposedHelpers.findMethodExact("de.androidpit.io.model.ForumPost",
                param.classLoader, "toView", View.class, Context.class, View.OnClickListener.class,
                WebViewClient.class, boolean.class, boolean.class,
                "de.androidpit.ui.forum.ForumThreadActivity", int.class, int.class, int.class);
         abstractThreadList_retrofitSuccess = XposedHelpers.findMethodExact(
            "de.androidpit.ui.forum.AbstractThreadListFragment$3", param.classLoader, "success",
                 Object.class, "retrofit.client.Response");
    }
}
