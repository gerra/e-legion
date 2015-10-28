package ru.projects.german.vkplaylister.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 26.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class PlayerHelper implements ServiceConnection {
    private static final String TAG = PlayerHelper.class.getSimpleName();

    public static final int PLAY_MESSAGE   = 1;
    public static final int RESUME_MESSAGE = 2;
    public static final int PAUSE_MESSAGE  = 3;
    public static final int STOP_MESSAGE   = 4;
    public static final int PLAY_NEXT      = 5;
    public static final int PLAY_PREV      = 6;
    public static final int PLAY_PAUSE     = 7;

    public static final int REGISTER_HELPER = 8;

    public static final String ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE";
    public static final String ACTION_PREV = "ACTION_PREV";
    public static final String ACTION_NEXT = "ACTION_NEXT";

    public static final String ORDER_KEY = "ORDER_KEY";
    public static final String POSITION_TO_PLAY_KEY = "POSITION_TO_PLAY_KEY";
    public static final String AUDIO_TO_PLAY_KEY = "AUDIO_TO_PLAY_KEY";

    private Handler mIncomingHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_NEXT:
                    playNext();
                    break;
                case PLAY_PREV:
                    playPrev();
                    break;
                case PLAY_PAUSE:
                    boolean oldState = isPlaying;
                    isPlaying = (boolean) msg.obj;
                    if (oldState != isPlaying) {
                        if (isPlaying) {
                            onPlay();
                        } else {
                            onStop();
                        }
                    }
                    break;
                default:
                    Log.e(TAG, "Unexpected message: " + msg.toString());
            }
            return true;
        }
    });
    private Messenger mMessenger = new Messenger(mIncomingHandler);

    private Context context;
    private Messenger mService;

    private int currentPlayPosition;
    private Audio.AudioList order;
    private Album album;
    private boolean isPlaying;

    private void bindService() {
        Intent service = new Intent(context, PlayerService.class);
        context.bindService(service, this, Context.BIND_IMPORTANT | Context.BIND_AUTO_CREATE);
    }

    public PlayerHelper(Context context) {
        this.context = context;
        bindService();
    }

    public Audio getCurrentAudio() {
        if (order != null) {
            return order.get(currentPlayPosition);
        }
        return null;
    }

    public Audio getCurrentPlayingAudio() {
        if (isPlaying) {
            return getCurrentAudio();
        }
        return null;
    }

    private void play(Audio audio) {
        try {
            mService.send(Message.obtain(null, PLAY_MESSAGE, audio));
            isPlaying = true;
            onPlay();
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send play message to service, " + e.getMessage());
        }
    }

    public void play(Album album) {
        if (album.equals(this.album)) {
            resume();
        } else {
            play(album.getAudios(), 0, album);
        }
    }

    public void play(Audio.AudioList order, int positionToPlay, Album album) {
        this.order = order;
        this.album = album;
        currentPlayPosition = positionToPlay;
        play(order.get(positionToPlay));
    }

    public void playNext() {
        Log.d(TAG, "playNext()");
        currentPlayPosition++;
        if (currentPlayPosition >= order.size()) {
            currentPlayPosition = 0;
        }
        play(order.get(currentPlayPosition));
    }

    public void playPrev() {
        currentPlayPosition--;
        if (currentPlayPosition < 0) {
            currentPlayPosition = order.size() - 1;
        }
        play(order.get(currentPlayPosition));
    }

    public void resume() {
        try {
            mService.send(Message.obtain(null, RESUME_MESSAGE));
            isPlaying = true;
            onPlay();
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send resume message to service, " + e.getMessage());
        }
    }

    public void pause() {
        try {
            mService.send(Message.obtain(null, PAUSE_MESSAGE));
            isPlaying = false;
            onStop();
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send pause message to service, " + e.getMessage());
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        mService = new Messenger(service);
        Message msg = Message.obtain(null, REGISTER_HELPER);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send register_helper message to service, " + e.getMessage());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        mService = null;
        bindService();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    private void onPlay() {
        for (PlayerListener listener : listeners) {
            listener.onPlay(album, order.get(currentPlayPosition));
        }
    }

    private void onStop() {
        for (PlayerListener listener : listeners) {
            listener.onStop(album, order.get(currentPlayPosition));
        }
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public interface PlayerListener {
        void onPlay(Album album, Audio audio);
        void onStop(Album album, Audio audio);
    }

    private List<PlayerListener> listeners = new ArrayList<>();

    public void registerListener(PlayerListener listener) {
        listeners.add(listener);
        if (isPlaying && order != null && order.get(currentPlayPosition) != null) {
            listener.onPlay(album, order.get(currentPlayPosition));
        }
        if (!isPlaying && order != null && order.get(currentPlayPosition) != null) {
            listener.onStop(album, order.get(currentPlayPosition));
        }
    }

    public void unregisterListener(PlayerListener listener) {
        listeners.remove(listener);
    }
}
