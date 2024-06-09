package Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.aiapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private EditTextPreference apiKeyPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        apiKeyPreference = findPreference("api_key_preference");

        // Listener for preference changes
        if (apiKeyPreference != null) {
            apiKeyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    String newApiKey = (String) newValue;
                    // Store the new API key in SharedPreferences
                    SharedPreferences preferences = getActivity().getSharedPreferences("api_key_preference", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("api_key_preference", newApiKey);
                    editor.apply();
                    return true;
                }
            });
        }
    }
}
