package ru.projects.german.vkplaylister.model;

import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import java.util.ArrayList;

/**
 * Created on 18.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class Audio {
    public static class AudioList extends ArrayList<Audio> {
        public AudioList() {
            super();
        }
        public AudioList(VkAudioArray vkAudios) {
            super();
            for (VKApiAudio vkAudio : vkAudios) {
                add(new Audio(vkAudio));
            }
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
