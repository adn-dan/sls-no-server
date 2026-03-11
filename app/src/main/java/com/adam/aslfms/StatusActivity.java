/**
 * This file is part of Simple Scrobbler.
 * <p>
 * https://github.com/simple-last-fm-scrobbler/sls
 * <p>
 * Copyright 2011 Simple Scrobbler Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.adam.aslfms;

import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.adam.aslfms.service.NetApp;
import com.adam.aslfms.util.AppSettings;
import com.adam.aslfms.util.AuthStatus;
import com.adam.aslfms.util.ScrobblesDatabase;
import com.adam.aslfms.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StatusActivity extends AppCompatActivity {

    private static final int MENU_SCROBBLE_NOW_ID = 0;
    private static final int MENU_VIEW_CACHE_ID = 1;
    private static final int MENU_RESET_STATS_ID = 2;

    private AppSettings settings;
    private ScrobblesDatabase mDb;

    private static final String TAG = "StatusActivity";

    @Override
    public Resources.Theme getTheme() {
        settings = new AppSettings(this);
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(settings.getAppTheme(), true);
        return theme;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_activity);

        settings = new AppSettings(this);
        setTheme(settings.getAppTheme());

        mDb = new ScrobblesDatabase(this);

        try {
            mDb.open();
        } catch (SQLException e) {
            Log.e(TAG, "Cannot open database!");
            Log.e(TAG, e.getMessage());
            mDb = null;
        }

        ViewPager2 viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        if (viewPager != null) {
            TabAdapter adapter = buildAdapter();
            viewPager.setAdapter(adapter);
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> tab.setText(adapter.getTitle(position)))
                    .attach();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDb != null) mDb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.status, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SCROBBLE_NOW_ID:
                int numInCache = mDb != null ? mDb.queryNumberOfUnscrobbledTracks() : 0;
                Util.scrobbleAllIfPossible(this, numInCache);
                return true;
            case R.id.MENU_RESET_STATS_ID:
                for (NetApp napp : NetApp.values()) {
                    settings.clearSubmissionStats(napp);
                }
                this.finish();
                startActivity(getIntent());
                return true;
            case R.id.MENU_VIEW_CACHE_ID:
                Intent i = new Intent(this, ViewScrobbleCacheActivity.class);
                i.putExtra("viewall", true);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TabAdapter buildAdapter() {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());
        for (NetApp napp : NetApp.values()) {
            if (settings.getAuthStatus(napp) != AuthStatus.AUTHSTATUS_NOAUTH) {
                adapter.addFragment(StatusFragment.newInstance(napp.getValue()), napp.getName());
            }
        }
        return adapter;
    }

    static class TabAdapter extends FragmentStateAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mTitles = new ArrayList<>();

        public TabAdapter(FragmentManager fm, androidx.lifecycle.Lifecycle lifecycle) {
            super(fm, lifecycle);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mTitles.add(title);
        }

        public String getTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public Fragment createFragment(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragments.size();
        }
    }
}
