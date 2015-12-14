package ru.projects.german.vkplaylister.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.adapter.viewholder.BaseAudioViewHolder;
import ru.projects.german.vkplaylister.adapter.viewholder.ProgressViewHolder;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;
import ru.projects.german.vkplaylister.player.PlayerHelper;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public abstract class BaseAudioListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PlayerHelper.PlayerListener {
    private static final String TAG = BaseAudioListAdapter.class.getSimpleName();
    protected static final int AUDIO_TYPE = 1;
    protected static final int LOADING_TYPE = 2;

    protected Audio.AudioList audios = new Audio.AudioList();
    protected Album album = null;

    private Audio currentPlayingAudio = null;

    public void setAlbum(Album album) {
        this.album = album;
    }

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

            Audio playingAudio = TheApp.getPlayerHelper().getCurrentPlayingAudio();
            if (getItem(position).equals(playingAudio)) {
                playButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp);
            } else {
                playButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp);
            }

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentAudioPosition = audios.indexOf(TheApp.getPlayerHelper().getCurrentAudio());
                    if (currentAudioPosition != -1) {
                        if (currentAudioPosition == position) {
                            if (TheApp.getPlayerHelper().isPlaying()) {
                                TheApp.getPlayerHelper().pause();
                            } else {
                                TheApp.getPlayerHelper().resume();
                            }
//                            notifyItemChanged(position);
                        } else {
//                            TheApp.getPlayerHelper().setAlbum(album);
                            TheApp.getPlayerHelper().play(audios, position, album);
//                            notifyItemChanged(position);
                            if (TheApp.getPlayerHelper().isPlaying()) {
//                                notifyItemChanged(currentAudioPosition);
                            }
                        }
                    } else {
//                        TheApp.getPlayerHelper().setAlbum(album);
                        TheApp.getPlayerHelper().play(audios, position, album);
//                        notifyItemChanged(position);
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
        if (audiosToAdd.getTotalCount() > audios.getTotalCount()) {
            audios.setTotalCount(audiosToAdd.getTotalCount());
        }
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

    public Audio.AudioList getAudios() {
        return audios;
    }

    public void clear() {
        int oldSize = audios.size();
        audios.clear();
        notifyItemRangeRemoved(0, oldSize);
    }

    @Override
    public void onPlay(Album album, Audio audio) {
        if (audio == null) {
            return;
        }
        int oldPosition = audios.indexOf(currentPlayingAudio);
        int newPosition = audios.indexOf(audio);
        currentPlayingAudio = audio;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        if (oldPosition != newPosition) {
            if (newPosition != -1) {
                notifyItemChanged(newPosition);
            }
        }
    }

    @Override
    public void onStop(Album album, Audio audio) {
        if (audio == null) {
            return;
        }
        int position = audios.indexOf(audio);
        if (position != -1) {
            notifyItemChanged(position);
        }
    }
}
