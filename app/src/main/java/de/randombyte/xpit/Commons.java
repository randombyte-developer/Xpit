package de.randombyte.xpit;

import android.content.Context;
import android.view.View;
import android.webkit.WebViewClient;

import java.lang.reflect.Method;

import static de.randombyte.xpit.Helper.findMethod;

/**
 * Multiple used code.
 */
public class Commons {

    public static Method forumPost_toView;

    public static void init() {
        forumPost_toView = findMethod("de.androidpit.io.model.ForumPost", "toView",
                View.class, Context.class, View.OnClickListener.class, WebViewClient.class, boolean.class,
                boolean.class, "de.androidpit.ui.forum.ForumThreadActivity", int.class, int.class, int.class);
    }
}
