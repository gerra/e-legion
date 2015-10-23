package ru.projects.german.vkplaylister.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public abstract class BaseAudioViewHolder extends BinderViewHolder<Audio> {
    protected TextView artist;
    protected TextView title;

    public BaseAudioViewHolder(View itemView) {
        super(itemView);
        artist = (TextView) itemView.findViewById(R.id.artist);
        title = (TextView) itemView.findViewById(R.id.title);
    }

    @Override
    public void bindItem(Audio item) {
        artist.setText(item.getArtist());
        title.setText(item.getTitle());
    }
}
