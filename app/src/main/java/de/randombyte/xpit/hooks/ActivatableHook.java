package de.randombyte.xpit.hooks;

import android.content.SharedPreferences;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * A hook that can be en/disabled on the fly without reboot.
 */
public abstract class ActivatableHook {

    public final String prefKey;
    public final String title;
    public final boolean defaultValue;

    private boolean enabled = false;
    private List<Hook> hooks = new ArrayList<>();
    private List<XC_MethodHook.Unhook> unhooks = new ArrayList<>();

    public ActivatableHook(String prefKey, String title, boolean defaultValue) {
        this.prefKey = prefKey;
        this.title = title;
        this.defaultValue = defaultValue;
        enabled = defaultValue;
    }

    /**
     * Registers a hook. You must call updateHooks() so the changes take affect.
     */
    protected final void registerHook(Method method, XC_MethodHook hook) {
        hooks.add(new Hook(method, hook));
    }

    public final void updateHooks() {
        unhookAll();
        if (enabled) {
            for (int i = 0; i < hooks.size(); i++) {
                Hook hook = hooks.get(i);
                unhooks.add(XposedBridge.hookMethod(hook.method, hook.hook));
            }
        }
    }

    public final void unhookAll() {
        for (XC_MethodHook.Unhook unhook : unhooks) {
            unhook.unhook();
        }
        unhooks.clear();
    }

    /**
     * Sets this hook enabled/disabled. You don't have to call updateHooks().
     */
    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        updateHooks();
    }

    public void readEnabled(SharedPreferences prefs) {
        this.enabled = prefs.getBoolean(prefKey, defaultValue);
        updateHooks();
    }
}
