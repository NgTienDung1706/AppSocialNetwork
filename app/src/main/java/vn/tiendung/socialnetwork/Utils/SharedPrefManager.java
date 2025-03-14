package vn.tiendung.socialnetwork.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "APP_PREFS";
    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // Private constructor để tránh tạo nhiều instance
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void saveEmail(String email) {
        editor.putString("EMAIL", email);
        editor.apply();
    }

    public String getEmail() {
        return sharedPreferences.getString("EMAIL", null);
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
