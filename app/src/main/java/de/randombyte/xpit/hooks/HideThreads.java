package de.randombyte.xpit.hooks;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.randombyte.xpit.Settings;
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

    public HideThreads() {
    }

    public void init(final XC_LoadPackage.LoadPackageParam params) {

        //Inflate in options menu
        XposedHelpers.findAndHookMethod("de.androidpit.ui.forum.ForumThreadActivity", params.classLoader,
                "onCreateOptionsMenu", Menu.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object thread = XposedHelpers.getObjectField(param.thisObject, "mThread");
                final Context currentActivityContext = (Context) param.thisObject;
                final int id = XposedHelpers.getIntField(thread, "id");
                final String title = (String) XposedHelpers.getObjectField(thread, "title");
                Menu menu = (Menu) param.args[0];
                menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Thread ausblenden")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (!Settings.addHiddenThread(id, title)) {
                                    Toast.makeText(currentActivityContext, "Wird demnächst ausgeblendet",
                                            Toast.LENGTH_LONG).show();
                                    XposedBridge.log("Aus");
                                } else {
                                    Toast.makeText(currentActivityContext, "Schon ausgeblendet",
                                            Toast.LENGTH_LONG).show();
                                    XposedBridge.log("Scho Aus");
                                }
                                return true;
                            }
                        });
            }
        });

        //Callback for Retrofit
        XposedHelpers.findAndHookMethod("de.androidpit.ui.forum.AbstractThreadListFragment$3",
                params.classLoader, "success", "de.androidpit.io.model.ForumThreadsResponse",
                "retrofit.client.Response", new XC_MethodHook() {
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
                });
    }
}
