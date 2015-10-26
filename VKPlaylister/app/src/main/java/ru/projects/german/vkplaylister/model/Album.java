package ru.projects.german.vkplaylister.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    public static class AlbumList extends ArrayList<Album> {
        public int findAlbumPosition(Album album) {
            for (int i = 0; i < size(); i++) {
                Album cur = get(i);
                if (cur.equals(album)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean hasAlbum(Album album) {
            return findAlbumPosition(album) != -1;
        }
    }

    private String title;
    private int totalCount;
    private Audio.AudioList audios = new Audio.AudioList();

    /**
     * Flag indicates that this album was uploaded in the some album in vk
     */
    private boolean synchronizedWithVk;

    /**
     * id of remote album
     * valid if synchronizedWithVk = true
     */
    private int vkId = -1;

    /**
     * owner id of remote album
     * valid if synchronizedWithVk = true
     */
    private int vkOwnerId = -1;

    public Album(String title) {
        this.title = title;
    }

    public Album(String title, Audio.AudioList audios) {
        this.title = title;
        this.audios = audios;
    }

    public void addAudio(Audio audio) {
        audios.add(audio);
    }

    public String getTitle() {
        return title;
    }

    public void setVkId(int vkId) {
        this.vkId = vkId;
    }

    public int getVkId() {
        return vkId;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getVkOwnerId() {
        return vkOwnerId;
    }

    public void setVkOwnerId(int vkOwnerId) {
        this.vkOwnerId = vkOwnerId;
    }

    public Audio.AudioList getAudios() {
        return audios;
    }

    public void setAudios(Audio.AudioList audios) {
        this.audios = audios;
    }

    public boolean isSynchronizedWithVk() {
        return synchronizedWithVk;
    }

    public void setSynchronizedWithVk(boolean synchronizedWithVk) {
        this.synchronizedWithVk = synchronizedWithVk;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getAvailableCount() {
        return audios.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Album)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Album other = (Album) o;
        if (other.vkId != -1 && vkId != -1) {
            return other.vkId == vkId && other.vkOwnerId == vkOwnerId;
        }
        if (!other.title.equals(title)) {
            return false;
        }
        Log.d("Album", "Hard compare of 2 albums");
        return other.audios.equals(audios);
    }

    public void clear() {
        audios.clear();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Album [")
                .append(String.format("tit"));
        return "Album [title=" + title
                + ",vkId=" + vkId
                + ",vkOwnerId" + vkOwnerId
                + ",audios count=(total=" + totalCount + ",locally=" + audios.size()
                + ")]";
    }
}
