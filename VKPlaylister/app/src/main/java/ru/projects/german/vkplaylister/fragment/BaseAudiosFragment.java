package ru.projects.german.vkplaylister.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.activity.MainActivity;
import ru.projects.german.vkplaylister.adapter.BaseAudioListAdapter;
import ru.projects.german.vkplaylister.adapter.RecyclerItemClickListener;
import ru.projects.german.vkplaylister.loader.AudioListLoader;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public abstract class BaseAudiosFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Audio.AudioList>, HasTitle {
    private static final String TAG = BaseAudiosFragment.class.getSimpleName();
    protected static final String ALBUM_KEY = "ALBUM_KEY";

    protected RecyclerView audioList;
    protected BaseAudioListAdapter adapter;
    protected RecyclerItemClickListener onItemClickListener;

    protected abstract void initAdapter();
    protected abstract void initOnItemClickListener();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (adapter == null) {
            initAdapter();
        }
        if (onItemClickListener == null) {
            initOnItemClickListener();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "AudioListLoader in lm: " + (getLoaderManager().getLoader(R.id.audios_loader) != null));
        Bundle args = getArguments();
        Album album = null;
        if (args != null) {
            album = (Album) args.getSerializable(ALBUM_KEY);
        }
        if (album == null || album.isSynchronizedWithVk()) {
            getLoaderManager().initLoader(R.id.audios_loader, getArguments(), this);
        } else {
            updateAudiosInAdapter(album.getAudios());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioList = (RecyclerView) view.findViewById(R.id.audio_list);
        audioList.setLayoutManager(new LinearLayoutManager(getActivity()));
        audioList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onItemClickListener != null) {
            audioList.addOnItemTouchListener(onItemClickListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (onItemClickListener != null) {
            audioList.removeOnItemTouchListener(onItemClickListener);
        }
    }

    private void updateAudiosInAdapter(Audio.AudioList audios) {
        Log.d(TAG, "updateAudiosInAdapter()");
        adapter.addAudios(audios);
    }

    @Override
    public Loader<Audio.AudioList> onCreateLoader(int id, Bundle args) {
        if (id == R.id.audios_loader) {
            Log.d(TAG, "onCreateLoader");
            Album album = null;
            if (args != null) {
                album = (Album) args.getSerializable(ALBUM_KEY);
            }
            return new AudioListLoader(TheApp.getApp(), album, 100, 0);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Audio.AudioList> loader, Audio.AudioList data) {
        if (data != null) {
            Log.d(TAG, "Audios were gotten, size = " + data.size());
            updateAudiosInAdapter(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Audio.AudioList> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
