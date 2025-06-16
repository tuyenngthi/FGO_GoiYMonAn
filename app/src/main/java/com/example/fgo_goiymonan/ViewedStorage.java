package com.example.fgo_goiymonan;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewedStorage {
    private static final String PREF_NAME = "viewed_pref";
    private static final String KEY_VIEWED_LIST = "viewed_list";

    public static void addViewedRecipe(Context context, Recipe recipe) {
        List<Recipe> list = getViewedList(context);

        for (Recipe r : list) {
            if (r.getId() == recipe.getId()) {
                list.remove(r);
                break;
            }
        }
        list.add(0, recipe); // thêm lên đầu

        if (list.size() > 20) list.remove(list.size() - 1); // giới hạn 20 món

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(KEY_VIEWED_LIST, new Gson().toJson(list)).apply();
    }

    public static List<Recipe> getViewedList(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = pref.getString(KEY_VIEWED_LIST, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Recipe>>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}
