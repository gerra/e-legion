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

import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 26.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class PlayerHelper implements ServiceConnection {
    private static final String TAG = PlayerHelper.class.getSimpleName();

    public static final int REGISTER_HELPER = 5;

    public static final int PLAY_MESSAGE = 1;
    public static final int RESUME_MESSAGE = 2;
    public static final int PAUSE_MESSAGE = 3;
    public static final int STOP_MESSAGE = 4;

    public static final int COMPLETE_MESSAGE = 5;

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ORDER_KEY = "ORDER_KEY";
    public static final String POSITION_TO_PLAY_KEY = "POSITION_TO_PLAY_KEY";
    public static final String AUDIO_TO_PLAY_KEY = "AUDIO_TO_PLAY_KEY";

    private Handler mIncomingHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE_MESSAGE:
                    playNext();
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
        isPlaying = true;
        try {
            mService.send(Message.obtain(null, PLAY_MESSAGE, audio));
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send play message to service, " + e.getMessage());
        }
    }

    public void play(Audio.AudioList order, int positionToPlay) {
        this.order = order;
        currentPlayPosition = positionToPlay;
        play(order.get(positionToPlay));
    }

    public void playNext() {
        currentPlayPosition++;
        if (currentPlayPosition > order.size()) {
            currentPlayPosition = 0;
        }
        play(order.get(currentPlayPosition));
    }

    public void resume() {
        isPlaying = true;
        try {
            mService.send(Message.obtain(null, RESUME_MESSAGE));
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send resume message to service, " + e.getMessage());
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            mService.send(Message.obtain(null, PAUSE_MESSAGE));
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
}
