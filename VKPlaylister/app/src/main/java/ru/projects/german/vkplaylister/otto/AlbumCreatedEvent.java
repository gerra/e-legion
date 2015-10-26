package ru.projects.german.vkplaylister.otto;

import ru.projects.german.vkplaylister.model.Album;

/**
 * Created on 26.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumCreatedEvent {
    private Album album;

    public AlbumCreatedEvent(Album album) {
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }
}
