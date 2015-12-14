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
public class AlbumsLoader extends AsyncTaskLoader<Album.AlbumList> {
    private static final String TAG = AlbumsLoader.class.getSimpleName();

    private static final int ALBUMS_PER_REQUEST = 50;

    private int currentOffset;
    private volatile boolean wasStarted;

    public AlbumsLoader(Context context) {
        super(context);
    }

    @Override
    public Album.AlbumList loadInBackground() {
        Log.d(TAG, "loadInBackground()");
        Album.AlbumList albums = DataManager.getAlbumListFromNet(ALBUMS_PER_REQUEST, currentOffset);
        wasStarted = true;
        return albums;
    }

    public void loadMoreAlbums(int offset) {
        Log.d(TAG, "loadMoreAlbums(" + offset + ")");
        currentOffset = offset;
        super.forceLoad();
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading()");
        if (!wasStarted || takeContentChanged()) {
            Log.d(TAG, "onStartLoading(), start");
            loadMoreAlbums(currentOffset);
        }
    }
}
