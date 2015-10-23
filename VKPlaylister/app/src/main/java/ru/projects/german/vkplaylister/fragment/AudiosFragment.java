package ru.projects.german.vkplaylister.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.adapter.AudioListAdapter;
import ru.projects.german.vkplaylister.adapter.RecyclerItemClickListener;
import ru.projects.german.vkplaylister.model.Album;

/**
 * Created by root on 14.10.15.
 */
public class AudiosFragment extends BaseAudiosFragment {
    private static final String TAG = AudiosFragment.class.getSimpleName();

    public static AudiosFragment newInstance(Album album) {
        AudiosFragment fragment = new AudiosFragment();
        Bundle args = new Bundle();
        args.putSerializable(ALBUM_KEY, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {
        adapter = new AudioListAdapter();
    }

    @Override
    protected void initOnItemClickListener() {
        onItemClickListener = new RecyclerItemClickListener(TheApp.getApp(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                Log.d(TAG, adapter.getItem(position).getTitle() + ", view id=" + view.getId());
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audios, container, false);
    }
}
