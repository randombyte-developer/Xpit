package de.randombyte.xpit;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.randombyte.xpit.hooks.HideThreads;

/**
 * Helper class for managing the settings.
 */
public class Settings {

    public static SharedPreferences TARGET_PREFS;

    public static String[] getHiddenThreadsString() {
        Set<String> idsString = TARGET_PREFS
                .getStringSet(HideThreads.HIDDEN_THREADS_PREF_KEY, new HashSet<String>());
        return idsString.toArray(new String[idsString.size()]);
    }

    /**
     * Returns the threads that are marked by the user as hidden from the SharedPreferences.
     * @return Returns a map of id and last known title of the threads.
     */
    public static Map<Integer, String> getHiddenThreads() {
        String[] threadStrings = getHiddenThreadsString();
        Map<Integer, String> threads = new HashMap<>(threadStrings.length);
        for (String threadString : threadStrings) {
            String[] pair = threadString.split(";", 2); //Ignore ";"s in title after first ";"
            threads.put(Integer.valueOf(pair[0]), pair[1]);
        }
        return threads;
    }

    public static String[] getHiddenThreadsTitles() {
        Map<Integer, String> hiddenThreads = getHiddenThreads();
        return hiddenThreads.values().toArray(new String[hiddenThreads.size()]);
    }

    public static void setHiddenThreads(Map<Integer, String> threads) {
        Set<String> threadsString = new HashSet<>(threads.size());
        for (Map.Entry<Integer, String> thread : threads.entrySet()) {
            threadsString.add(thread.getKey() + ";" + thread.getValue()); //id;title
        }
        TARGET_PREFS.edit().putStringSet(HideThreads.HIDDEN_THREADS_PREF_KEY, threadsString).apply();
    }

    /**
     * Adds a thread as hidden.
     * @param id The thread id
     * @param title The title of the thread; for user
     * @return True if the id already was mapped.
     */
    public static boolean addHiddenThread(int id, String title) {
        Map<Integer, String> hiddenThreads = getHiddenThreads();
        boolean modified = hiddenThreads.put(id, title) != null;
        setHiddenThreads(hiddenThreads);
        return modified;
    }
}
