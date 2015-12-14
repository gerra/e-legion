package ru.projects.german.vkplaylister;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import ru.projects.german.vkplaylister.player.PlayerHelper;

/**
 * Created by root on 14.10.15.
 */
public class TheApp extends Application {
    private static final String TAG = TheApp.class.getSimpleName();

    private static TheApp app;
    private static PlayerHelper playerHelper;
    private static Gson gson;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Log.d(TAG, "VKAccessToken was changed to null");
            } else {
                Log.d(TAG, "VKAccessToken was changed and saved in the sharedPreferences ");
                newToken.save();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        VKSdk.initialize(this);
        vkAccessTokenTracker.startTracking();
        playerHelper = new PlayerHelper(this);
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .serializeNulls()
                    .create();
        }
        return gson;
    }

    public static TheApp getApp() {
        return app;
    }

    public static PlayerHelper getPlayerHelper() {
        return playerHelper;
    }
}
