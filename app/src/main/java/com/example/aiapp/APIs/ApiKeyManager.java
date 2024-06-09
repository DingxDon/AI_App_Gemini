package com.example.aiapp.APIs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class ApiKeyManager {

    private static final String TAG = "ApiKeyManager";
    private static final String PREF_API_KEY = "api_key_preference";

    public static String getApiKey(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("api_key_preference", Context.MODE_PRIVATE);
        String apiKey = preferences.getString(PREF_API_KEY, "");
        Log.d(TAG, "Retrieved API key from shared preferences: " + apiKey);
        return apiKey;


    }
}