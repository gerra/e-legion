package ru.projects.german.vkplaylister.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.Set;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.viewholder.AlbumViewHolder;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.otto.AlbumCreatedEvent;
import ru.projects.german.vkplaylister.otto.AlbumDeletedEvent;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    private static final String TAG = AlbumListAdapter.class.getSimpleName();

    private Handler recentlyDeletedEraser = new Handler();

    private Album.AlbumList albums = new Album.AlbumList();
    // is created because of after deleting album, vk is still return it to me
    private Set<Album> recentlyDeleted = new HashSet<>();

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
            if (!recentlyDeleted.contains(album) && !albums.contains(album)) {
                newAlbums.add(album);
            }
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
        recentlyDeleted.add(album);
//        recentlyDeletedEraser.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "recently deleted erasing");
//                if (recentlyDeleted != null && album != null) {
//                    recentlyDeleted.remove(album);
//                }
//            }
//        }, 5_000);
        Log.d(TAG, "album deleted: " + album.toString());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onCreateAlbum(AlbumCreatedEvent event) {
        final Album album = event.getAlbum();
        int i = albums.findAlbumPosition(album);
        if (i == -1) {
            albums.add(0, album);
            notifyItemInserted(i);
        }
        Log.d(TAG, "album created: " + album.toString());
    }
}