package utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import model.SelectedItem;

public class WebsiteRecentSearch {
    private static final String PREF_NAME = "web_search";
    private static final String KEY_STRING_ARRAY = "key";

    public static void saveStringArray(Context context, List<SelectedItem> selectedItemList) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(selectedItemList);
        editor.putString(KEY_STRING_ARRAY, json);
        editor.apply();
    }

    public static List<SelectedItem> getStringArray(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(KEY_STRING_ARRAY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<List<SelectedItem>>() {
            }.getType();
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void removeItem(Context context, SelectedItem itemToRemove) {
        List<SelectedItem> selectedItemList = getStringArray(context);
        selectedItemList.remove(itemToRemove);
        saveStringArray(context, selectedItemList);
    }

    public static void addItem(Context context, SelectedItem selectedItem) {
        List<SelectedItem> selectedItemList = getStringArray(context);
        selectedItemList.add(0, selectedItem);
        if (selectedItemList.size() > 5) {
            selectedItemList.remove(selectedItemList.size() - 1);
        }
        saveStringArray(context, selectedItemList);
    }

    public static void removeStringArray(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_STRING_ARRAY);
        editor.apply();
    }

}

