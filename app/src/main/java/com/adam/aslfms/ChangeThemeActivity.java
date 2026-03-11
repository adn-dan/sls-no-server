/**
 * This file is part of Simple Scrobbler.
 */
package com.adam.aslfms;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.adam.aslfms.util.AppSettings;
import com.adam.aslfms.util.MyContextWrapper;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ChangeThemeActivity extends AppCompatActivity {
    private static final String TAG = "ChangeThemeActivity";
    private AppSettings settings;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase));
    }

    @Override
    public Resources.Theme getTheme() {
        settings = new AppSettings(this);
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(settings.getAppTheme(), true);
        return theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_options);
        settings = new AppSettings(this);
        setTheme(settings.getAppTheme());
        setupBrandToggle();
        setupModeToggle();
    }

    private void setupBrandToggle() {
        MaterialButtonToggleGroup brandGroup = findViewById(R.id.toggle_brand);
        int currentTheme = settings.getAppTheme();
        if (currentTheme == R.style.AppThemeLastFm || currentTheme == R.style.AppThemeLastFmDark) {
            brandGroup.check(R.id.lastfm_theme);
        } else if (currentTheme == R.style.AppThemeLibreFm || currentTheme == R.style.AppThemeLibreFmDark) {
            brandGroup.check(R.id.librefm_theme);
        } else if (currentTheme == R.style.AppThemeListenBrainz || currentTheme == R.style.AppThemeListenBrainzDark) {
            brandGroup.check(R.id.listenbrainz_theme);
        } else {
            brandGroup.check(R.id.default_theme);
        }
        brandGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            applyTheme(checkedId);
        });
    }

    private void setupModeToggle() {
        MaterialButtonToggleGroup modeGroup = findViewById(R.id.toggle_mode);
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            modeGroup.check(R.id.mode_dark);
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            modeGroup.check(R.id.mode_light);
        } else {
            modeGroup.check(R.id.mode_system);
        }
        modeGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.mode_dark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (checkedId == R.id.mode_light) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });
    }

    private void applyTheme(int brandId) {
        int themeRes;
        if (brandId == R.id.lastfm_theme) {
            themeRes = R.style.AppThemeLastFm;
        } else if (brandId == R.id.librefm_theme) {
            themeRes = R.style.AppThemeLibreFm;
        } else if (brandId == R.id.listenbrainz_theme) {
            themeRes = R.style.AppThemeListenBrainz;
        } else {
            themeRes = R.style.AppTheme;
        }
        settings.setAppTheme(themeRes);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
