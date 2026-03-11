package com.adam.aslfms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adam.aslfms.util.AppSettings;
import com.adam.aslfms.util.MyContextWrapper;
import com.adam.aslfms.util.Util;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_CURRENT_NAV = "current_nav_item";

    private AppSettings mSettings;
    private int mCurrentNavId = R.id.nav_home;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase));
    }

    @Override
    public Resources.Theme getTheme() {
        mSettings = new AppSettings(this);
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(mSettings.getAppTheme(), true);
        return theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = new AppSettings(this);
        setTheme(mSettings.getAppTheme());

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            mCurrentNavId = item.getItemId();
            navigateTo(mCurrentNavId);
            return true;
        });

        if (savedInstanceState != null) {
            mCurrentNavId = savedInstanceState.getInt(KEY_CURRENT_NAV, R.id.nav_home);
        }

        // Load initial fragment
        if (savedInstanceState == null) {
            navigateTo(mCurrentNavId);
        }
        bottomNav.setSelectedItemId(mCurrentNavId);

        runChecks();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_NAV, mCurrentNavId);
    }

    private void navigateTo(int navId) {
        Fragment fragment;
        if (navId == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (navId == R.id.nav_stats) {
            fragment = new StatsFragment();
        } else if (navId == R.id.nav_settings) {
            fragment = new SettingsPreferenceFragment();
        } else {
            fragment = new HomeFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            new AboutDialog(this).show();
            return true;
        } else if (id == R.id.menu_whats_new) {
            new WhatsNewDialog(this).show();
            return true;
        } else if (id == R.id.menu_exit) {
            handleAppExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void runChecks() {
        mSettings = new AppSettings(this);
        int v = Util.getAppVersionCode(this, getPackageName());
        if (mSettings.getWhatsNewViewedVersion() < v) {
            mSettings.setKeyBypassNewPermissions(2);
        }
        if (mSettings.getKeyBypassNewPermissions() == 2) {
            startActivity(new Intent(this, PermissionsActivity.class));
        } else if (mSettings.getWhatsNewViewedVersion() < v) {
            new WhatsNewDialog(this).show();
        }
        Util.runServices(this);
    }

    private void handleAppExit() {
        final Context ctx = this;
        new MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.warning) + "! " +
                        getString(R.string.are_you_sure) + " - " +
                        getString(R.string.warning_will_not_scrobble))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    boolean currentActiveState = mSettings.isActiveAppEnabled(Util.checkPower(ctx));
                    mSettings.setActiveAppEnabled(Util.checkPower(ctx), false);
                    mSettings.setTempExitAppEnabled(Util.checkPower(ctx), true);
                    Util.runServices(ctx);
                    Util.stopAllServices(ctx);
                    mSettings.setActiveAppEnabled(Util.checkPower(ctx), currentActiveState);
                    mSettings.setTempExitAppEnabled(Util.checkPower(ctx), false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAndRemoveTask();
                    }
                    ActivityCompat.finishAffinity(MainActivity.this);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
