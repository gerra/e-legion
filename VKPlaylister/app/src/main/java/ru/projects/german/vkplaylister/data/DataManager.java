package ru.projects.german.vkplaylister.data;

import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VkAudioArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.projects.german.vkplaylister.Constants;
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
    private static final String TAG = DataManager.class.getSimpleName();

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

    public static Album.AlbumList getAlbumList(int count, int offset) {
        VKParameters params = new VKParameters();
        params.put(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId);
        params.put(VKApiConst.COUNT, String.valueOf(count));
        params.put(VKApiConst.OFFSET, String.valueOf(offset));
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
                        currentAlbum.setVkOwnerId(Integer.parseInt(item.getString("owner_id")));
                        currentAlbum.setSynchronizedWithVk(true);
                        albums.add(currentAlbum);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if (albums != null) {
            for (Album album : albums) {
                Audio.AudioList audios = getAudiosFromNet(
                        album.getVkOwnerId(),
                        album.getVkId(),
                        false,
                        0,
                        ModernAudiosLoader.AUDIOS_PER_REQUEST
                );
                if (audios != null) {
                    album.setAudios(audios);
                    int totalCount = audios.getTotalCount();
                    if (totalCount != -1) {
                        album.setTotalCount(totalCount);
                    } else {
                        album.setTotalCount(audios.size());
                    }
                }
            }
        }
        return albums;
    }

    public static void createEmptyAlbumByTitleAndGetId(String title, VKRequest.VKRequestListener listener) {
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
            audioIds += audio.getId();
        }
        VKParameters params = new VKParameters();
        params.put(Constants.VK_ALBUM_ID, album.getVkId());
        params.put(Constants.VK_AUDIO_IDS, audioIds);
        VKRequest request = new VKRequest(Constants.VK_AUDIOS_MOVE_TO_ALBUM, params);
        request.executeWithListener(listener);
    }

    public static void removeAlbumFromNet(Album album, VKRequest.VKRequestListener listener) {
        if (!album.isSynchronizedWithVk()) {
            Log.e(TAG, "Album " + album.toString() + " is not synchronized with vk");
            return;
        }
        if (album == null) {
            return;
        }
        VKParameters params = new VKParameters();
        params.put(Constants.VK_ALBUM_ID, album.getVkId());
        VKRequest request = new VKRequest(Constants.VK_AUDIOS_DELETE_ALBUM, params);
        request.executeWithListener(listener);
    }

    public static String getAudioUrl(Audio audio) {
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
}
