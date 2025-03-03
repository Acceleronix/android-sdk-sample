package com.acceleronix.app.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    private static final String FILE_NAME = "sp_file";

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit(); 
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE); 
        return sp.getInt(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

}