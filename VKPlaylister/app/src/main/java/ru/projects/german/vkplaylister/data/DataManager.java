package ru.projects.german.vkplaylister.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKBatchRequest;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VkAudioArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.projects.german.vkplaylister.Constants;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.VkHelper;
import ru.projects.german.vkplaylister.loader.ModernAudiosLoader;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class DataManager {
    public static abstract class MyRequestListener {
        public void onComplete(Object object) {}
        public void onError(String error) {}
    }

    private static final String TAG = DataManager.class.getSimpleName();

    private static final String PREFERENCES_KEY = "PLAYLISTER_PREFERENCES";
    private static final String ALBUM_COUNT_KEY = "ALBUM_COUNT";
    private static final String ALBUM_KEY = "ALBUM";
    private static final String SYNC_WITH_VK_KEY = "SYNC_WITH_VK";

    private static Audio.AudioList getAudiosByRequest(VKRequest request) {
        final Audio.AudioList[] audios = new Audio.AudioList[1];
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                int totalCount = -1;
                try {
                    VkAudioArray vkAudios = (VkAudioArray) new VkAudioArray().parse(response.json);
                    audios[0] = new Audio.AudioList(vkAudios);
                    totalCount = response.json
                            .getJSONObject("response")
                            .getInt("count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (totalCount != -1) {
                    audios[0].setTotalCount(totalCount);
                }
            }
        });
        return audios[0];
    }

    private static void getAudiosByRequestAsynchronously(VKRequest request, final MyRequestListener listener) {
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                int totalCount = -1;
                Audio.AudioList audios = new Audio.AudioList();
                try {
                    VkAudioArray vkAudios = (VkAudioArray) new VkAudioArray().parse(response.json);
                    audios = new Audio.AudioList(vkAudios);
                    totalCount = response.json
                            .getJSONObject("response")
                            .getInt("count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (totalCount != -1) {
                    audios.setTotalCount(totalCount);
                }
                listener.onComplete(audios);
            }
        });
    }

    public static Audio.AudioList getAudiosFromNet(int ownerId,
                                                int albumId,
                                                boolean needUser,
                                                int offset,
                                                int count,
                                                final int... audioIds) {
        final VKRequest request = VkHelper.getAudioRequest(
                ownerId,
                albumId,
                needUser,
                offset,
                count,
                audioIds);
        return getAudiosByRequest(request);
    }

    public static Audio.AudioList searchAudiosInNet(String query, int offset, int count) {
        VKParameters params = new VKParameters();
        params.put(Constants.Q, query);
        params.put(Constants.VK_AUTO_COMPLETE, 1);
        params.put(Constants.VK_LYRICS, 0);
        params.put(Constants.VK_PERFORMER_ONLY, 0);
        params.put(Constants.SORT, 2);
        params.put(Constants.VK_SEARCH_OWN, 0);
        params.put(Constants.OFFSET, offset);
        params.put(Constants.COUNT, count);
        VKRequest request = new VKRequest(Constants.VK_AUDIO_SEARCH, params);
        return getAudiosByRequest(request);
    }

    public static boolean isSyncWithVk() {
        return TheApp.getApp().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(SYNC_WITH_VK_KEY, false);
    }

    public static void syncWithVk(final MyRequestListener listener) {
        Log.d(TAG, "syncWithVk()");
        VKParameters params = new VKParameters();
        params.put(Constants.OWNER_ID, VKAccessToken.currentToken().userId);
        final VKRequest albumsRequest = new VKRequest(Constants.VK_AUDIOS_GET_ALBUMS, params);
        albumsRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                final Album.AlbumList albums = new Album.AlbumList();
                try {
                    JSONArray items = response.json.getJSONObject("response").getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        Album currentAlbum = new Album(item.getString("title"));
                        currentAlbum.setVkId(Integer.parseInt(item.getString("id")));
                        currentAlbum.setOwnerId(Integer.parseInt(item.getString("owner_id")));
                        currentAlbum.setSynchronizedWithVk(true);
                        albums.add(currentAlbum);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "syncWithVk(), albums info gotten, size=" + albums.size());
                if (albums.size() > 0) {
                    List<VKRequest> albumRequests = new ArrayList<>(albums.size());
                    for (final Album album : albums) {
                        VKRequest request = VkHelper.getAudioRequest(
                                album.getOwnerId(),
                                album.getVkId(),
                                false,
                                0,
                                0
                        );
                        albumRequests.add(request);
                    }
                    VKBatchRequest batchRequest = new VKBatchRequest(albumRequests.toArray(new VKRequest[albumRequests.size()]));
                    batchRequest.executeWithListener(new VKBatchRequest.VKBatchRequestListener() {
                        @Override
                        public void onComplete(VKResponse[] responses) {
                            Log.d(TAG, "syncWithVk(), albums gotten");
                            // save the order of adding
                            for (int i = responses.length - 1; i >= 0; i--) {
                                VKResponse response = responses[i];
                                int totalCount = -1;
                                Audio.AudioList audios = new Audio.AudioList();
                                try {
                                    VkAudioArray vkAudios = (VkAudioArray) new VkAudioArray().parse(response.json);
                                    audios = new Audio.AudioList(vkAudios);
                                    totalCount = response.json
                                            .getJSONObject("response")
                                            .getInt("count");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (totalCount == -1) {
                                    totalCount = audios.size();
                                }
                                audios.setTotalCount(totalCount);
                                albums.get(i).setAudios(audios);
                                albums.get(i).setTotalCount(totalCount);
                                saveAlbum(albums.get(i));
                            }
                            TheApp.getApp().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
                                    .edit()
                                    .putBoolean(SYNC_WITH_VK_KEY, true)
                                    .apply();
                            listener.onComplete(albums);
                        }

                        @Override
                        public void onError(VKError error) {
                            listener.onError(error.toString());
                        }
                    });
                } else {
                    TheApp.getApp().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(SYNC_WITH_VK_KEY, true)
                            .apply();
                    listener.onComplete(albums);
                }
            }

            @Override
            public void onError(VKError error) {
                listener.onError(error.toString());
            }
        });
    }

    public static Album.AlbumList getAlbumListFromNet(int count, int offset) {
        VKParameters params = new VKParameters();
        params.put(Constants.OWNER_ID, VKAccessToken.currentToken().userId);
        params.put(Constants.COUNT, String.valueOf(count));
        params.put(Constants.OFFSET, String.valueOf(offset));
        VKRequest albumsRequest = new VKRequest(Constants.VK_AUDIOS_GET_ALBUMS, params);

        final Album.AlbumList albums = new Album.AlbumList();

        albumsRequest.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    JSONArray items = response.json.getJSONObject("response").getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        Album currentAlbum = new Album(item.getString("title"));
                        currentAlbum.setVkId(Integer.parseInt(item.getString("id")));
                        currentAlbum.setOwnerId(Integer.parseInt(item.getString("owner_id")));
                        currentAlbum.setSynchronizedWithVk(true);
                        albums.add(currentAlbum);
                        saveAlbum(currentAlbum);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if (albums != null) {
            for (Album album : albums) {
                Audio.AudioList audios = getAudiosFromNet(
                        album.getOwnerId(),
                        album.getVkId(),
                        false,
                        0,
                        ModernAudiosLoader.AUDIOS_PER_REQUEST
                );
                if (audios != null) {
                    album.setAudios(audios);
                    int totalCount = audios.getTotalCount();
                    album.setTotalCount(totalCount);
                }
            }
        }
        return albums;
    }

    public static void createEmptyAlbumByTitleAndGetId(String title, VKRequest.VKRequestListener listener) {
        Log.d(TAG, "createEmptyAlbumByTitleAndGetId(), title=" + title);
        VKParameters params = new VKParameters();
        params.put(Constants.VK_ALBUM_TITLE, title);
        VKRequest request = new VKRequest(Constants.VK_AUDIOS_ADD_ALBUM, params);
        request.executeWithListener(listener);
    }

    public static void loadAlbumToNet(Album album, VKRequest.VKRequestListener listener) {
        String audioIds = "";
        Audio.AudioList audios = album.getAudios();
        for (int i = 0; i < audios.size(); i++) {
            Audio audio = audios.get(i);
            if (i != 0) {
                audioIds += ",";
            }
            audioIds += audio.getOwnerId() + "_" + audio.getId();
        }
        Log.d(TAG, "loadAlbumToNet(),album=" + album.toString() + ",ids=" + audioIds);
        VKParameters params = new VKParameters();
        params.put(Constants.VK_ALBUM_ID, album.getVkId());
        params.put(Constants.VK_AUDIO_IDS, audioIds);
        VKRequest request = new VKRequest(Constants.VK_AUDIOS_MOVE_TO_ALBUM, params);
        request.executeWithListener(listener);
    }

    public static void removeAlbumFromNet(Album album, VKRequest.VKRequestListener listener) {
        if (album == null) {
            listener.onError(null);
            return;
        }
        Log.d(TAG, "removeAlbumFromNet(), " + album.toString());
        if (!album.isSynchronizedWithVk()) {
            Log.e(TAG, "Album " + album.toString() + " is not synchronized with vk");
            listener.onError(null);
            return;
        }
        VKParameters params = new VKParameters();
        params.put(Constants.VK_ALBUM_ID, album.getVkId());
        VKRequest request = new VKRequest(Constants.VK_AUDIOS_DELETE_ALBUM, params);
        request.executeWithListener(listener);
    }

    public static void getAudioUrl(Audio audio, final MyRequestListener listener) {
        Log.d(TAG, "getting audio url(async), audio=" + audio.toString());
        VKParameters params = new VKParameters();
        params.put(Constants.VK_AUDIOS, String.valueOf(audio.getOwnerId() + "_" + audio.getId()));
        VKRequest request = new VKRequest(Constants.VK_AUDIO_GET_BY_ID, params);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    String url = response.json.getJSONArray("response").getJSONObject(0).getString("url");
                    listener.onComplete(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError("Parsing error");
                }
            }

            @Override
            public void onError(VKError error) {
                Log.e(TAG, error + error.toString());
                listener.onError(error.toString());
            }
        });
    }

    public static String getAudioUrl(Audio audio) {
        Log.d(TAG, "getting audio url, audio=" + audio.toString());
        VKParameters params = new VKParameters();
        params.put(Constants.VK_AUDIOS, String.valueOf(audio.getOwnerId() + "_" + audio.getId()));
        VKRequest request = new VKRequest(Constants.VK_AUDIO_GET_BY_ID, params);
        final String[] url = new String[1];
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    url[0] = response.json.getJSONArray("response").getJSONObject(0).getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                Log.e(TAG, error + toString());
            }
        });
        return url[0];
    }

    public static void saveAlbum(Album album) {
        Log.d(TAG, "saveAlbum(), " + album);
        SharedPreferences preferences = TheApp.getApp().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        int albumLocalId;
        boolean isNew = false;
        if (album.getLocalId() == -1) {
            albumLocalId = preferences.getInt(ALBUM_COUNT_KEY, 0) + 1;
            album.setLocalId(albumLocalId);
            isNew = true;
        } else {
            albumLocalId = album.getLocalId();
        }
        String jsonAlbum = TheApp.getApp().getGson().toJson(album);
        SharedPreferences.Editor editor = preferences.edit();
        if (isNew) {
            editor.putInt(ALBUM_COUNT_KEY, albumLocalId);
        }
        editor.putString(ALBUM_KEY + "_" + albumLocalId, jsonAlbum);
        editor.apply();
    }

    public static Album.AlbumList getAlbumList() {
        Log.d(TAG, "getAlbumList()");
        SharedPreferences preferences = TheApp.getApp().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        int count = preferences.getInt(ALBUM_COUNT_KEY, 0);
        Album.AlbumList albums = new Album.AlbumList();
        for (int i = count; i >= 1; i--) {
            String jsonAlbum = preferences.getString(ALBUM_KEY + "_" + i, null);
            if (jsonAlbum != null) {
                albums.add(TheApp.getGson().fromJson(jsonAlbum, Album.class));
            }
        }
        return albums;
    }

    public static void removeAlbum(Album album) {
        Log.d(TAG, "removeAlbum(), " + album.toString());
        album.clear();
        SharedPreferences preferences = TheApp.getApp().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        int localId = album.getLocalId();
        if (localId == -1) {
            throw new IllegalArgumentException("Album does not have localId. " + album.toString());
        }
        preferences.edit()
                .remove(ALBUM_KEY + "_" + localId)
                .apply();
    }
}
