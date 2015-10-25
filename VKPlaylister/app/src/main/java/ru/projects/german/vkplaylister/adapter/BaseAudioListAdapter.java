package ru.projects.german.vkplaylister.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.viewholder.BaseAudioViewHolder;
import ru.projects.german.vkplaylister.adapter.viewholder.ProgressViewHolder;
import ru.projects.german.vkplaylister.model.Audio;
import ru.projects.german.vkplaylister.player.PlayerService;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public abstract class BaseAudioListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = BaseAudioListAdapter.class.getSimpleName();
    protected static final int AUDIO_TYPE = 1;
    protected static final int LOADING_TYPE = 2;

    protected Audio.AudioList audios = new Audio.AudioList();

    private int currentPlayingPosition = -1;
    private boolean currentIsPaused = false;

    protected abstract BaseAudioViewHolder getAudioViewHolder(ViewGroup parent);

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) == null) {
            return LOADING_TYPE;
        } else {
            return AUDIO_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOADING_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            return new ProgressViewHolder(view);
        } else {
            return getAudioViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BaseAudioViewHolder) {
            ((BaseAudioViewHolder) holder).bindItem(getItem(position));
            final ImageView playButton = ((BaseAudioViewHolder) holder).playButton;
            if (currentPlayingPosition == position && !currentIsPaused) {
                playButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp);
            } else {
                playButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp);
            }
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPlayingPosition == -1) {
                        currentPlayingPosition = position;
                        playButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp);
                        PlayerService.startPlaying(audios, currentPlayingPosition);
                    } else {
                        if (currentPlayingPosition == position) {
                            currentIsPaused = !currentIsPaused;
                            playButton.setImageResource(
                                    currentIsPaused ? R.drawable.ic_play_circle_filled_white_48dp : R.drawable.ic_pause_circle_filled_white_48dp);
                        } else {
                            int oldPlayingPosition = currentPlayingPosition;
                            currentPlayingPosition = position;
                            currentIsPaused = false;
                            notifyItemChanged(oldPlayingPosition);
                            playButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp);
                            PlayerService.startPlaying(audios, currentPlayingPosition);
                        }
                    }
                }
            });
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    public void addAudios(Audio.AudioList audiosToAdd) {
        removeLoadingItem();
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
        notifyItemRangeInserted(oldSize, audios.size() - oldSize);
    }

    public void addLoadingItem() {
        if (audios.size() == 0 || audios.get(audios.size() - 1) != null) {
            audios.add(null);
            notifyItemInserted(audios.size() - 1);
        }
    }

    private void removeLoadingItem() {
        if (audios.size() != 0 && audios.get(audios.size() - 1) == null) {
            audios.remove(audios.size() - 1);
            notifyItemRemoved(audios.size());
        }
    }

    public Audio getItem(int position) {
        return audios.get(position);
    }
}
