package de.randombyte.xpit.hooks;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Displays the settings for this module in the target app
 */
public class XpitSettings {

    public void init(XC_LoadPackage.LoadPackageParam loadPackageParam, final List<ActivatableHook> hooks) {

        XposedHelpers.findAndHookMethod(PreferenceFragment.class,
                "addPreferencesFromResource", int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (!param.thisObject.getClass().getName().equals("de.androidpit.ui.SettingsFragment")) {
                            return;
                        }
                        PreferenceFragment prefFragment = (PreferenceFragment) param.thisObject;
                        PreferenceScreen rootScreen = prefFragment.getPreferenceManager()
                                .createPreferenceScreen(prefFragment.getActivity());

                        PreferenceCategory category = new PreferenceCategory(prefFragment.getActivity());
                                    /* First add category to hierarchy, then add further preferences
                                    to the category because for adding something to category a preference
                                    manager is required. It will be set when the category itself is
                                    added to hierarchy. */
                        rootScreen.addPreference(category); //First this
                        category.setTitle("Xpit");
                        category.addPreference(createXpitScreen(prefFragment.getActivity(),
                                prefFragment.getPreferenceManager(), hooks)); //Then this
                        prefFragment.setPreferenceScreen(rootScreen);
                    }
                });
    }

    private PreferenceScreen createXpitScreen(Context targetContext, PreferenceManager prefManager, List<ActivatableHook> hooks) {
        PreferenceScreen xpitScreen = prefManager.createPreferenceScreen(targetContext);
        xpitScreen.setTitle("Xpit");

        for (final ActivatableHook hook : hooks) {
            CheckBoxPreference checkPref = createCheckboxPreference(targetContext, hook.title,
                    hook.prefKey, hook.defaultValue);
            checkPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    hook.setEnabled((Boolean) newValue);
                    return true;
                }
            });
            xpitScreen.addPreference(checkPref);
        }

        return xpitScreen;
    }

    private CheckBoxPreference createCheckboxPreference(Context context, String title, String key, boolean defaultValue) {
        CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
        checkBoxPref.setTitle(title);
        checkBoxPref.setKey(key);
        checkBoxPref.setDefaultValue(defaultValue);
        return checkBoxPref;
    }
}
