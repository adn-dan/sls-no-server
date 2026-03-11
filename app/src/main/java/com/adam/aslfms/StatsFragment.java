package com.adam.aslfms;

import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adam.aslfms.util.ScrobblesDatabase;
import com.adam.aslfms.util.StatisticsData;
import com.adam.aslfms.util.TopItem;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsFragment extends Fragment {

    private static final long PERIOD_ALL = 0L;
    private static final long PERIOD_30D = 30 * 86400L;
    private static final long PERIOD_7D  = 7 * 86400L;
    private static final long PERIOD_24H = 86400L;

    private ScrobblesDatabase mDb;
    private ExecutorService mExecutor;

    private TextView mTextTotalScrobbles;
    private TextView mTextUniqueArtists;
    private TextView mTextUniqueTracks;
    private TextView mTextListeningTime;
    private LinearLayout mListTopArtists;
    private LinearLayout mListTopTracks;
    private LinearLayout mListTopAlbums;

    private long mCurrentSince = PERIOD_ALL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExecutor = Executors.newSingleThreadExecutor();
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
        View root = inflater.inflate(R.layout.fragment_stats, container, false);

        mTextTotalScrobbles = root.findViewById(R.id.text_total_scrobbles);
        mTextUniqueArtists  = root.findViewById(R.id.text_unique_artists);
        mTextUniqueTracks   = root.findViewById(R.id.text_unique_tracks);
        mTextListeningTime  = root.findViewById(R.id.text_listening_time);
        mListTopArtists     = root.findViewById(R.id.list_top_artists);
        mListTopTracks      = root.findViewById(R.id.list_top_tracks);
        mListTopAlbums      = root.findViewById(R.id.list_top_albums);

        MaterialButtonToggleGroup toggle = root.findViewById(R.id.toggle_period);
        toggle.check(R.id.btn_all_time);
        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            long now = System.currentTimeMillis() / 1000;
            if (checkedId == R.id.btn_all_time) mCurrentSince = PERIOD_ALL;
            else if (checkedId == R.id.btn_30d)  mCurrentSince = now - PERIOD_30D;
            else if (checkedId == R.id.btn_7d)   mCurrentSince = now - PERIOD_7D;
            else if (checkedId == R.id.btn_24h)  mCurrentSince = now - PERIOD_24H;
            loadStats();
        });

        loadStats();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExecutor.shutdown();
        if (mDb != null) mDb.close();
    }

    private void loadStats() {
        if (mDb == null) return;
        final long since = mCurrentSince;
        mExecutor.execute(() -> {
            StatisticsData data = mDb.loadStatistics(since);
            requireActivity().runOnUiThread(() -> populateViews(data));
        });
    }

    private void populateViews(StatisticsData data) {
        if (!isAdded()) return;

        mTextTotalScrobbles.setText(formatCount(data.totalScrobbles));
        mTextUniqueArtists.setText(formatCount(data.uniqueArtists));
        mTextUniqueTracks.setText(formatCount(data.uniqueTracks));
        mTextListeningTime.setText(formatDuration(data.totalListeningTimeSeconds));

        populateTopList(mListTopArtists, data.topArtists);
        populateTopList(mListTopTracks, data.topTracks);
        populateTopList(mListTopAlbums, data.topAlbums);
    }

    private void populateTopList(LinearLayout container, List<TopItem> items) {
        container.removeAllViews();
        if (items == null || items.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText(R.string.stats_empty);
            empty.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
            container.addView(empty);
            return;
        }

        int maxCount = items.get(0).count;
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < items.size(); i++) {
            TopItem item = items.get(i);
            View row = inflater.inflate(R.layout.item_top_entry, container, false);

            TextView rank  = row.findViewById(R.id.text_rank);
            TextView name  = row.findViewById(R.id.text_name);
            TextView count = row.findViewById(R.id.text_count);
            LinearProgressIndicator bar = row.findViewById(R.id.progress_bar);

            rank.setText(String.valueOf(i + 1));
            name.setText(item.name);
            count.setText(getString(R.string.stats_plays) + " " + formatCount(item.count));
            bar.setMax(maxCount > 0 ? maxCount : 1);
            bar.setProgressCompat(item.count, false);

            container.addView(row);
        }
    }

    private String formatCount(int count) {
        if (count >= 1_000_000) return String.format(Locale.getDefault(), "%.1fM", count / 1_000_000f);
        if (count >= 1_000) return String.format(Locale.getDefault(), "%.1fK", count / 1_000f);
        return String.valueOf(count);
    }

    private String formatDuration(long seconds) {
        if (seconds <= 0) return "—";
        long days  = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long mins  = (seconds % 3600) / 60;
        if (days > 0) return getString(R.string.stats_days_format, (int) days, (int) hours);
        return getString(R.string.stats_hours_format, (int) hours, (int) mins);
    }
}
