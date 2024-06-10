package Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.aiapp.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    private EditTextPreference apiKeyPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyThemeFromPreference();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        apiKeyPreference = findPreference("api_key_preference");

        // Listener for preference changes
        if (apiKeyPreference != null) {
            apiKeyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String newApiKey = (String) newValue;
                // Store the new API key in SharedPreferences
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
                editor.putString("api_key_preference", newApiKey);
                editor.apply();
                return true;
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("theme_preference")) {
            applyThemeFromPreference();
        }
    }

    private void applyThemeFromPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String themeValue = sharedPreferences.getString("theme_preference", "system");
        switch (themeValue) {
            case "light":
                requireActivity().setTheme(R.style.AppTheme_Light);
                break;
            case "dark":
                requireActivity().setTheme(R.style.AppTheme_Dark);
                break;
            default:
                // For system default, you can leave it as it is
                break;
        }
    }
}
