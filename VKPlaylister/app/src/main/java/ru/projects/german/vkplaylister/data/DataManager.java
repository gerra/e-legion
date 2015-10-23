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

import java.util.Set;

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
        VKRequest albumsRequest = new VKRequest(Constants.VK_ALBUMS_GET, params);

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

        for (Album album : albums) {
            VkAudioArray audios = getAudiosFromNet(
                    album.getVkOwnerId(),
                    album.getVkId(),
                    false,
                    0,
                    1
            );
            album.setAudios(new Audio.AudioList(audios));
            album.setTotalCount(audios.getCount());
        }
        return albums;
    }

    public static Album createAlbum(String title, Set<Audio> audios) {
        return new Album(title, new Audio.AudioList(audios));
    }
}
