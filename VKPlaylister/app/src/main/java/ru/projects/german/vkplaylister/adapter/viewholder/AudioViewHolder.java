package ru.projects.german.vkplaylister.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created by root on 15.10.15.
 */
public class AudioViewHolder extends BinderViewHolder<Audio> {
    private TextView artist;
    private TextView title;

    public AudioViewHolder(View itemView) {
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
