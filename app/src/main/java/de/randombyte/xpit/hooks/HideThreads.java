package de.randombyte.xpit.hooks;

import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.randombyte.xpit.Xpit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.randombyte.xpit.Helper.findMethod;

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
        XposedBridge.hookMethod(findMethod("de.androidpit.ui.forum.ForumThreadActivity", "onCreateOptionsMenu",
                Menu.class), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object thread = XposedHelpers.getObjectField(param.thisObject, "mThread");
                final int id = XposedHelpers.getIntField(thread, "id");
                Menu menu = (Menu) param.args[0];
                menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Thread ausblenden")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (addHiddenThreads(id)) {
                                    Toast.makeText(Xpit.TARGET_CONTEXT, "Wird demnächst ausgeblndet",
                                            Toast.LENGTH_LONG).show();
                                    XposedBridge.log("Aus");
                                } else {
                                    Toast.makeText(Xpit.TARGET_CONTEXT, "Schon ausgeblendet",
                                            Toast.LENGTH_LONG).show();
                                    XposedBridge.log("Scho Aus");
                                }
                                return true;
                            }
                        });
            }
        });

        //Callback for Retrofit
        XposedBridge.hookMethod(findMethod("de.androidpit.ui.forum.AbstractThreadListFragment$3",
                "success", "de.androidpit.io.model.ForumThreadsResponse", "retrofit.client.Response"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Set<String> hiddenIdsString = Xpit.TARGET_PREFS
                                .getStringSet(HIDDEN_THREADS_PREF_KEY, new HashSet<String>());
                        String[] hiddenIdsStringArray = hiddenIdsString.toArray(new String[hiddenIdsString.size()]);
                        List<Integer> hiddenIds = new ArrayList<>(hiddenIdsStringArray.length);
                        for (String idString : hiddenIdsStringArray) {
                            hiddenIds.add(Integer.valueOf(idString));
                        }

                        Object threadsResponse = param.args[0];
                        Object[] threads = (Object[]) XposedHelpers.getObjectField(threadsResponse, "threads");

                        //Find thread id that will definitely be shown
                        int displayedThreadId = -1;
                        for (Object thread : threads) {
                            int id = XposedHelpers.getIntField(thread, "id");
                            if (!hiddenIds.contains(id)) {
                                //Should be shown
                                displayedThreadId = id;
                            }
                        }

                        //Actually hide threads with magic
                        for (int i = 0; i < threads.length; i++) {
                            int id = XposedHelpers.getIntField(threads[i], "id");
                            if (hiddenIds.contains(id)) {
                                XposedBridge.log("Hiding " + id);
                                /* Set id to a thread id that will be shown, so that it will be
                                sorted out */
                                XposedHelpers.setIntField(threads[i], "id", displayedThreadId);
                                /* Move this thread to the end of array so that the right thread will
                                   be sorted out */
                                Object swapTmp = threads[i];
                                threads[i] = threads[threads.length - 1];
                                threads[threads.length - 1] = swapTmp;
                            }
                        }

                        XposedHelpers.setObjectField(threadsResponse, "threads", threads);
                    }
                });
    }

    public boolean addHiddenThreads(int id) {
        Set<String> hiddenThreads = Xpit.TARGET_PREFS.getStringSet(HIDDEN_THREADS_PREF_KEY, new HashSet<String>());
        boolean modified = hiddenThreads.add(String.valueOf(id));
        SharedPreferences.Editor editor = Xpit.TARGET_PREFS.edit();
        editor.putStringSet(HIDDEN_THREADS_PREF_KEY, hiddenThreads);
        editor.apply();
        return modified;
    }
}
