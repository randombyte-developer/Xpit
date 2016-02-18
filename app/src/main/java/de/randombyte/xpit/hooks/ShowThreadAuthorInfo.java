package de.randombyte.xpit.hooks;

import android.view.View;
import android.widget.TextView;

import de.randombyte.xpit.Commons;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static de.randombyte.xpit.Helper.findMethod;

/**
 * Adds a prefix before the author name of a post.
 */
public class ShowThreadAuthorInfo extends ActivatableHook {

    private static final String AUTHOR_NAME_PREFIX = "[TE] "; // German for "Thread creator"

    private int currentlyShownThreadAuthorId = -1;

    public ShowThreadAuthorInfo() {
        super("threadCreator", "TE-Hinweis", true);

        //Callback for Retrofit
        registerHook(findMethod("de.androidpit.ui.forum.ForumThreadActivity$5", "success",
                "de.androidpit.io.model.ForumPostsThreadResponse", "retrofit.client.Response"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object thread = XposedHelpers.getObjectField(param.args[0], "thread");
                        currentlyShownThreadAuthorId = XposedHelpers.getIntField(thread, "authorId");
                    }
                });

        registerHook(Commons.forumPost_toView, new XC_MethodHook() {
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
