package de.randombyte.xpit.hooks;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.randombyte.xpit.Settings;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
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

    private PreferenceScreen createXpitScreen(final Context targetContext, final PreferenceManager prefManager, List<ActivatableHook> hooks) {
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

        //Hidden threads list
        MultiSelectListPreference hiddenThreadsPref = new MultiSelectListPreference(targetContext);
        hiddenThreadsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MultiSelectListPreference pref = (MultiSelectListPreference) preference;
                //Prepare for dialog to be shown; doing it here to update values
                Map<Integer, String> hiddenThreads = Settings.getHiddenThreads();
                Integer[] ids = hiddenThreads.keySet().toArray(new Integer[hiddenThreads.keySet().size()]);
                String[] idStrings = new String[ids.length];
                for (int i = 0; i < idStrings.length; i++) {
                    idStrings[i] = ids[i].toString();
                }
                pref.setEntryValues(idStrings);
                pref.setEntries(hiddenThreads.values().toArray(new String[hiddenThreads.size()]));

                return true;
            }
        });
        hiddenThreadsPref.setTitle("Ausgeblendete Threads");

        hiddenThreadsPref.setKey(HideThreads.HIDDEN_THREADS_PREF_KEY);
        hiddenThreadsPref.setPositiveButtonText("Entfernen");
        hiddenThreadsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValues) {
                Set<String> threadsRemoved = (Set<String>) newValues;
                Map<Integer, String> threads = Settings.getHiddenThreads();
                for (String threadTitle : threadsRemoved) {
                    Integer id = Integer.valueOf(threadTitle.split(";")[0]);
                    if (threads.remove(id) == null) {
                        XposedBridge.log(id + " not found in hidden threads prefs! Shouldn't happen!");
                        Toast.makeText(targetContext, "Fehler! Bitte Xposed-Log an Entwickler schicken.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                Settings.setHiddenThreads(threads);

                return false;
            }
        });

        //xpitScreen.addPreference(hiddenThreadsPref);

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
