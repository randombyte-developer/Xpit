package de.randombyte.xpit;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.randombyte.xpit.hooks.HideThreads;
import de.randombyte.xpit.hooks.LoadThreadsAmount;

/**
 * Helper class for managing the settings.
 */
public class Settings {

    public static SharedPreferences TARGET_PREFS;

    /**
     * Returns the internal representation of the hidden threads in the original order.
     */
    public static String[] getHiddenThreadsString() {
        Set<String> idsString = TARGET_PREFS
                .getStringSet(HideThreads.HIDDEN_THREADS_PREF_KEY, new HashSet<String>());
        return idsString.toArray(new String[idsString.size()]);
    }

    /**
     * Returns only the titles remaining the original order.
     */
    public static String[] getHiddenThreadsTitles() {
        String[] strings = getHiddenThreadsString();
        String[] titles = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            titles[i] = strings[i].split(";", 2)[1];
        }
        return titles;
    }

    /**
     * Returns the threads that are marked by the user as hidden from the SharedPreferences.
     * NOTE: The order of course isn't as in the SharedPreferences.
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

    public static String getLoadThreadsAmount() {
        return TARGET_PREFS.getString(LoadThreadsAmount.PREF_KEY, "20");
    }
}
