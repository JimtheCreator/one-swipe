package utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import model.JustInfoApps;
import model.Recents;

public class WebsiteRecentSearch {
    private static final String PREF_NAME = "web_search";
    private static final String KEY_STRING_ARRAY = "key";

    public static void saveStringArray(Context context, List<String> stringArrayList) {
        // Ensure the list size doesn't exceed 5
        if (stringArrayList.size() > 5) {
            stringArrayList = stringArrayList.subList(0, 5);
        }

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

    public static void addItem(Context context, String value) {
        List<String> stringArrayList = getStringArray(context);

        // Add the new item to the beginning of the list
        stringArrayList.add(0, value);

        // If the list has more than 5 items, remove the last one
        if (stringArrayList.size() > 5) {
            stringArrayList.remove(stringArrayList.size() - 1);
        }

        saveStringArray(context, stringArrayList);
    }

    public static void removeStringArray(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_STRING_ARRAY);
        editor.apply();
    }
}
