package com.adam.aslfms;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.Preference;

import androidx.preference.PreferenceFragmentCompat;

import com.adam.aslfms.util.AppSettings;
import com.adam.aslfms.util.ScrobblesDatabase;
import com.adam.aslfms.util.Util;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    private static final String KEY_SCROBBLE_ALL_NOW   = "scrobble_all_now";
    private static final String KEY_VIEW_SCROBBLE_CACHE = "view_scrobble_cache";
    private static final String KEY_HEART_CURRENT_TRACK = "my_heart_button";
    private static final String KEY_COPY_CURRENT_TRACK  = "my_copy_button";

    private ScrobblesDatabase mDb;
    private AppSettings mSettings;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_prefs, rootKey);

        mSettings = new AppSettings(requireContext());
        mDb = new ScrobblesDatabase(requireContext());
        try {
            mDb.open();
        } catch (SQLException e) {
            mDb = null;
        }

        androidx.preference.Preference scrobbleNow = findPreference(KEY_SCROBBLE_ALL_NOW);
        if (scrobbleNow != null) {
            int numCache = mDb != null ? mDb.queryNumberOfUnscrobbledTracks() : 0;
            scrobbleNow.setSummary(getString(R.string.scrobbles_cache).replace("%1", String.valueOf(numCache)));
            scrobbleNow.setEnabled(numCache > 0);
            scrobbleNow.setOnPreferenceClickListener(p -> {
                int n = mDb != null ? mDb.queryNumberOfUnscrobbledTracks() : 0;
                Util.scrobbleAllIfPossible(requireContext(), n);
                return true;
            });
        }

        androidx.preference.Preference viewCache = findPreference(KEY_VIEW_SCROBBLE_CACHE);
        if (viewCache != null) {
            viewCache.setOnPreferenceClickListener(p -> {
                Intent i = new Intent(requireContext(), ViewScrobbleCacheActivity.class);
                i.putExtra("viewall", true);
                startActivity(i);
                return true;
            });
        }

        androidx.preference.Preference heart = findPreference(KEY_HEART_CURRENT_TRACK);
        if (heart != null) {
            heart.setOnPreferenceClickListener(p -> {
                Util.heartIfPossible(requireContext());
                return true;
            });
        }

        androidx.preference.Preference copy = findPreference(KEY_COPY_CURRENT_TRACK);
        if (copy != null) {
            copy.setOnPreferenceClickListener(p -> {
                Util.copyIfPossible(requireContext());
                return true;
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDb != null) mDb.close();
    }
}
