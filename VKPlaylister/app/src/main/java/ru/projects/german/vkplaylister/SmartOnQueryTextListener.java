package ru.projects.german.vkplaylister;

import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.util.Log;

/**
 * Created on 26.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class SmartOnQueryTextListener implements SearchView.OnQueryTextListener {
    private static final String TAG = SmartOnQueryTextListener.class.getSimpleName();
    private static final long DEFAULT_DELAY_BEFORE_LOADING = 2_000;
    private static final int DEFAULT_THRESHOLD = 3;

    public interface OnReadyListener {
        void onReady(String text);
    }

    private Handler handler = new Handler();
    private Runnable onReadyRunnable;

    private int threshold = DEFAULT_THRESHOLD;
    private long delayBeforeLoading = DEFAULT_DELAY_BEFORE_LOADING;
    private OnReadyListener onReadyListener;

    public SmartOnQueryTextListener(int threshold, long delayBeforeLoading,
                                    OnReadyListener onReadyListener) {
        this.threshold = threshold;
        this.delayBeforeLoading = delayBeforeLoading;
        this.onReadyListener = onReadyListener;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onReadyListener.onReady(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        Log.d(TAG, "Search text listener, onChange(), text=" + newText);
        if (onReadyRunnable != null) {
            handler.removeCallbacks(onReadyRunnable);
        }
        if (newText.length() >= threshold) {
            onReadyRunnable = new Runnable() {
                @Override
                public void run() {
                    onReadyListener.onReady(newText);
                }
            };
            handler.postDelayed(onReadyRunnable, delayBeforeLoading);
        } else if (newText.length() == 0) {
            onReadyListener.onReady(newText);
        }
        return true;
    }
}
