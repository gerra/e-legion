package ru.projects.german.vkplaylister.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.vk.sdk.VKAccessToken;

import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 25.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class ModernAudiosLoader extends AsyncTaskLoader<Audio.AudioList> {
    private static final String TAG = ModernAudiosLoader.class.getSimpleName();

    public static final int AUDIOS_PER_REQUEST = 50;

    private LoadingHelper loadingHelper = new LoadingHelper(){};

    private Album album;
    private int currentOffset;

    private boolean isRunning;
    private volatile boolean wasStarted;

    public ModernAudiosLoader(Context context, Album album, LoadingHelper loadingHelper) {
        super(context);
        if (loadingHelper != null) {
            this.loadingHelper = loadingHelper;
        }
        this.album = album;
    }

    @Override
    public Audio.AudioList loadInBackground() {
        Log.d(TAG, "loadInBackground()");
        Audio.AudioList audios = DataManager.getAudiosFromNet(
                album != null ? album.getVkOwnerId() : Integer.parseInt(VKAccessToken.currentToken().userId),
                album != null ? album.getVkId() : -1,
                false,
                currentOffset,
                AUDIOS_PER_REQUEST
        );
        wasStarted = true;
        return audios;
    }

    public void loadMoreAudios(int offset) {
        Log.d(TAG, "loadMoreAudios(), isRunning=" + isRunning);
        if (loadingHelper.needLoading()) {
            Log.d(TAG, "loadMoreAudios(), need loading");
            this.currentOffset = offset;
            forceLoad();
        }
    }

    @Override
    public void deliverResult(Audio.AudioList data) {
        Log.d(TAG, "deliverResult()");
        isRunning = false;
        super.deliverResult(data);
    }

    @Override
    protected void onForceLoad() {
        Log.d(TAG, "onForceLoad()");
        loadingHelper.onStartLoading();
        isRunning = true;
        super.onForceLoad();
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading(), isRunning=" + isRunning);
        super.onStartLoading();
        if (!isRunning && (takeContentChanged() || !wasStarted)) {
            loadMoreAudios(currentOffset);
        }
    }

    @Override
    public void onCanceled(Audio.AudioList data) {
        isRunning = false;
        super.onCanceled(data);
    }
}
