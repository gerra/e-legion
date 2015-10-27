package ru.projects.german.vkplaylister.otto;

import ru.projects.german.vkplaylister.model.Album;

/**
 * Created on 25.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumDeletedEvent {
    private Album album;

    public AlbumDeletedEvent(Album album) {
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }
}
