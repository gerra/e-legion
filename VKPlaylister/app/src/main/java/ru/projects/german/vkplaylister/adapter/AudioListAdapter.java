package ru.projects.german.vkplaylister.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.viewholder.AudioViewHolder;

/**
 * Created by root on 15.10.15.
 */
public class AudioListAdapter extends BaseAudioListAdapter {
    private static final String TAG = AudioListAdapter.class.getSimpleName();

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(view);
    }
}
