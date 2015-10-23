package ru.projects.german.vkplaylister.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.viewholder.AlbumViewHolder;
import ru.projects.german.vkplaylister.model.Album;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class LocalAlbumListAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    private static final String TAG = LocalAlbumListAdapter.class.getSimpleName();

    private Album.AlbumList albums = new Album.AlbumList();

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        holder.bindItem(albums.get(position));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void addAlbums(Album.AlbumList albumsToAdd) {
        Log.d(TAG, "attempt to add " + albumsToAdd.size() + " albums");
        Album.AlbumList newAlbumsToAdd = new Album.AlbumList();
        for (Album album : albumsToAdd) {
            if (!albums.hasAlbum(album)) {
                newAlbumsToAdd.add(album);
            }
        }
        Log.d(TAG, "added " + newAlbumsToAdd.size() + " albums");
        int oldSize = albums.size();
        albums.addAll(newAlbumsToAdd);
        notifyItemRangeInserted(oldSize, albums.size());
    }

    public Album getItem(int position) {
        return albums.get(position);
    }
}
