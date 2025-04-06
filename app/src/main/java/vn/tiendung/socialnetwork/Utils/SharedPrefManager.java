package vn.tiendung.socialnetwork.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "APP_PREFS";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_USERNAME = "USERNAME";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_NAME = "NAME"; // Thêm name
    private static final String KEY_AVATAR = "AVATAR"; // Thêm avatar
    private static final String KEY_LOGGED_IN = "LOGGED_IN";

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

    // Lưu thông tin user sau khi đăng nhập
    public void saveUser(String userId, String username, String email, String name, String avatar) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_AVATAR, avatar);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }
    public void saveUser(String userId, String username, String email) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    // Lấy ID của user
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    // Lấy username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // Lấy email
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    // Lấy tên đầy đủ của user
    public String getName() {
        return sharedPreferences.getString(KEY_NAME, null);
    }

    // Lấy avatar
    public String getAvatar() {
        return sharedPreferences.getString(KEY_AVATAR, null);
    }

    // Kiểm tra user đã đăng nhập hay chưa
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    // Xóa dữ liệu khi user đăng xuất
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
