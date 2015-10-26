package ru.projects.german.vkplaylister.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
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
import ru.projects.german.vkplaylister.SmartOnQueryTextListener;
import ru.projects.german.vkplaylister.adapter.SelectAudioAdapter;
import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.fragment.dialog.ProgressDialogFragment;
import ru.projects.german.vkplaylister.loader.ModernAudiosLoader;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;
import ru.projects.german.vkplaylister.otto.AlbumCreatedEvent;
import ru.projects.german.vkplaylister.otto.AlbumDeletedEvent;
import ru.projects.german.vkplaylister.otto.Otto;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class CreateAlbumFragment extends SelectAudiosFragment implements OnBackPressedListener {
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

    private Album createdAlbum;

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
        createdAlbum = new Album(getAlbumTitle());
        createdAlbum.setSynchronizedWithVk(true);
        createdAlbum.setVkId(getArguments().getInt(ALBUM_VK_ID_KEY));
        createdAlbum.setVkOwnerId(Integer.parseInt(VKAccessToken.currentToken().userId));
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

    @Override
    protected void onSelectionFinished(Set<Audio> selectedAudios) {
        for (Audio audio : selectedAudios) {
            createdAlbum.addAudio(audio);
        }
        createdAlbum.getAudios().setTotalCount(selectedAudios.size());
        createdAlbum.setTotalCount(selectedAudios.size());

        final ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(
                getResources().getString(R.string.dialog_wait_title),
                getResources().getString(R.string.add_audios_to_album_wait_message, createdAlbum.getTitle()));
        progressDialog.show(getFragmentManager(), ProgressDialogFragment.TAG);
        DataManager.loadAlbumToNet(createdAlbum, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                Log.d(TAG, "Album was loaded, " + response.json.toString());
                Otto.post(new AlbumCreatedEvent(createdAlbum));
                getFragmentManager()
                        .beginTransaction()
                        .remove(getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG))
                        .commit();
                getMainActivity().openFragment(AlbumFragment.newInstance(createdAlbum, true));
            }

            @Override
            public void onError(VKError error) {
                Log.e(TAG, error.toString());
                getFragmentManager()
                        .beginTransaction()
                        .remove(getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG))
                        .commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.finish_selection) {
            Set<Audio> selectedAudios = ((SelectAudioAdapter) adapter).getSelectedAudios();
            onSelectionFinished(selectedAudios);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchAudios(String searchQuery) {
        ModernAudiosLoader loader = (ModernAudiosLoader)
                getLoaderManager().<Audio.AudioList>getLoader(R.id.audios_loader);
        if (loader != null) {
            adapter.clear();
            loader.setLoadType(ModernAudiosLoader.LoadType.SEARCH);
            loader.setSearchQuery(searchQuery);
            loader.loadMoreAudios(0);
        }
    }

    private void loadMyAudios() {
        ModernAudiosLoader loader = (ModernAudiosLoader)
                getLoaderManager().<Audio.AudioList>getLoader(R.id.audios_loader);
        if (loader != null) {
            adapter.clear();
            loader.setLoadType(ModernAudiosLoader.LoadType.BY_ALBUM);
            loader.loadMoreAudios(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.select_audios_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.search_audios);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SmartOnQueryTextListener(3, 1000, new SmartOnQueryTextListener.OnReadyListener() {
            @Override
            public void onReady(String text) {
                Log.d(TAG, "Search text listener, onReady(), text=" + text);
                if (text.length() > 0) {
                    searchAudios(text);
                } else {
                    loadMyAudios();
                }
            }
        }));
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    @Override
    public CharSequence getTitle() {
        return getAlbumTitle();
    }

    @Override
    public void onBackPressed() {
        ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(
                getResources().getString(R.string.dialog_wait_title),
                getResources().getString(R.string.remove_album_wait_message, createdAlbum.getTitle())
        );
        progressDialog.show(getFragmentManager(), ProgressDialogFragment.TAG);
        DataManager.removeAlbumFromNet(createdAlbum, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                Otto.post(new AlbumDeletedEvent(createdAlbum));
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG))
                        .commit();
                getMainActivity().closeCurrentFragment();
            }

            @Override
            public void onError(VKError error) {
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG))
                        .commit();
                getMainActivity().closeCurrentFragment();
            }
        });
    }
}
