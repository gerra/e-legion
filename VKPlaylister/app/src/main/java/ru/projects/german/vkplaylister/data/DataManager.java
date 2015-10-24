package ru.projects.german.vkplaylister.data;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VkAudioArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.projects.german.vkplaylister.Constants;
import ru.projects.german.vkplaylister.VkHelper;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();

    public static VkAudioArray getAudiosFromNet(int owner_id,
                                                int album_id,
                                                boolean needUser,
                                                int offset,
                                                int count,
                                                int... audioIds) {
        VKRequest request = VkHelper.getAudioRequest(
                owner_id,
                album_id,
                needUser,
                offset,
                count,
                audioIds);
        final VkAudioArray[] audios = new VkAudioArray[1];
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    audios[0] = (VkAudioArray) new VkAudioArray().parse(response.json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return audios[0];
    }

    public static Album.AlbumList getAlbumList() {
        VKParameters params = new VKParameters();
        params.put(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId);
        params.put(VKApiConst.COUNT, "10");
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
                VkAudioArray audios = getAudiosFromNet(
                        album.getVkOwnerId(),
                        album.getVkId(),
                        false,
                        0,
                        1
                );
                if (audios != null) {
                    album.setAudios(new Audio.AudioList(audios));
                    album.setTotalCount(audios.getCount());
                }
            }
        }
        return albums;
    }

    public static void uploadAlbum(Album album) {
        
    }

    public static void createAlbumByTitleAndGetId(String title, VKRequest.VKRequestListener listener) {
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
        params.put(Constants.VK_ALBUM_AUDIO_IDS, audioIds);
        VKRequest request = new VKRequest(Constants.VK_AUDIOS_MOVE_TO_ALBUM, params);
        request.executeWithListener(listener);
    }
}
