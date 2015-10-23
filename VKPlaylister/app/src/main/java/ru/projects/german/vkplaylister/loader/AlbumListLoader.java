package ru.projects.german.vkplaylister.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.model.Album;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumListLoader extends AsyncTaskLoader<Album.AlbumList> {
    public AlbumListLoader(Context context) {
        super(context);
    }

    Album.AlbumList cachedAlbumList;

    @Override
    public Album.AlbumList loadInBackground() {
        return cachedAlbumList = DataManager.getAlbumList();
    }


    @Override
    protected void onStartLoading() {
        if (cachedAlbumList != null) {
            deliverResult(cachedAlbumList);
        }
        if (cachedAlbumList == null || takeContentChanged()) {
            forceLoad();
        }
    }
}
