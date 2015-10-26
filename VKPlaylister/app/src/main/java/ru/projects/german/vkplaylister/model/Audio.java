package ru.projects.german.vkplaylister.model;

import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created on 18.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class Audio implements Serializable {
    public static class AudioList extends ArrayList<Audio> implements Serializable {
        /**
         * Count of total items on the server
         * Valid, if instance was created using VkAudioArray
         *
         * Not recommended to set from {@link #Audio(VKApiAudio)}
         * because of server may be did not give us total count
         */
        private int totalCount = -1;

        public AudioList() {
            super();
        }

        public AudioList(Collection<? extends Audio> collection) {
            super(collection);
        }

        public AudioList(VkAudioArray vkAudios) {
            super();
            for (VKApiAudio vkAudio : vkAudios) {
                add(new Audio(vkAudio));
            }
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }

    private int id;
    private int ownerId;

    private String artist;
    private String title;
    private int duration;

    public Audio(VKApiAudio vkAudio) {
        id = vkAudio.id;
        ownerId = vkAudio.owner_id;
        artist = vkAudio.artist;
        title = vkAudio.title;
        duration = vkAudio.duration;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }
}
