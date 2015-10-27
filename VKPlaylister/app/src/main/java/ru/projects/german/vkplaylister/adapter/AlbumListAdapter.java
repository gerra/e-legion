package ru.projects.german.vkplaylister.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.viewholder.AlbumViewHolder;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.otto.AlbumChangedEvent;
import ru.projects.german.vkplaylister.otto.AlbumCreatedEvent;
import ru.projects.german.vkplaylister.otto.AlbumDeletedEvent;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    private static final String TAG = AlbumListAdapter.class.getSimpleName();

    private Album.AlbumList albums = new Album.AlbumList();

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
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
        Album.AlbumList newAlbums = new Album.AlbumList();
        for (Album album : albumsToAdd) {
            newAlbums.add(album);
        }
        int added = newAlbums.size();
        Log.d(TAG, "added " + added + " albums");
        newAlbums.addAll(albums);
        albums = newAlbums;
        notifyItemRangeInserted(0, added);
    }

    public Album getItem(int position) {
        return albums.get(position);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDeleteAlbum(AlbumDeletedEvent event) {
        final Album album = event.getAlbum();
        int i = albums.findAlbumPosition(album);
        if (i != -1) {
            albums.remove(i);
            notifyItemRemoved(i);
        }
        Log.d(TAG, "album deleted: " + album.toString());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onCreateAlbum(AlbumCreatedEvent event) {
        final Album album = event.getAlbum();
        int i = albums.indexOf(album);
        if (i == -1) {
            albums.add(0, album);
            notifyItemInserted(i);
        }
        Log.d(TAG, "album created: " + album.toString());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAlbumChanged(AlbumChangedEvent event) {
        final Album changedAlbum = event.getAlbum();
        for (int i = 0; i < albums.size(); i++) {
            Album album = albums.get(i);
            if (album.getLocalId() == changedAlbum.getLocalId()) {
                notifyItemChanged(i);
                break;
            }
        }
    }
}