package ru.projects.german.vkplaylister.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
@Deprecated
public abstract class SimpleCacheLoader<R> extends AsyncTaskLoader<R> {
    public interface VkRequestResponseHelper<R> {
        VKRequest createRequest(R currentData);
        R getObjectFromResponse(VKResponse response) throws JSONException;
    }

    private static final String TAG = SimpleCacheLoader.class.getSimpleName();
    private static final int LOADED_DATA_TIME_EXPIRED = 30 * 1000; // 30 seconds

    private R cachedResult;
    private long lastDownloadTime = -1;
    private LoadingListener loadingListener;
    private boolean isRunning = false;

    public boolean isRunning() {
        return isRunning;
    }

    private VkRequestResponseHelper<R> helper;

    public SimpleCacheLoader(Context context, VkRequestResponseHelper helper, LoadingListener loadingListener) {
        super(context);
        this.helper = helper;
        this.loadingListener = loadingListener;
    }

    private boolean cacheIsExpired() {
        if (lastDownloadTime == -1) {
            return true;
        }
        return System.currentTimeMillis() - lastDownloadTime > LOADED_DATA_TIME_EXPIRED;
    }

    @Override
    public R loadInBackground() {
        Log.d(TAG, "loadInBackground, starting...");
        if (helper == null) {
            Log.d(TAG, "loadInBackground, ends with null");
            return cachedResult;
        } else {
            VKRequest request = helper.createRequest(cachedResult);
            request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    try {
                        cachedResult = helper.getObjectFromResponse(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    lastDownloadTime = System.currentTimeMillis();
                }
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "loadInBackground, ends with some data");
            return cachedResult;
        }
    }

    @Override
    public void deliverResult(R data) {
        super.deliverResult(data);
        isRunning = false;
    }

    @Override
    protected boolean onCancelLoad() {
        isRunning = false;
        return super.onCancelLoad();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        isRunning = true;
        if (loadingListener != null) {
            loadingListener.onStartLoading();
        }
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading " + hashCode());
        if (cachedResult != null) {
            deliverResult(cachedResult);
        }
        if (cachedResult == null || takeContentChanged() || cacheIsExpired()) {
            forceLoad();
        }
    }

    protected void setCachedResult(R cachedResult) {
        this.cachedResult = cachedResult;
    }
}
