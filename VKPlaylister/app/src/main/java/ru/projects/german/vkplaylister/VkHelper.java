package ru.projects.german.vkplaylister;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

/**
 * Created on 18.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class VkHelper {
    public static VKRequest getAudioRequest(int owner_id,
                                             int album_id,
                                             boolean needUser,
                                             int offset,
                                             int count,
                                             int... audioIds) {
        VKParameters params = new VKParameters();
        if (owner_id > 0) {
            params.put(VKApiConst.OWNER_ID, owner_id);
        }
        if (album_id > 0) {
            params.put(VKApiConst.ALBUM_ID, album_id);
        }
        params.put("need_user", needUser ? 1 : 0);
        if (offset > 0) {
            params.put(VKApiConst.OFFSET, offset);
        }
        if (count > 0) {
            params.put(VKApiConst.COUNT, count);
        }
        if (audioIds.length > 0) {
            String audioIdsAsString = "";
            for (int i = 0; i < audioIds.length; i++) {
                audioIdsAsString += Integer.toString(audioIds[i]);
                if (i != audioIds.length - 1) {
                    audioIdsAsString += ",";
                }
            }
            params.put("audio_ids", audioIdsAsString);
        }
        VKRequest request = new VKRequest(Constants.VK_AUDIO_GET, params);
        return request;
    }

    public static int getVkObjectHash(int ownerId, int id, Class objClass) {
        String s = objClass.getSimpleName() + "_" + ownerId + "_" + id;
        return s.hashCode();
    }
}
