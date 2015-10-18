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

    public AudioListLoader(Context context, final Album album) {
        super(context, new VkRequestResponseHelper<Audio.AudioList>() {
            @Override
            public VKRequest createRequest() {
                VKRequest request = VkHelper.getAudioRequest(
                        album != null ? album.getVkOwnerId() : Integer.parseInt(VKAccessToken.currentToken().userId),
                        album != null ? album.getVkId() : -1,
                        false,
                        0,
                        10
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

    public AudioListLoader(Context context) {
        this(context, null);
    }
//    private static final String TAG = AudioListLoader.class.getSimpleName();
//    private static final int LOADED_DATA_TIME_EXPIRED = 30 * 1000; // 30 seconds
//
//    private static VkAudioArray cachedArray;
//    private static long lastDownloadTime = -1;
//
//    public AudioListLoader(Context context) {
//        super(context);
//    }
//
//    private boolean cacheIsExpired() {
//        if (lastDownloadTime == -1) {
//            return true;
//        }
//        return System.currentTimeMillis() - lastDownloadTime > LOADED_DATA_TIME_EXPIRED;
//    }
//
//    @Override
//    public VkAudioArray loadInBackground() {
//        Log.d(TAG, "loadInBackground");
//        VKParameters params = new VKParameters();
//        params.put(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId);
//        params.put(VKApiConst.COUNT, "10");
//        VKRequest request = new VKRequest("audio.get", params);
//        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                try {
//                    cachedArray = (VkAudioArray) new VkAudioArray().parse(response.json);
//                    lastDownloadTime = System.currentTimeMillis();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        return cachedArray;
//    }
//
//    @Override
//    protected void onStartLoading() {
//        Log.d(TAG, "onStartLoading");
//        if (cachedArray != null) {
//            deliverResult(cachedArray);
//        }
//        if (cachedArray == null || takeContentChanged() || cacheIsExpired()) {
//            forceLoad();
//        }
//    }
}
