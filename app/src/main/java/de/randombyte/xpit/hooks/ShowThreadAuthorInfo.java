package de.randombyte.xpit.hooks;

import android.content.Context;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.TextView;

import de.randombyte.xpit.Constants;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.randombyte.xpit.Constants.Classes;

/**
 * Adds a prefix before the author name of a post.
 */
public class ShowThreadAuthorInfo extends HookProvider {

    private static final String AUTHOR_NAME_PREFIX = "[TE] "; //German for "Thread creator"

    private int currentlyShownThreadAuthorId = -1;

    public ShowThreadAuthorInfo(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super(loadPackageParam);
    }

    @Override
    public void initHooks() {

        findMethod(Constants.Classes.FORUM_THREAD_ACTIVITY_LOAD_POSTS_CALLBACK, "success",
                classes(Constants.Classes.FORUM_POSTS_THREAD_RESPONSE, Constants.Classes.RETROFIT_RESPONSE))
                .hook(new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object thread = XposedHelpers.getObjectField(param.args[0], "thread");
                        currentlyShownThreadAuthorId = XposedHelpers.getIntField(thread, "authorId");
                    }
                });

        findMethod(Classes.FORUM_POST, "toView",
                classes(View.class, Context.class, View.OnClickListener.class, WebViewClient.class,
                        boolean.class, boolean.class, Classes.FORUM_THREAD_ACTIVITY, int.class, int.class, int.class))
                .hook(new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        int postAuthorId = XposedHelpers.getIntField(param.thisObject, "authorId");
                        if (postAuthorId == currentlyShownThreadAuthorId) {
                            View resultView = (View) param.getResult();
                            Object viewHolder = resultView.getTag();
                            TextView authorTextView = (TextView) XposedHelpers.getObjectField(viewHolder, "author");
                            authorTextView.setText(AUTHOR_NAME_PREFIX + authorTextView.getText());
                        }
                    }
                });
    }
}
