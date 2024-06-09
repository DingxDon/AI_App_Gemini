package Fragments;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.aiapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // Access preferences by their keys
        EditTextPreference signaturePreference = findPreference("signature");
        ListPreference replyPreference = findPreference("reply");
        SwitchPreferenceCompat syncPreference = findPreference("sync");
        SwitchPreferenceCompat attachmentPreference = findPreference("attachment");

        // Example: Set default values
        signaturePreference.setDefaultValue("Your signature here");
        replyPreference.setDefaultValue("reply");

        // Example: Update summary based on preference value
        syncPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean syncEnabled = (boolean) newValue;
            attachmentPreference.setEnabled(syncEnabled);
            return true;
        });

        // Example: Listen for preference changes
        replyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            // Handle preference change
            return true; // Return true to persist the new value, false to discard it
        });
    }
}
