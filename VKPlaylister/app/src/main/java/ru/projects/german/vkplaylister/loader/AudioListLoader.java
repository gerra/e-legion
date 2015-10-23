package ru.projects.german.vkplaylister.loader;

import android.content.Context;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import org.json.JSONException;

import ru.projects.german.vkplaylister.VkHelper;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

public class AudioListLoader extends SimpleCacheLoader<Audio.AudioList> {

    public AudioListLoader(Context context, final Album album, final int count, final int offset) {
        super(context, new VkRequestResponseHelper<Audio.AudioList>() {
            @Override
            public VKRequest createRequest() {
                VKRequest request = VkHelper.getAudioRequest(
                        album != null ? album.getVkOwnerId() : Integer.parseInt(VKAccessToken.currentToken().userId),
                        album != null ? album.getVkId() : -1,
                        false,
                        offset,
                        count
                );
                return request;
            }

            @Override
            public Audio.AudioList getObjectFromResponse(VKResponse response) throws JSONException {
                Audio.AudioList audios = new Audio.AudioList();
                VkAudioArray vkAudios = (VkAudioArray) new VkAudioArray().parse(response.json);
                for (VKApiAudio vkAudio : vkAudios) {
                    audios.add(new Audio(vkAudio));
                }
                return audios;
            }
        });
        if (album != null) {
            super.setCachedResult(album.getAudios());
        }
    }

    public AudioListLoader(Context context, final Album album) {
        this(context, album, 0, 0);
    }

    public AudioListLoader(Context context) {
        this(context, null);
    }
}
