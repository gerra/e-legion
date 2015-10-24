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

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.Set;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.adapter.RecyclerItemClickListener;
import ru.projects.german.vkplaylister.adapter.SelectAudioAdapter;
import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class CreateAlbumFragment extends BaseAudiosFragment {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    private void onSelectionFinished(Set<Audio> selectedAudios) {
        final Album createdAlbum = new Album(getAlbumTitle(), new Audio.AudioList(selectedAudios));
        createdAlbum.setSynchronizedWithVk(true);
        createdAlbum.setVkId(getArguments().getInt(ALBUM_VK_ID_KEY));
        createdAlbum.setVkOwnerId(Integer.parseInt(VKAccessToken.currentToken().userId));

        final ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance("1", "2");
        progressDialog.show(getFragmentManager(), ProgressDialogFragment.TAG);
        DataManager.loadAlbumToNet(createdAlbum, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                getMainActivity().openFragment(AudiosFragment.newInstance(createdAlbum));
                progressDialog.dismiss();
            }

            @Override
            public void onError(VKError error) {
                Log.e(TAG, error.toString());
                progressDialog.dismiss();
            }
        });
//        getMainActivity().openFragment(AudiosFragment.newInstance(createdAlbum));
    }

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
