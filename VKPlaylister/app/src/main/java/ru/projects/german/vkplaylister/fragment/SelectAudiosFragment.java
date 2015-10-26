package ru.projects.german.vkplaylister.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.SelectAudioAdapter;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 25.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public abstract class SelectAudiosFragment extends BaseAudiosFragment {
    private static final String TAG = CreateAlbumFragment.class.getSimpleName();
    private static final String ALBUM_TITLE_KEY = "ALBUM_TITLE_KEY";
    private static final String ALBUM_VK_ID_KEY = "ALBUM_VK_ID_KEY";

    public static CreateAlbumFragment newInstance(String albumTitle, int albumVkId) {
        CreateAlbumFragment fragment = new CreateAlbumFragment();
        Bundle args = new Bundle();
        args.putString(ALBUM_TITLE_KEY, albumTitle);
        args.putInt(ALBUM_VK_ID_KEY, albumVkId);
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateAlbumFragment newInstance(String albumTitle) {
        return newInstance(albumTitle, -1);
    }

    private FloatingActionButton showSelectedAudiosButton;

    private String getAlbumTitle() {
        return getArguments().getString(ALBUM_TITLE_KEY);
    }

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
        return inflater.inflate(R.layout.fragment_select_audios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showSelectedAudiosButton = (FloatingActionButton) view.findViewById(R.id.show_selected_audios);
    }

    protected abstract void onSelectionFinished(Set<Audio> selectedAudios);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.finish_selection) {
            Set<Audio> selectedAudios = ((SelectAudioAdapter) adapter).getSelectedAudios();
            onSelectionFinished(selectedAudios);
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.select_audios_menu, menu);
    }

    @Override
    public CharSequence getTitle() {
        return getAlbumTitle();
    }
}

