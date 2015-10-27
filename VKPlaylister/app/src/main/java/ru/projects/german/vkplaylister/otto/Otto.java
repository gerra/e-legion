package ru.projects.german.vkplaylister.otto;

import android.util.Log;

import com.squareup.otto.Bus;

/**
 * Created on 25.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class Otto {
    private static String TAG = Otto.class.getSimpleName();

    private static Bus BUS = new Bus();

    public static void post(Object event) {
        Log.d(TAG, "post: " + event.toString());
        BUS.post(event);
    }

    public static void register(Object handler) {
        Log.d(TAG, "register " + handler.toString());
        BUS.register(handler);
    }

    public static void unregister(Object handler) {
        Log.d(TAG, "unregister " + handler.toString());
        BUS.unregister(handler);
    }
}
