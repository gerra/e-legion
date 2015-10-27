package ru.projects.german.vkplaylister.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import ru.projects.german.vkplaylister.R;

/**
 * Created on 25.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public ProgressViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
    }
}
