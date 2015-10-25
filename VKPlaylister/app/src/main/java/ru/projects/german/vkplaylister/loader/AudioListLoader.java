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

@Deprecated
public class AudioListLoader extends SimpleCacheLoader<Audio.AudioList> {
    private static final int AUDIOS_PER_REQUEST = 50;

    private int currentOffset = 0;

    public AudioListLoader(Context context, final Album album, LoadingListener listener) {
        super(context, new VkRequestResponseHelper<Audio.AudioList>() {
            @Override
            public VKRequest createRequest(Audio.AudioList audios) {
                VKRequest request = VkHelper.getAudioRequest(
                        album != null ? album.getVkOwnerId() : Integer.parseInt(VKAccessToken.currentToken().userId),
                        album != null ? album.getVkId() : -1,
                        false,
                        0,
                        AUDIOS_PER_REQUEST
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
        }, listener);
        if (album != null) {
            super.setCachedResult(album.getAudios());
        }
    }

    public AudioListLoader(Context context) {
        this(context, null, null);
    }
}
