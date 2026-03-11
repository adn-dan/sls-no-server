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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adam.aslfms.service.NetApp;
import com.adam.aslfms.service.ScrobblingService;
import com.adam.aslfms.util.AppSettings;
import com.adam.aslfms.util.AuthStatus;
import com.adam.aslfms.util.ScrobblesDatabase;
import com.adam.aslfms.util.Util;
import com.adam.aslfms.util.enums.SubmissionType;

import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment {

    private static final String TAG = "StatusFragment";
    private static final String EXTRA_NETAPP = "StatusFragment.NETAPP";

    private NetApp mNetApp;

    private AppSettings settings;
    private ScrobblesDatabase mDb;

    private int mProfilePageLinkPosition = -1;

    private RecyclerView mRecyclerView;
    private StatusAdapter mAdapter;
    private List<Pair> mItems = new ArrayList<>();

    public static StatusFragment newInstance(int netApp) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_NETAPP, netApp);
        StatusFragment fragment = new StatusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_status, container, false);

        mRecyclerView = rootView.findViewById(R.id.stats_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new StatusAdapter(mItems);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {}

            @Override
            public void onChildViewDetachedFromWindow(View view) {}
        });

        // Profile link click
        mAdapter.setOnItemClickListener((position) -> {
            if (position == mProfilePageLinkPosition
                    && settings.getAuthStatus(mNetApp) == AuthStatus.AUTHSTATUS_OK) {
                String url = mNetApp.getProfileUrl(settings);
                Log.d(TAG, "Clicked link to profile page, opening: " + url);
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
            }
        });

        fillData();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int snapp = (int) getArguments().getSerializable(EXTRA_NETAPP);
        if (snapp < 1) {
            Log.e(TAG, "Got null snetapp");
            getActivity().finish();
        }
        mNetApp = NetApp.fromValue(snapp);
        settings = new AppSettings(getActivity());
        mDb = new ScrobblesDatabase(getActivity());
        mDb.open();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(onChange);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter ifs = new IntentFilter();
        ifs.addAction(ScrobblingService.BROADCAST_ONSTATUSCHANGED);
        ifs.addAction(ScrobblingService.BROADCAST_ONAUTHCHANGED);
        getActivity().registerReceiver(onChange, ifs);
        fillData();
    }

    protected void fillData() {
        mItems.clear();
        int numInCache = mDb.queryNumberOfScrobbles(mNetApp);

        // auth
        Pair auth = new Pair();
        if (settings.getAuthStatus(mNetApp) == AuthStatus.AUTHSTATUS_OK) {
            auth.setKey(getString(R.string.logged_in_just));
            auth.setValue(settings.getUsername(mNetApp));
        } else {
            auth.setKey(Util.getStatusSummary(getContext(), settings, mNetApp));
            auth.setValue(Util.getStatusSummary(getActivity(), settings, mNetApp, false));
        }
        mItems.add(auth);

        // link to profile
        Pair prof_link = new Pair();
        prof_link.setKey(getString(R.string.profile_page));
        if (settings.getAuthStatus(mNetApp) == AuthStatus.AUTHSTATUS_OK) {
            prof_link.setValue(mNetApp.getProfileUrl(settings));
        } else {
            prof_link.setValue(getString(R.string.not_logged_in));
        }
        mItems.add(prof_link);
        mProfilePageLinkPosition = mItems.size() - 1;

        // scrobble
        Pair scrobble = new Pair();
        scrobble.setKey(getSubmissionStatusKey(SubmissionType.SCROBBLE));
        scrobble.setValue(getSubmissionStatusValue(SubmissionType.SCROBBLE));
        mItems.add(scrobble);

        // np
        Pair np = new Pair();
        np.setKey(getSubmissionStatusKey(SubmissionType.NP));
        np.setValue(getSubmissionStatusValue(SubmissionType.NP));
        mItems.add(np);

        // scrobbles in cache
        Pair cache = new Pair();
        cache.setKey(getString(R.string.scrobbles_cache_nonum));
        cache.setValue(Integer.toString(numInCache));
        mItems.add(cache);

        // scrobble stats
        Pair scstats = new Pair();
        scstats.setKey(getString(R.string.stats_scrobbles));
        scstats.setValue(Integer.toString(settings.getNumberOfSubmissions(mNetApp, SubmissionType.SCROBBLE)));
        mItems.add(scstats);

        // np stats
        Pair npstats = new Pair();
        npstats.setKey(getString(R.string.stats_nps));
        npstats.setValue(Integer.toString(settings.getNumberOfSubmissions(mNetApp, SubmissionType.NP)));
        mItems.add(npstats);

        // total scrobbles
        Pair tsStats = new Pair();
        tsStats.setKey(mNetApp.getName());
        tsStats.setValue(settings.getTotalScrobbles(mNetApp));
        mItems.add(tsStats);

        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }

    private String getSubmissionStatusKey(SubmissionType stype) {
        if (settings.wasLastSubmissionSuccessful(mNetApp, stype)) {
            return sGetLastAt(stype);
        } else {
            return sGetLastFailAt(stype);
        }
    }

    private String getSubmissionStatusValue(SubmissionType stype) {
        long time = settings.getLastSubmissionTime(mNetApp, stype);
        String when;
        String what;
        if (time == -1) {
            when = getString(R.string.never);
            what = "";
        } else {
            when = Util.timeFromLocalMillis(getActivity(), time);
            what = "\n" + settings.getLastSubmissionInfo(mNetApp, stype);
        }
        return when + what;
    }

    private String sGetLastAt(SubmissionType stype) {
        if (stype == SubmissionType.SCROBBLE) {
            return getString(R.string.scrobble_last_at);
        } else {
            return getString(R.string.nowplaying_last_at);
        }
    }

    private String sGetLastFailAt(SubmissionType stype) {
        if (stype == SubmissionType.SCROBBLE) {
            return getString(R.string.scrobble_last_fail_at);
        } else {
            return getString(R.string.nowplaying_last_fail_at);
        }
    }

    private BroadcastReceiver onChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String snapp = intent.getStringExtra("netapp");
                if (snapp == null) {
                    Log.e(TAG, "Got null snetapp from broadcast");
                    return;
                }
                NetApp napp = NetApp.valueOf(snapp);
                if (napp == getNetApp()) {
                    StatusFragment.this.fillData();
                }
            }
        }
    };

    protected synchronized NetApp getNetApp() {
        return mNetApp;
    }

    // ---- Pair data model ----

    private static class Pair {
        private String key;
        private String value;

        private Pair() {}

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    // ---- RecyclerView Adapter ----

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    private static class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

        private final List<Pair> mItems;
        private OnItemClickListener mListener;

        StatusAdapter(List<Pair> items) {
            mItems = items;
        }

        void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.status_info_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Pair item = mItems.get(position);
            holder.keyView.setText(item.getKey());
            holder.valueView.setText(item.getValue());
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) mListener.onItemClick(position);
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView keyView;
            TextView valueView;

            ViewHolder(View itemView) {
                super(itemView);
                keyView = itemView.findViewById(R.id.key);
                valueView = itemView.findViewById(R.id.value);
            }
        }
    }
}
