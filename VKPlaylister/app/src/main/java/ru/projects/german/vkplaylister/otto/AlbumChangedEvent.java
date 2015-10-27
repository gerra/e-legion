package ru.projects.german.vkplaylister.otto;

import ru.projects.german.vkplaylister.model.Album;

/**
 * Created on 27.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumChangedEvent {
    private Album album;

    public AlbumChangedEvent(Album album) {
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }
}
