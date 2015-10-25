package ru.projects.german.vkplaylister.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import org.json.JSONException;

import ru.projects.german.vkplaylister.VkHelper;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 25.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class ModernAudioListLoader extends AsyncTaskLoader<Audio.AudioList> {
    private static final int AUDIOS_PER_REQUEST = 50;
    private static final String TAG = ModernAudioListLoader.class.getSimpleName();

    private LoadingListener loadingListener;
    private Album album;
    private int currentOffset;
    private boolean isRunning;

    public ModernAudioListLoader(Context context, Album album, LoadingListener loadingListener) {
        super(context);
        this.loadingListener = loadingListener;
        this.album = album;
    }

    @Override
    public Audio.AudioList loadInBackground() {
        VKRequest request = VkHelper.getAudioRequest(
                album != null ? album.getVkOwnerId() : Integer.parseInt(VKAccessToken.currentToken().userId),
                album != null ? album.getVkId() : -1,
                false,
                currentOffset,
                AUDIOS_PER_REQUEST
        );
        final Audio.AudioList[] result = new Audio.AudioList[1];
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                Audio.AudioList audios = new Audio.AudioList();
                VkAudioArray vkAudios = null;
                try {
                    vkAudios = (VkAudioArray) new VkAudioArray().parse(response.json);
                    for (VKApiAudio vkAudio : vkAudios) {
                        audios.add(new Audio(vkAudio));
                    }
                    result[0] = audios;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                Log.e(TAG, error.toString());
            }
        });
        return result[0];
    }

    public void loadMoreAudios(int offset) {
        // TODO: not to load if offset + items.count > vkCount
        this.currentOffset = offset;
        forceLoad();
    }

    @Override
    public void deliverResult(Audio.AudioList data) {
        isRunning = false;
        super.deliverResult(data);
    }

    @Override
    protected boolean onCancelLoad() {
        isRunning = false;
        return super.onCancelLoad();
    }

    @Override
    protected void onForceLoad() {
        if (loadingListener != null) {
            loadingListener.onStartLoading();
        }
        isRunning = true;
        super.onForceLoad();
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (!isRunning) {
            loadMoreAudios(currentOffset);
        }
    }
}
