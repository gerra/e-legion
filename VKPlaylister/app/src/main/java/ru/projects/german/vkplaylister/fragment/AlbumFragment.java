package ru.projects.german.vkplaylister.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.SimpleAudioListAdapter;
import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.fragment.dialog.ProgressDialogFragment;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.otto.AlbumDeletedEvent;
import ru.projects.german.vkplaylister.otto.Otto;

/**
 * Created by root on 14.10.15.
 */
public class AlbumFragment extends BaseAudiosFragment {
    private static final String TAG = AlbumFragment.class.getSimpleName();

    public static AlbumFragment newInstance(Album album, boolean isJustCreated) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ALBUM_KEY, album);
        args.putBoolean(ALBUM_IS_JUST_CREATED_KEY, isJustCreated);
        fragment.setArguments(args);
        return fragment;
    }

    public static AlbumFragment newInstance(Album album) {
        return newInstance(album, false);
    }

    @Override
    protected void initAdapter() {
        adapter = new SimpleAudioListAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.album_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.remove_album) {
            final Album albumToDelete = getAlbum();
            if (albumToDelete.isSynchronizedWithVk()) {
                final ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(
                        getString(R.string.dialog_wait_title),
                        getString(R.string.remove_album_wait_message, albumToDelete.getTitle())
                );
                progressDialog.show(getFragmentManager(), ProgressDialogFragment.TAG);
                DataManager.removeAlbumFromNet(albumToDelete, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        getFragmentManager()
                                .beginTransaction()
                                .remove(getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG))
                                .commit();
                        getMainActivity().closeCurrentFragment();
                    }

                    @Override
                    public void onError(VKError error) {
                        if (error != null) {
                            Log.e(TAG, error.toString());
                        }
                        getFragmentManager()
                                .beginTransaction()
                                .remove(getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG))
                                .commit();
                        getMainActivity().closeCurrentFragment();
                    }
                });
            } else {
                getMainActivity().closeCurrentFragment();
            }
            DataManager.removeAlbum(albumToDelete);
            Otto.post(new AlbumDeletedEvent(albumToDelete));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public CharSequence getTitle() {
        Album album = getAlbum();
        return album != null ? album.getTitle() : null;
    }
}
