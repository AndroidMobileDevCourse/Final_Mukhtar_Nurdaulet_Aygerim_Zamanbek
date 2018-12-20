package kz.iitu.cloudy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import kz.iitu.cloudy.model.User;

/**
 * Created by 1506k on 5/14/18.
 */

public class PreferenceUtils {

    private static final String PREF_NAME = "cloudy_pref";
    private static final String CURRENT_USER = "user";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setCurrentUser(@NonNull Context context, User user) {
        getSharedPreferences(context).edit().putString(CURRENT_USER, new Gson().toJson(user)).apply();
    }

    public static User getCurrentUser(@NonNull Context context) {
        return new Gson()
                .fromJson(getSharedPreferences(context)
                        .getString(CURRENT_USER, null), User.class);
    }

    public static boolean isLoggedIn(@NonNull Context context) {
        return getCurrentUser(context) != null;
    }
}
