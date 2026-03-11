package com.adam.aslfms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adam.aslfms.service.ScrobblingService;
import com.adam.aslfms.util.AppSettings;
import com.adam.aslfms.util.ScrobblesDatabase;
import com.adam.aslfms.util.Track;
import com.adam.aslfms.util.Util;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    private ScrobblesDatabase mDb;
    private AppSettings mSettings;

    private TextView mTextTrack;
    private TextView mTextArtist;
    private TextView mTextAlbum;
    private TextView mTextCacheCount;
    private MaterialButton mBtnScrobbleNow;
    private MaterialButton mBtnViewCache;
    private MaterialButton mBtnHeart;
    private MaterialButton mBtnCopy;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = new AppSettings(requireContext());
        mDb = new ScrobblesDatabase(requireContext());
        try {
            mDb.open();
        } catch (SQLException e) {
            mDb = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mTextTrack      = root.findViewById(R.id.text_track);
        mTextArtist     = root.findViewById(R.id.text_artist);
        mTextAlbum      = root.findViewById(R.id.text_album);
        mTextCacheCount = root.findViewById(R.id.text_cache_count);
        mBtnScrobbleNow = root.findViewById(R.id.btn_scrobble_now);
        mBtnViewCache   = root.findViewById(R.id.btn_view_cache);
        mBtnHeart       = root.findViewById(R.id.btn_heart);
        mBtnCopy        = root.findViewById(R.id.btn_copy);

        mBtnScrobbleNow.setOnClickListener(v -> {
            int numInCache = mDb != null ? mDb.queryNumberOfUnscrobbledTracks() : 0;
            Util.scrobbleAllIfPossible(requireContext(), numInCache);
        });

        mBtnViewCache.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), ViewScrobbleCacheActivity.class);
            i.putExtra("viewall", true);
            startActivity(i);
        });

        mBtnHeart.setOnClickListener(v -> Util.heartIfPossible(requireContext()));
        mBtnCopy.setOnClickListener(v -> Util.copyIfPossible(requireContext()));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter ifs = new IntentFilter();
        ifs.addAction(ScrobblingService.BROADCAST_ONSTATUSCHANGED);
        requireActivity().registerReceiver(mStatusReceiver, ifs);
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(mStatusReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDb != null) mDb.close();
    }

    private void updateUI() {
        if (mDb == null) return;

        // Show the most recently scrobbled track
        Track recent = mDb.fetchRecentTrack();
        if (recent != null) {
            mTextTrack.setText(recent.getTrack());
            mTextArtist.setText(recent.getArtist());
            mTextArtist.setVisibility(View.VISIBLE);
            if (!recent.getAlbum().isEmpty()) {
                mTextAlbum.setText(recent.getAlbum());
                mTextAlbum.setVisibility(View.VISIBLE);
            } else {
                mTextAlbum.setVisibility(View.GONE);
            }
        } else {
            mTextTrack.setText(R.string.home_nothing_playing);
            mTextArtist.setVisibility(View.GONE);
            mTextAlbum.setVisibility(View.GONE);
        }

        // Cache count
        int numCache = mDb.queryNumberOfUnscrobbledTracks();
        mTextCacheCount.setText(getString(R.string.home_cache_count, numCache));
        mBtnScrobbleNow.setEnabled(numCache > 0);
    }

    private final BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };
}
