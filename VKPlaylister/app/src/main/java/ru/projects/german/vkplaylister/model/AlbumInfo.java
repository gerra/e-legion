package ru.projects.german.vkplaylister.model;

import java.io.Serializable;

/**
 * Created on 18.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumInfo implements Serializable {
    private String title;
    private int size;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVkId() {
        return vkId;
    }

    public void setVkId(int vkId) {
        this.vkId = vkId;
    }

    public int getVkOwnerId() {
        return vkOwnerId;
    }

    public void setVkOwnerId(int vkOwnerId) {
        this.vkOwnerId = vkOwnerId;
    }

    public int getSize() {
        return size;
    }
}
