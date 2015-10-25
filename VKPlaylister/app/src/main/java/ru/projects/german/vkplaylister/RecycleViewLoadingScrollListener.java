package ru.projects.german.vkplaylister;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created on 25.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class RecycleViewLoadingScrollListener extends RecyclerView.OnScrollListener {
    public interface OnLoadListener {
        void onLoad(int totalCount);
    }

    private int itemThresHold;
    private OnLoadListener onLoadListener;

    public RecycleViewLoadingScrollListener(int itemThreshold, OnLoadListener onLoadListener) {
        this.itemThresHold = itemThreshold;
        this.onLoadListener = onLoadListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy < 0) {
            return;
        }
        int totalCount = recyclerView.getLayoutManager().getItemCount();
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        if (lastVisibleItem + 10 >= totalCount) {
            if (onLoadListener != null) {
                onLoadListener.onLoad(totalCount);
            }
        }
    }
}
