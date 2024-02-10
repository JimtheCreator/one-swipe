package utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void addItem(Context context, String value) {
        List<String> stringArrayList = getStringArray(context);
        stringArrayList.add(value);
        saveStringArray(context, stringArrayList);
    }

    public static void removeStringArray(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_STRING_ARRAY);
        editor.apply();
    }
}
