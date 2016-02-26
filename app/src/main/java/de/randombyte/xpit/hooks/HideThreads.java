package de.randombyte.xpit.hooks;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.randombyte.xpit.Commons;
import de.randombyte.xpit.Settings;
import de.randombyte.xpit.Xpit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Gives the ability to hide a thread via the overflow menu in a thread. Hidden threads can be showed
 * again via the settings.
 */
public class HideThreads {

    public static final String HIDDEN_THREADS_PREF_KEY = "hiddenThreads";

    private static final XC_MethodHook REMOVE_HIDDEN_THREADS_IN_RETROFIT_CALLBACK =  new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            Set<Integer> hiddenThreadIds = Settings.getHiddenThreads().keySet(); //Only ids
            Object[] threads = (Object[]) XposedHelpers.getObjectField(param.args[0], "threads");
            List filteredThreads = new ArrayList(threads.length);
            for (Object thread : threads) {
                int id = XposedHelpers.getIntField(thread, "id");
                if (!hiddenThreadIds.contains(id)) {
                    filteredThreads.add(thread);
                } else {
                    XposedBridge.log("Hiding " + id);
                }
            }

            XposedHelpers.setObjectField(param.args[0], "threads", filteredThreads
                    .toArray((Object[]) Array.newInstance(threads[0].getClass(),
                            filteredThreads.size())));
        }
    };

    public static void init(final XC_LoadPackage.LoadPackageParam params) {

        //Inflate in options menu in thread list
        XposedHelpers.findAndHookMethod("android.widget.PopupMenu", params.classLoader,
                "show", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        PopupMenu popup = (PopupMenu) param.thisObject;
                        final Context context = (Context) XposedHelpers.getObjectField(popup, "mContext");
                        if (!context.getPackageName().equals(Xpit.TARGET_PACKAGE_NAME)) {
                            return;
                        }
                        Class itemListenerOfThreadListAdapter = XposedHelpers.findClass(
                                "de.androidpit.ui.adapters.ForumThreadsArrayAdapter$OverflowMenuItemClickListener",
                                params.classLoader);
                        Object listener = XposedHelpers.getObjectField(popup, "mMenuItemClickListener");
                        if (itemListenerOfThreadListAdapter.isInstance(listener)) {
                            //Found the correct popup
                            View anchorView = (View) XposedHelpers.getObjectField(param.thisObject, "mAnchor");
                            final Object forumThread = anchorView.getTag();

                            popup.getMenu().add("Thread ausblenden").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    addHiddenThread(forumThread, context);
                                    return true;
                                }
                            });
                        }
                    }
                });

        //Inflate in options menu in thread activity
        XposedHelpers.findAndHookMethod("de.androidpit.ui.forum.ForumThreadActivity", params.classLoader,
                "onCreateOptionsMenu", Menu.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final Object thread = XposedHelpers.getObjectField(param.thisObject, "mThread");
                final Context currentActivityContext = (Context) param.thisObject;
                Menu menu = (Menu) param.args[0];
                menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Thread ausblenden")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                addHiddenThread(thread, currentActivityContext);
                                return true;
                            }
                        });
            }
        });

        XposedBridge.hookMethod(Commons.abstractThreadList_retrofitSuccess,
                REMOVE_HIDDEN_THREADS_IN_RETROFIT_CALLBACK);

        XposedHelpers.findAndHookMethod("de.androidpit.ui.forum.ForumBrowserFragment$1", params.classLoader,
                "success", Object.class, "retrofit.client.Response", REMOVE_HIDDEN_THREADS_IN_RETROFIT_CALLBACK);
    }

    private static void addHiddenThread(Object forumThread, Context context) {
        int id = XposedHelpers.getIntField(forumThread, "id");
        String title = (String) XposedHelpers.getObjectField(forumThread, "title");
        if (!Settings.addHiddenThread(id, title)) {
            Toast.makeText(context, "Wird demnächst ausgeblendet", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Schon ausgeblendet", Toast.LENGTH_LONG).show();
        }
    }
}
