package ru.projects.german.vkplaylister.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;

import ru.projects.german.vkplaylister.R;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class SelectAudioViewHolder extends BaseAudioViewHolder {
    public ImageView addAudioButton;

    public SelectAudioViewHolder(View itemView) {
        super(itemView);
        addAudioButton = (ImageView) itemView.findViewById(R.id.add_audio);
    }
}
