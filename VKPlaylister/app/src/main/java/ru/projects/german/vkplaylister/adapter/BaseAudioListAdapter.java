package ru.projects.german.vkplaylister.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import ru.projects.german.vkplaylister.adapter.viewholder.BaseAudioViewHolder;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public abstract class BaseAudioListAdapter extends RecyclerView.Adapter<BaseAudioViewHolder> {
    private static final String TAG = BaseAudioListAdapter.class.getSimpleName();

    protected Audio.AudioList audios = new Audio.AudioList();

    @Override
    public void onBindViewHolder(BaseAudioViewHolder holder, int position) {
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
