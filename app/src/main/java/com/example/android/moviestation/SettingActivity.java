package com.example.android.moviestation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Here we will use PreferenceFragment to allow user to choose between options and save it
 */

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = SettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

    }

    public static class MoviePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        private String defaultSort;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);


            Preference sections = findPreference(getString(R.string.setting_key));
            bindPreferenceSummaryToValue(sections);

        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String prefString = preferences.getString(preference.getKey(), "");
            defaultSort = prefString;
            onPreferenceChange(preference, prefString);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            /*
            if the string changed we update CatalogActivity.sortChanged to True.
             */
            if (!stringValue.equals(defaultSort)) {
                CatalogActivity.setSortChanged(true);
            }
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] lables = listPreference.getEntries();
                    preference.setSummary(lables[prefIndex]);
                }
            }
            return true;
        }


    }
}
