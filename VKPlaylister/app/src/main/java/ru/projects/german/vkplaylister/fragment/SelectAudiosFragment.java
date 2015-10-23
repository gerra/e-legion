package ru.projects.german.vkplaylister.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.adapter.RecyclerItemClickListener;
import ru.projects.german.vkplaylister.adapter.SelectAudioAdapter;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class SelectAudiosFragment extends BaseAudiosFragment {
    private static final String TAG = SelectAudiosFragment.class.getSimpleName();

    public static Fragment newInstance() {
        SelectAudiosFragment fragment = new SelectAudiosFragment();
        return fragment;
    }

    private FloatingActionButton showSelectedAudiosButton;

    @Override
    protected void initAdapter() {
        adapter = new SelectAudioAdapter(new SelectAudioAdapter.OnSelectItemListener() {
            @Override
            public void onSelect(final View view, int position) {
                Log.d(TAG, adapter.getItem(position).getTitle() + " changed state");
                ((SelectAudioAdapter) adapter).changeSelectStateAtPosition(position);

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_select_audios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showSelectedAudiosButton = (FloatingActionButton) view.findViewById(R.id.show_selected_audios);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_album_menu, menu);

    }

    @Override
    protected void initOnItemClickListener() {
        if (onItemClickListener == null) {
            onItemClickListener = new RecyclerItemClickListener(TheApp.getApp(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position) {
                    Log.d(TAG, adapter.getItem(position).getTitle());
                    return view.getId() == R.id.add_audio;
                }
            });
        }

    }
}
