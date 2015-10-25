package ru.projects.german.vkplaylister.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.model.Album;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumListLoader extends AsyncTaskLoader<Album.AlbumList> {
    private static final String TAG = AlbumListLoader.class.getSimpleName();

    private static final int ALBUMS_PER_REQUEST = 50;

    private LoadingListener loadingListener;
    private int currentOffset;

    public AlbumListLoader(Context context) {
        super(context);
    }

    @Override
    public Album.AlbumList loadInBackground() {
        Album.AlbumList albums = DataManager.getAlbumList(ALBUMS_PER_REQUEST, currentOffset);
        return albums;
    }

    public void loadMoreAlbums(int offset) {
        Log.d(TAG, "loadMoreAlbums(" + offset + ")");
        currentOffset = offset;
        super.forceLoad();
    }

    @Override
    protected void onStartLoading() {
        loadMoreAlbums(currentOffset);
    }
}
