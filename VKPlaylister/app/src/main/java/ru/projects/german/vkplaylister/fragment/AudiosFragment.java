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
import ru.projects.german.vkplaylister.adapter.AudioListAdapter;
import ru.projects.german.vkplaylister.adapter.RecyclerItemClickListener;
import ru.projects.german.vkplaylister.loader.AudioListLoader;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created by root on 14.10.15.
 */
public class AudiosFragment extends Fragment implements LoaderManager.LoaderCallbacks<Audio.AudioList> {
    private static final String TAG = AudiosFragment.class.getSimpleName();
    private static final String ALBUM_KEY = "ALBUM_KEY";

    private RecyclerView audioList;
    private AudioListAdapter adapter;
    private RecyclerItemClickListener onItemClickListener;

    public static AudiosFragment newInstance() {
        AudiosFragment fragment = new AudiosFragment();
        return fragment;
    }

    public static AudiosFragment newInstance(Album album) {
        AudiosFragment fragment = new AudiosFragment();
        Bundle args = new Bundle();
        args.putSerializable(ALBUM_KEY, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (adapter == null) {
            adapter = new AudioListAdapter();
        }
        if (onItemClickListener == null) {
            onItemClickListener = new RecyclerItemClickListener(TheApp.getApp(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d(TAG, adapter.getItem(position).getTitle());
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "AudioListLoader in lm: " + (getLoaderManager().getLoader(R.id.audios_loader) != null));
        getLoaderManager().initLoader(R.id.audios_loader, getArguments(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_audios, container, false);
        audioList = (RecyclerView) view.findViewById(R.id.audio_list);
        audioList.setLayoutManager(new LinearLayoutManager(getActivity()));
        audioList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        audioList.addOnItemTouchListener(onItemClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        audioList.removeOnItemTouchListener(onItemClickListener);
    }

    @Override
    public Loader<Audio.AudioList> onCreateLoader(int id, Bundle args) {
        if (id == R.id.audios_loader) {
            Log.d(TAG, "onCreateLoader");
            Album album = null;
            if (args != null) {
                album = (Album) args.getSerializable(ALBUM_KEY);
            }
            return new AudioListLoader(TheApp.getApp(), album);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Audio.AudioList> loader, Audio.AudioList data) {
        if (data != null) {
            Log.d(TAG, "Audios were gotten, size = " + data.size());
            adapter.addAudios(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Audio.AudioList> loader) {
        Log.d(TAG, "onLoaderReset");
    }
}
