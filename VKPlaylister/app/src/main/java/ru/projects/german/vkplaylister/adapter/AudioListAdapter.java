package ru.projects.german.vkplaylister.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.viewholder.AudioViewHolder;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created by root on 15.10.15.
 */
public class AudioListAdapter extends RecyclerView.Adapter<AudioViewHolder> {
    private static final String TAG = AudioListAdapter.class.getSimpleName();

    private Audio.AudioList audios = new Audio.AudioList();

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AudioViewHolder holder, int position) {
        holder.bindItem(audios.get(position));
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    public void addAudios(Audio.AudioList audiosToAdd) {
        Log.d(TAG, "attempt to add " + audiosToAdd.size() + " audios");
        Audio.AudioList newAudiosToAdd = new Audio.AudioList();
        for (Audio audioToAdd : audiosToAdd) {
            boolean isNew = true;
            for (Audio audio : audios) {
                if (audio.getId() == audioToAdd.getId()) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                newAudiosToAdd.add(audioToAdd);
            }
        }
        Log.d(TAG, "added " + newAudiosToAdd.size() + " audios");
        int oldSize = audios.size();
        audios.addAll(newAudiosToAdd);
        notifyItemRangeInserted(oldSize, audios.size());
    }

    public Audio getItem(int position) {
        return audios.get(position);
    }
}
