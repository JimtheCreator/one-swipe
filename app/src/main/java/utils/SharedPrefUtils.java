package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.health.connect.datatypes.AppInfo;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import model.JustInfoApps;

public class SharedPrefUtils {
    private static final String PREF_NAME = "Apps";
    private static final String KEY_STRING_ARRAY = "string_array_key";

    public static void saveStringArray(Context context, List<String> stringArrayList) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(stringArrayList);

        editor.putString(KEY_STRING_ARRAY, json);
        editor.apply();
    }

    public static List<String> getStringArray(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = preferences.getString(KEY_STRING_ARRAY, null);

        if (json == null) {
            return new ArrayList<>(); // Default empty ArrayList if no value is stored
        }

        try {
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            // Handle the exception or log the error
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void removeItem(Context context, String itemToRemove) {
        List<String> stringArrayList = getStringArray(context);
        stringArrayList.remove(itemToRemove);
        saveStringArray(context, stringArrayList);
    }

    public static void removeAppInfo(Context context, JustInfoApps appInfoToRemove) {
        // Retrieve the list of AppInfo objects from SharedPreferences
        List<JustInfoApps> appInfoList = getAppInfoList(context);

        // If the list is not null, iterate over it to find and remove the item
        if (appInfoList != null) {
            Iterator<JustInfoApps> iterator = appInfoList.iterator();
            while (iterator.hasNext()) {
                JustInfoApps storedAppInfo = iterator.next();
                if (storedAppInfo.getPackageName().equals(appInfoToRemove.getPackageName())) {
                    iterator.remove(); // Remove the item from the list
                    break; // Stop iterating once the item is removed
                }
            }

            // Save the updated list back to SharedPreferences
            saveAppInfoList(context, appInfoList);
        }
    }

//    public static void addItem(Context context, String value) {
//        List<String> stringArrayList = getStringArray(context);
//        stringArrayList.add(value);
//        saveStringArray(context, stringArrayList);
//    }

    public static List<JustInfoApps> getAppInfoList(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = preferences.getString(KEY_STRING_ARRAY, null);

        if (json == null) {
            return new ArrayList<>(); // Default empty ArrayList if no value is stored
        }

        try {
            Type type = new TypeToken<List<JustInfoApps>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            // Handle the exception or log the error
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void addItem(Context context, JustInfoApps appInfo) {
        List<JustInfoApps> appInfoList = getAppInfoList(context);
        Collections.reverse(appInfoList);
        appInfoList.add(appInfo);
        saveAppInfoList(context, appInfoList);
    }

    public static void saveAppInfoList(Context context, List<JustInfoApps> appInfoList) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(appInfoList);

        editor.putString(KEY_STRING_ARRAY, json);
        editor.apply();
    }

    public static void removeAll(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_STRING_ARRAY);
        editor.apply();
    }

}
